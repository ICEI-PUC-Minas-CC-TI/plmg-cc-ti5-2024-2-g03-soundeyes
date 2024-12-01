package com.jerson.soundeyes.feature_app.data.api

import android.content.Context
import android.graphics.*

import android.util.Log
import com.jerson.soundeyes.feature_app.consts.Const.CONFIDENCE_THRESHOLD
import com.jerson.soundeyes.feature_app.consts.Const.INPUT_IMAGE_TYPE
import com.jerson.soundeyes.feature_app.consts.Const.INPUT_MEAN
import com.jerson.soundeyes.feature_app.consts.Const.INPUT_STANDARD_DEVIATION
import com.jerson.soundeyes.feature_app.consts.Const.IOU_THRESHOLD
import com.jerson.soundeyes.feature_app.consts.Const.LABELS_YOLO
import com.jerson.soundeyes.feature_app.consts.Const.OUTPUT_IMAGE_TYPE
import com.jerson.soundeyes.feature_app.consts.Const.TFLITE_YOLO
import com.jerson.soundeyes.feature_app.domain.model.BoundingBox
import com.jerson.soundeyes.feature_app.presentation.utils.TextToSpeechManager
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.nnapi.NnApiDelegate

import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

//TODO: liberar dados do interpreter
//TODO: Fazer os devidos tratamentos
object YOLOClassifier {
    private lateinit var interpreter: Interpreter
    private lateinit var appContext: Context
    private lateinit var labels:List<String>
    private lateinit var gpuDelegate: GpuDelegate

    private var tensorWidth = 0
    private var tensorHeight = 0
    private var numChannel = 0
    private var numElements = 0
    private val imageProcessor = ImageProcessor.Builder()
        .add(NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
        .add(CastOp(INPUT_IMAGE_TYPE))
        .build()

    fun init(context: Context) {
        appContext = context.applicationContext // Armazena o contexto da aplicação
        if (!YOLOClassifier::interpreter.isInitialized) {
            try {
                gpuDelegate = GpuDelegate()
                val option = Interpreter.Options().apply {
                    addDelegate(gpuDelegate)
                }
                interpreter = Interpreter(loadModelFile(),option)
            }catch (e: Exception){
                gpuDelegate.close()
                Log.d("Interpreter", "Não foi possivel inicializar com GPU, tentando com CPU")
                val option = Interpreter.Options().apply {
                    numThreads = 4
                }
                interpreter = Interpreter(loadModelFile(),option)
            }

            val inputShape = interpreter.getInputTensor(0)?.shape() ?: return
            val outputShape = interpreter.getOutputTensor(0)?.shape() ?: return

            tensorWidth = inputShape[1]
            tensorHeight = inputShape[2]
            numChannel = outputShape[1]
            numElements = outputShape[2]
            carregaLabels()
        }

    }

    private fun carregaLabels() {
        val txtLabels: InputStream = appContext.assets.open(LABELS_YOLO)
        val bufferReader = InputStreamReader(txtLabels)
        try {
            labels = bufferReader.readLines()
        }catch (e: Exception){
            Log.d("Labels","Erro na leitura do arquivo")
        }
    }

    fun close() {
        if (::interpreter.isInitialized) {
            interpreter.close() // Libere os recursos do Interpreter
        }
        if(::gpuDelegate.isInitialized){
            gpuDelegate.close()
        }
    }
    // Método para carregar o arquivo TFLite do diretório assets
    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = appContext.assets.openFd(TFLITE_YOLO) // Altere para o nome do seu modelo YOLOv5
        FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }

    // Método para realizar a predição a partir de uma imagem Bitmap
    fun classifyImage(frame: Bitmap): List<BoundingBox>? {

        val resizedBitmap = Bitmap.createScaledBitmap(frame, tensorWidth, tensorHeight, false)

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resizedBitmap)
        val processedImage = imageProcessor.process(tensorImage)
        val imageBuffer = processedImage.buffer

        val output =
            TensorBuffer.createFixedSize(intArrayOf(1, numChannel, numElements), OUTPUT_IMAGE_TYPE)

