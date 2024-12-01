package com.jerson.soundeyes.feature_app.consts

import org.tensorflow.lite.DataType

object Const {
    const val LABELS_YOLO = "labelsYolo.txt"
    const val TFLITE_YOLO = "yolov8n_float16.tflite"
    const val INPUT_MEAN = 0f
    const val INPUT_STANDARD_DEVIATION = 255f
    val INPUT_IMAGE_TYPE = DataType.FLOAT32
    val OUTPUT_IMAGE_TYPE = DataType.FLOAT32
   const val CONFIDENCE_THRESHOLD = 0.25F
   const val IOU_THRESHOLD = 0.5F
}