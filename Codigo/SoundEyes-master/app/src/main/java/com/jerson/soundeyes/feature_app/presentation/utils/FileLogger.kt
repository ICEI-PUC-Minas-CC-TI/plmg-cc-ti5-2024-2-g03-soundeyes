package com.jerson.soundeyes.feature_app.presentation.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException

object FileLogger {

    private const val TAG = "FileLogger"

    fun logToFile(context: Context, tag: String, message: String) {
        val logMessage = "${System.currentTimeMillis()}: [$tag] $message\n"
        try {
            // Define o arquivo no diretório de arquivos da aplicação
            val logFile = File(context.filesDir, "app_logs.txt")

            // Grava a mensagem no arquivo
            FileWriter(logFile, true).use { writer ->
                writer.append(logMessage)
            }

            // Ainda imprime no Logcat, se necessário
            Log.d(tag, message)
        } catch (e: IOException) {
            Log.e(TAG, "Erro ao gravar log no arquivo", e)
        }
    }
}
