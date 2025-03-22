package com.jerson.soundeyes.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottonSheetLog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    timeClassifier: String,
    sizeImage: String
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {
            Text(text = "Dados:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(5.dp))
            Text(text = buildAnnotatedString {
                append("Classificação da Imagem:")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(timeClassifier)
                }
            })
            Spacer(Modifier.height(5.dp))
            Text(text = buildAnnotatedString {
                append("Tamanho da Imagem:")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(sizeImage)
                }
            })
            Spacer(Modifier.height(5.dp))
            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onDismiss()
                    }
                }
            }) {
                Text(text ="Fechar estatísiticas")
            }
        }
        // Sheet content

    }

}