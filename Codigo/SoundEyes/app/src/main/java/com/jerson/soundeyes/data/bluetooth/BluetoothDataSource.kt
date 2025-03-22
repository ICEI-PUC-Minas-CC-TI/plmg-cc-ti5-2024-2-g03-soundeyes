package com.jerson.soundeyes.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
class BluetoothDataSource(private val context: Context) {

    private val UUID_SERIAL: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // Função para obter o endereço MAC do dispositivo emparelhado pelo nome
    private fun getPairedDeviceAddress(deviceName: String = "ESP32-CAM"): String? {
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


    suspend fun sendCameraConfig(value: Int, callback : (Boolean) -> Unit) {
        var socket: BluetoothSocket? = null
        val outputStream: OutputStream?
        var resp: Boolean
        try {
            val deviceAddress = "2C:BC:BB:84:5C:AA"
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)

            // Verificar permissão de conexão Bluetooth

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
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
                    withContext(Dispatchers.IO) {
                        recebido = inputStream.read()
                        resp = recebido == 1;
                        callback(resp)
                    }
                }
            }else{
                callback(false)
            }

        } catch (e: IOException) {
            Log.d("Bluetooth", "Erro ao conectar ou enviar dados: ${e.message}")
            callback(false)
            e.printStackTrace()
        } finally {
            try {
                socket?.close()
            } catch (e: IOException) {
                Log.d("Bluetooth", "Erro ao fechar o socket ou OutputStream: ${e.message}")
            }
        }
    }
    suspend fun receiveImageFromESP32(maxRetries: Int = 3): Bitmap? {
        var attempt = 0
        var image: Bitmap? = null

        while (attempt < maxRetries && image == null) {
            try {
                image = withContext(Dispatchers.IO) {
                    var socket: BluetoothSocket? = null
                    try {
                        val deviceAddress = "2C:BC:BB:84:5C:AA"
                        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                        val bluetoothAdapter = bluetoothManager.adapter
                        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            return@withContext null
                        }

                        // Cria e conecta o socket Bluetooth
                        socket = device.createRfcommSocketToServiceRecord(UUID_SERIAL)
                        socket.connect()

                        val inputStream: InputStream = socket.inputStream
                        val outputStream: OutputStream = socket.outputStream

                        // Envia a flag para que o ESP32 inicie o envio
                        val startCaptureFlag = byteArrayOf(0x01)
                        outputStream.write(startCaptureFlag)
                        outputStream.flush()
                        Log.d("Bluetooth", "passei aqui")
                        val buffer = ByteArray(40000)
                        val imageBuffer = ByteArrayOutputStream()
                        var endSequenceDetected = false

                        while (!endSequenceDetected) {

                            val bytesRead = try {
                                withTimeout(3000) {
                                    inputStream.read(buffer)
                                }
                            } catch (e: TimeoutCancellationException) {
                                throw IOException("Timeout na leitura da imagem")
                            }

                            if (bytesRead == -1) throw IOException("Conexão perdida durante a leitura da imagem")

                            // Verifica se chegou ao final da imagem (sequência de finalizadores)
                            if (bytesRead >= 4 &&
                                buffer[bytesRead - 4] == 0xFF.toByte() &&
                                buffer[bytesRead - 3] == 0xD9.toByte() &&
                                buffer[bytesRead - 2] == 0xFF.toByte() &&
                                buffer[bytesRead - 1] == 0xD9.toByte()
                            ) {
                                endSequenceDetected = true
                                imageBuffer.write(buffer, 0, bytesRead - 4)
                            } else {
                                imageBuffer.write(buffer, 0, bytesRead)
                            }
                        }

                        val imageData = imageBuffer.toByteArray()
                        BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    } finally {
                        try {
                            socket?.close()
                        } catch (e: Exception) {
                            Log.d("Bluetooth", "Erro no fechamento do socket! ${e.message}")
                        }
                    }
                }
            } catch (e: IOException) {
                // Aqui você pode logar o erro e fazer o retry
                Log.d("Bluetooth", "Tentativa ${attempt + 1} falhou: ${e.message}")
                // Aguarda um pequeno delay antes de tentar novamente
                delay(500)
            }
            attempt++
        }
        return image
    }


    fun isBluetoothEnabled(): Boolean {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
        return bluetoothAdapter?.isEnabled == true
    }

}