        try {
            interpreter.run(imageBuffer, output.buffer)
        } catch (e: Exception) {
            Log.e("Error Image", e.message.toString())
            e.printStackTrace()
        }
        val bestBoxes = bestBox(output.floatArray, resizedBitmap.width * resizedBitmap.height)
        if(bestBoxes != null){
            val text = logPositionInImage(bestBoxes, resizedBitmap.width)
            if (text != ""){
                Thread.sleep(100)
                TextToSpeechManager.convert(text)
            }
        }
        return bestBoxes
    }
    private fun bestBox(array: FloatArray, imageArea: Int) : List<BoundingBox>? {
        val boundingBoxes = mutableListOf<BoundingBox>()

        for (c in 0 until numElements) {
            var maxConf = -1.0f
            var maxIdx = -1
            var j = 4
            var arrayIdx = c + numElements * j
            while (j < numChannel) {
                if (array[arrayIdx] > maxConf) {
                    maxConf = array[arrayIdx]
                    maxIdx = j - 4
                }
                j++
                arrayIdx += numElements
            }

            if (maxConf > CONFIDENCE_THRESHOLD) {
                val clsName = labels[maxIdx]
                val cx = array[c] // 0
                val cy = array[c + numElements] // 1
                val w = array[c + numElements * 2]
                val h = array[c + numElements * 3]
                val x1 = cx - (w / 2F)
                val y1 = cy - (h / 2F)
                val x2 = cx + (w / 2F)
                val y2 = cy + (h / 2F)

                // Filtrar caixas fora dos limites da imagem
                if (x1 < 0F || x1 > 1F) continue
                if (y1 < 0F || y1 > 1F) continue
                if (x2 < 0F || x2 > 1F) continue
                if (y2 < 0F || y2 > 1F) continue

                // Calcular a área da caixa e filtrar caixas pequenas demais
                val area = w * h * imageArea
                if (area < imageArea * 0.03f) continue // Ignorar caixas muito pequenas

                // Determine a célula da grade onde o objeto está localizado
                val (row, col) = getGridPosition(cx, cy)

                boundingBoxes.add(
                    BoundingBox(
                        x1 = x1, y1 = y1, x2 = x2, y2 = y2,
                        cx = cx, cy = cy, w = w, h = h,
                        cnf = maxConf, cls = maxIdx, clsName = clsName,
                        gridRow = row, gridCol = col // Armazena a posição na grade
                    )
                )
            }
        }

        if (boundingBoxes.isEmpty()) return null

        return applyNMS(boundingBoxes)
    }

    private fun applyNMS(boxes: List<BoundingBox>) : MutableList<BoundingBox> {
        val sortedBoxes = boxes.sortedByDescending { it.cnf }.toMutableList()
        val selectedBoxes = mutableListOf<BoundingBox>()

        while(sortedBoxes.isNotEmpty()) {
            val first = sortedBoxes.first()
            selectedBoxes.add(first)
            sortedBoxes.remove(first)

            val iterator = sortedBoxes.iterator()
            while (iterator.hasNext()) {
                val nextBox = iterator.next()
                val iou = calculateIoU(first, nextBox)
                if (iou >= IOU_THRESHOLD) {
                    iterator.remove()
                }
            }
        }

        return selectedBoxes
    }

    private fun calculateIoU(box1: BoundingBox, box2: BoundingBox): Float {
        val x1 = maxOf(box1.x1, box2.x1)
        val y1 = maxOf(box1.y1, box2.y1)
        val x2 = minOf(box1.x2, box2.x2)
        val y2 = minOf(box1.y2, box2.y2)
        val intersectionArea = maxOf(0F, x2 - x1) * maxOf(0F, y2 - y1)
        val box1Area = box1.w * box1.h
        val box2Area = box2.w * box2.h
        return intersectionArea / (box1Area + box2Area - intersectionArea)
    }

    private fun getGridPosition(cx: Float, cy: Float): Pair<Int, Int> {
        // Dividimos a imagem em 3x3, então cada célula ocupa 1/3 da área
        val gridSize = 1f / 3f

        // Calcula a posição da célula da grade com base nas coordenadas cx e cy
        val row = (cy / gridSize).toInt().coerceIn(0, 2)
        val col = (cx / gridSize).toInt().coerceIn(0, 2)

        return Pair(row, col)
    }
    private fun logPositionInImage(boundingBoxes: List<BoundingBox>, imageWidth: Int): String {
        var textToSpeech: String = ""

        boundingBoxes.forEach { box ->
            // Calcular o centro do bounding box em coordenadas reais (bitmap)
            val cx = (box.cx * imageWidth) // Convertendo para coordenadas reais

            val col = (cx / (imageWidth / 3f)).toInt() // Coluna (0, 1, 2)

            val posicao = getPosition(col)
            if (posicao != "") {
                textToSpeech = "${box.clsName} detectado $posicao"
                println(textToSpeech)
            }
        }

        return textToSpeech
    }

    private fun getPosition(col: Int): String {
        return when (col) {
            0 -> "a esquerda"
            1 -> "a frente"
            2 -> "a direita"
            else -> ""
        }
    }



}
