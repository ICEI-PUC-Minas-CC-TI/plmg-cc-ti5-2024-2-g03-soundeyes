package com.jerson.soundeyes.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.jerson.soundeyes.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Criadores
            Text(text = "Criadores", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CreatorCard(
                    context = context,
                    name = "Jerson Vitor de Paula Gomes",
                    githubUrl = "https://github.com/JersonVitor",
                    linkedinUrl = "https://www.linkedin.com/in/jersonvitor/",
                    image = R.drawable.jerson
                )

                CreatorCard(
                    context = context,
                    name = "Wallace Freitas Oliveira",
                    githubUrl = "https://github.com/Olivwallace",
                    linkedinUrl = "https://www.linkedin.com/in/olivwallace/",
                    image = R.drawable.wallace
                )
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

            // Sobre o SoundEyes
            SectionTitle("Sobre o SoundEyes")

            HighlightedText("O que é?", "O SoundEyes é um assistente de mobilidade desenvolvido para transformar a maneira como pessoas com deficiência visual interagem com o ambiente ao seu redor. Utilizando inteligência artificial e tecnologia móvel, nosso sistema identifica obstáculos em tempo real (como móveis, veículos ou pessoas) e descreve sua localização por meio de comandos de voz claros e intuitivos.")
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            SectionTitle("Como Funciona?")

            HighlightedText("📷 Visão que ouve", "Uma câmera portátil captura o ambiente e envia as imagens para seu smartphone.")
            HighlightedText("🧠 IA que entende", "Algoritmos avançados analisam a cena, detectando objetos e suas posições (esquerda, frente, direita).")
            HighlightedText("🗣️ Voz que guia", "As informações são convertidas em audiodescrições instantâneas, permitindo que você navegue com segurança e autonomia.")

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

            SectionTitle("Nossa Missão")

            HighlightedText("💡 Tecnologia inclusiva", "Acreditamos que a tecnologia deve ser inclusiva. Por isso, criamos o SoundEyes para ser simples, portátil e adaptável ao seu dia a dia.")
            HighlightedText("🤝 Um parceiro na independência", "Não somos apenas um aplicativo: somos um parceiro na conquista de independência e confiança.")
            HighlightedText("❤️ Inovação com propósito", "Tecnologia com um propósito. Inovação com um coração.")
        }
    }
}

// Componente de título de seção
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

// Componente para exibição de texto com destaque
@Composable
fun HighlightedText(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}



@Composable
fun CreatorCard(
    context: Context,
    name: String,
    githubUrl: String,
    linkedinUrl: String,
    @DrawableRes image: Int
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(160.dp), // Definir um tamanho fixo para evitar distorções
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Avatar
            Image(
                painter = painterResource(id = image),
                contentDescription = "Foto de $name",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            // Nome do criador
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 2
            )

            // Links sociais
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {

                    Image(
                        modifier = Modifier.clickable {
                            openUrl(context, githubUrl)
                        }.size(30.dp),
                        painter = painterResource(R.drawable.github_icon),
                        contentDescription = "GitHub"
                    )


                    Image(
                        modifier = Modifier.clickable {
                            openUrl(context, linkedinUrl)
                        }.size(33.dp),
                        painter = painterResource(R.drawable.linkedin_icon),
                        contentScale = ContentScale.Inside,
                        contentDescription = "LinkedIn"
                    )

            }
        }
    }
}


// Função para abrir URL no navegador
fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
