import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.ActivityCompat
import com.jerson.soundeyes.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

object ConectionBluetooth {

    val UUID_SERIAL = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // Função para obter o endereço MAC do dispositivo emparelhado pelo nome
    private fun getPairedDeviceAddress(context: Context, deviceName: String = "ESP32-CAM"): String? {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter ?: return null
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Informa que a permissão é necessária
            return null
        }
        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
        for (device in pairedDevices) {
            if (device.name == deviceName) {
                return device.address
            }
        }
        return null
    }
    // Função principal para receber a imagem
    suspend fun receiveImageFromESP32(context: Context): Bitmap? {
        return withContext(Dispatchers.IO) {
            var socket: BluetoothSocket? = null
            try {
                val deviceAddress = "2C:BC:BB:84:5C:AA" //getPairedDeviceAddress(context)
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                val bluetoothAdapter = bluetoothManager.adapter ?: return@withContext null
                val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return@withContext null
                }

                // Cria e conecta o socket Bluetooth
                socket = device.createRfcommSocketToServiceRecord(UUID_SERIAL)
                socket.connect()

                val inputStream: InputStream = socket.inputStream ?: return@withContext null
                val outputStream: OutputStream = socket.outputStream ?: return@withContext null

                // Envia a flag de confirmação ao ESP32 antes de receber a imagem
                val stopCaptureFlag = byteArrayOf(0x01)
                outputStream.write(stopCaptureFlag)

                // Buffer temporário e buffer completo para a imagem
                val buffer = ByteArray(20480)
                val imageBuffer = ByteArrayOutputStream()
                var endSequenceDetected = false

                while (!endSequenceDetected) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) throw IOException("Conexão perdida durante a leitura da imagem")

                    // Verifica se chegamos ao final da imagem (sequência de bytes finalizadores)
                    if (bytesRead >= 4 && buffer[bytesRead - 4] == 0xFF.toByte() && buffer[bytesRead - 3] == 0xD9.toByte() &&
                        buffer[bytesRead - 2] == 0xFF.toByte() && buffer[bytesRead - 1] == 0xD9.toByte()
                    ) {
                        endSequenceDetected = true
                        imageBuffer.write(buffer, 0, bytesRead - 4)
                    } else {
                        imageBuffer.write(buffer, 0, bytesRead)
                    }
                }



                // Decodifica o buffer completo para um Bitmap
                val imageData = imageBuffer.toByteArray()
                BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            } catch (e: Exception) {
                e.message?.let { Log.d("Camera", it) }
                e.printStackTrace()
                null
            } finally {
                try {
                    socket?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    suspend fun simulateReceiveImageFromDrawable(context: Context): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                // Carrega a imagem do drawable
                val drawableId = R.drawable.image_example2// Substitua pelo nome real da imagem no drawable
                val inputStream = context.resources.openRawResource(drawableId)
                delay(100)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    /*suspend fun receiveImageFromESP32(context: Context): Bitmap?{
        return BitmapFactory.decodeResource(context.resources,R.drawable.imagem_teste)
    }*/
    suspend fun envioConfigCamera(context: Context, value: Int, callback : (Boolean) -> Unit) {
        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null
        var resp : Boolean = false
        try {
            val deviceAddress = "2C:BC:BB:84:5C:AA"
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)

            // Verificar permissão de conexão Bluetooth
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Bluetooth", "Permissão de conexão Bluetooth não concedida.")
                callback(false)
            }

            // Criar e conectar o socket Bluetooth
            socket = device.createRfcommSocketToServiceRecord(UUID_SERIAL)
            withContext(Dispatchers.IO) {
                socket.connect()
            }
            Log.d("Bluetooth", "Conexão com o dispositivo estabelecida.")

            // Obter o OutputStream do socket para enviar dados
            outputStream = socket.outputStream

            // Enviar o valor booleano como 1 ou 0
            withContext(Dispatchers.IO) {
                outputStream.write(value)
                outputStream.flush()
            }
            val inputStream: InputStream? = socket.inputStream
            var recebido: Int = 0
            if (inputStream != null) {
                withContext(Dispatchers.Main) {
                    recebido = inputStream.read()
                    resp = recebido == 1;
                    callback(resp)
                }
            }
            socket?.close()
        } catch (e: IOException) {
            Log.d("Bluetooth", "Erro ao conectar ou enviar dados: ${e.message}")
            e.printStackTrace()
        } finally {
            // Fechar o OutputStream e o socket após o envio
            try {
                outputStream?.close()
                socket?.close()
                callback(false)
            } catch (e: IOException) {
                Log.d("Bluetooth", "Erro ao fechar o socket ou OutputStream: ${e.message}")
            }
        }
    }

}