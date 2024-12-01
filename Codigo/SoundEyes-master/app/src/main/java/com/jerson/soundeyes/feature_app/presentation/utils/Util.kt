package com.jerson.soundeyes.feature_app.presentation.utils

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext

object Util {

    fun loadLabels(@ApplicationContext context: Context, arqtxt: String): List<String>{
        return try {
            context.assets.open(arqtxt).bufferedReader().use { read ->
                read.readLines()
            }
        }catch (e: Exception){
            Log.d("Labels", e.message.toString())
            emptyList()
        }
    }

}