package com.jerson.soundeyes.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

class TTSConfigManager(context: Context) {

    private val textToSpeechManager = TextToSpeechManager
    private val sharedPreferences = context.getSharedPreferences("TTSConfig", Context.MODE_PRIVATE)

    init {
        // Inicializa o TTS se ainda não estiver inicializado
        TextToSpeechManager.initialize(context)
        loadSavedConfig()
    }

    // Carregar as configurações salvas
    private fun loadSavedConfig() {
        val locale = Locale(
            sharedPreferences.getString("language", Locale.getDefault().language) ?: Locale.getDefault().language,
            sharedPreferences.getString("country", Locale.getDefault().country) ?: Locale.getDefault().country
        )
        setLanguage(locale)
        setSpeechRate(sharedPreferences.getFloat("speechRate", 1.0f))
        setPitch(sharedPreferences.getFloat("pitch", 1.0f))
    }

    // Salvar uma configuração
    private fun saveConfig(key: String, value: Any) {
        with(sharedPreferences.edit()) {
            when (value) {
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
            }
            apply()
        }
    }

    // Alterar idioma
    fun setLanguage(locale: Locale) {
        TextToSpeechManager.setLanguage(locale)
        saveConfig("language", locale.language)
        saveConfig("country", locale.country)
    }

    // Alterar taxa de fala
    fun setSpeechRate(rate: Float) {
        TextToSpeechManager.setSpeechRate(rate)
        saveConfig("speechRate", rate)
    }

    // Alterar pitch
    fun setPitch(pitch: Float) {
        TextToSpeechManager.setPitch(pitch)
        saveConfig("pitch", pitch)
    }

    // Obter vozes disponíveis
    fun getAvailableVoices(): List<Voice> {
        return TextToSpeechManager.getAvailableVoices()
    }

    // Alterar voz
    fun setVoice(voice: Voice) {
        TextToSpeechManager.setVoice(voice)
        saveConfig("voice", voice.name)
    }

    // Obter a configuração atual
    fun getConfig(): TTSConfig {
        val locale = Locale(
            sharedPreferences.getString("language", Locale.getDefault().language) ?: Locale.getDefault().language,
            sharedPreferences.getString("country", Locale.getDefault().country) ?: Locale.getDefault().country
        )
        val speechRate = sharedPreferences.getFloat("speechRate", 1.0f)
        val pitch = sharedPreferences.getFloat("pitch", 1.0f)
        return TTSConfig(locale, speechRate, pitch)
    }

    data class TTSConfig(
        val locale: Locale,
        val speechRate: Float,
        val pitch: Float
    )
}
