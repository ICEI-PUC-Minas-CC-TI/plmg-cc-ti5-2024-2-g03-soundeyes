package com.jerson.soundeyes.consts

import org.tensorflow.lite.DataType

object Const {
    const val LABELS_YOLO = "labelsYolo.txt"
    const val TFLITE_YOLO = "yolov8n_float32.tflite"
    const val INPUT_MEAN = 0f
    const val INPUT_STANDARD_DEVIATION = 255f
    val INPUT_IMAGE_TYPE = DataType.FLOAT32
    val OUTPUT_IMAGE_TYPE = DataType.FLOAT32
    const val CONFIDENCE_THRESHOLD = 0.50F
    const val IOU_THRESHOLD = 0.5F
    const val DESEMPENHO = "Desempenho"
    const val QUALIDADE = "Qualidade"
    const val CAMERA_BLUETOOTH = "ESP32CAM"
    const val CAMERA_DISPOSITIVO = "Câmera do dispositivo"
    const val FILE_CAMERA_BLUETOOTH = "dado_log_bluetooth"
    const val FILE_CAMERA_CLASSIFIER = "dado_log_image"
}