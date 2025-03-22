package com.jerson.soundeyes.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun CardLog(
    modifier: Modifier = Modifier,
    timeReceive:String,
    timeClassifier:String,
    sizeImage:String
) {
    Card(
        modifier = modifier.padding(10.dp)
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
    ){
      Column(
          modifier = Modifier.padding(20.dp)
      ) {
          Text(text = "Dados:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
          Text(text = buildAnnotatedString {
              append("Recebimento de Imagem:")
              withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                  append(timeReceive)
              }
          })
          Text(text = buildAnnotatedString {
              append("Classificação da Imagem:")
              withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                  append(timeClassifier)
              }
          })
          Text(text = buildAnnotatedString {
              append("Tamanho da Imagem:")
              withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                  append(sizeImage)
              }
          })
      }
    }
}