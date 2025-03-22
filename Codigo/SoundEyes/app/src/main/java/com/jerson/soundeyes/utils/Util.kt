package com.jerson.soundeyes.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import com.jerson.soundeyes.domain.model.BoundingBox
import dagger.hilt.android.qualifiers.ApplicationContext

object Util {

    fun drawBoundingBoxesOnBitmap(original: Bitmap, boxes: List<BoundingBox>): Bitmap {
        val mutableBitmap = original.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        // Configuração do Paint para as caixas
        val boxPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }

        // Configuração do Paint para o texto (rótulos)
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            style = Paint.Style.FILL
        }

        val backgroundPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        boxes.forEach { box ->
            val left = box.x1 * mutableBitmap.width
            val top = box.y1 * mutableBitmap.height
            val right = box.x2 * mutableBitmap.width
            val bottom = box.y2 * mutableBitmap.height

            // Desenha o retângulo da bounding box
            canvas.drawRect(left, top, right, bottom, boxPaint)

            // Texto do rótulo
            val label = "${box.clsName} (${(box.cnf * 100).toInt()}%)"
            val textWidth = textPaint.measureText(label)
            val textHeight = textPaint.textSize

            // Fundo do texto (retângulo vermelho atrás do rótulo)
            canvas.drawRect(left, top - textHeight, left + textWidth, top, backgroundPaint)

            // Desenha o texto (rótulo)
            canvas.drawText(label, left, top - 5f, textPaint)
        }

        return mutableBitmap
    }


}