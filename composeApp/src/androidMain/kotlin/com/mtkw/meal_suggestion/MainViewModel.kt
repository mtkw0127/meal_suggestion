package com.mtkw.meal_suggestion

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with most use cases
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.apiKey
    )

    private val _answer = MutableStateFlow("")
    val answer = _answer.asStateFlow()

    fun requestAnswerToAi(
        bitmap: Bitmap,
    ) {
        viewModelScope.launch {
            val input = content {
                image(bitmap)
                text(
                    createPrompt(
                        personNum = 2,
                    )
                )
            }
            val response = generativeModel.generateContent(input)
            _answer.value = response.text.orEmpty()
        }
    }

    private fun createPrompt(
        personNum: Int,
    ): String {
        val prompt = """
            写真から作れる料理を提案してください。
            [前提]
            写真には食材が写っています。
            
            [要求]
            写真に写っている食材を使って作れる料理を提案してください。
            ${personNum}人分の食事の提案をお願いします。
                        
            [回答形式1]
            写真に食材が写っていない場合は「写真には食材が写っていません」と回答してください。
            
            [回答形式2]
            写真に食材が写っている場合は以下の形式で回答してください。
            「...」の箇所は食材の数や手順の数に応じて増減することを意味しています。
            AAAには料理名を記載してください。BBB、CCCには食材名と分量を記載してください。
            
            料理名：AAA(${personNum}人分)
            BBB(分量)
            CCC(分量)
            ...
            
            手順
            ①XXXX
            ②YYYY
            ③ZZZZ
            ...
        """
        return prompt
    }
}