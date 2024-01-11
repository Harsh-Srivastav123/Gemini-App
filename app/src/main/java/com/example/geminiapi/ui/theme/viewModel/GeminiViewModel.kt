package com.example.geminiapi.ui.theme.viewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.LookaheadLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

//suspend fun gemini(): GenerativeModel {
//    val apiKey = "AIzaSyAcmyImU_aStKdSfBIj3FfcUQm0A_Dxj6A"
//    val generativeModel = GenerativeModel(
//        // Use a model that's applicable for your use case (see "Implement basic use cases" below)
//        modelName = "gemini-pro",
//        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
//        apiKey
//    ).apply{
//        startChat()
//    }
//    return generativeModel
////      var  obj: GenerateContentResponse? =null
//////    val prompt = text
//////    val response = generativeModel.generateContent(prompt)
//////    print(response.text)
//////    Log.i("gemini",response.text.toString())
////    val inputContent = content {
////
////        text(text)
////    }
////
////    val chat = generativeModel.startChat()
////    chat.sendMessageStream(inputContent).collect { chunk ->
////        obj=chunk;
////        Log.i("message", chunk.text.toString())
////    }
////    return  obj
//}
public class GeminiViewModel ():ViewModel(){
    private val apiKey = "AIzaSyAcmyImU_aStKdSfBIj3FfcUQm0A_Dxj6A"
    private val geminiProModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = apiKey
    ).apply {
        startChat()
    }

    private val geminiProVisionModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = apiKey
    ).apply {
        startChat()
    }
    val chats = mutableStateListOf<Triple<String, String, List<Bitmap>?>>()
    val isGenerating = mutableStateOf(false)
    fun prompt(textPrompt: String, images: List<Bitmap>?){
        isGenerating.value=true;
        chats.add(Triple("user",textPrompt,images))
        chats.add(Triple("model","",null))
        val generativeModel=if(images?.isEmpty() == true) geminiProModel else geminiProVisionModel
        val inputContent = content {
            images?.forEach { imageBitmap ->
                image(imageBitmap)
            }
            text(textPrompt)
        }
        viewModelScope.launch {
            generativeModel.generateContentStream(inputContent).collect{
                Log.i("prompt", it.text.toString())
                chats[chats.lastIndex] = Triple(
                    "received",
                    chats.last().second + it.text,
                    null
                )
                Log.i("model", chats.last().second)
                Log.i("chat view", chats.size.toString())
            }
        }
        isGenerating.value=false

    }
}
