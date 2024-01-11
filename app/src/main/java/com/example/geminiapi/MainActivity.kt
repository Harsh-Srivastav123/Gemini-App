package com.example.geminiapi

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.geminiapi.ui.theme.GeminiAPITheme
import com.example.geminiapi.ui.theme.viewModel.GeminiViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiAPITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(GeminiViewModel())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@SuppressLint("CoroutineCreationDuringComposition")

@Composable
fun HomeScreen(viewModel: GeminiViewModel) {
    Surface(Modifier.fillMaxSize()) {
        var uriImage = remember {
            mutableStateOf<List<Uri>>(emptyList())
        }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
            onResult = {

                uriImage.value = it
            })
        val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {

        }
    )




        val context= LocalContext.current
        var text = remember {
            mutableStateOf("")
        }
        var input = remember {
            mutableStateOf("")
        }
        val coroutineScope = rememberCoroutineScope()
//        coroutineScope.launch {
//            text.value= gemini(input.value).text.toString()
//        }

        val keyboardController = LocalSoftwareKeyboardController.current
        val chats = viewModel.chats
        Column(
            Modifier
                .fillMaxSize()
                .padding(15.dp)


        ) {

            ConversationScreen(conversations = chats, Modifier.fillMaxHeight(0.92f))
            Divider(thickness = 2.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)) {
                OutlinedTextField(value = input.value,
                    singleLine = true
                    ,placeholder = {
                    Text(text = "Enter Prompt Here ")
                }, onValueChange = {
                    input.value = it
                }, modifier = Modifier.fillMaxWidth(0.80f),
                    trailingIcon = {
                       Row() {
                           Image(imageVector = Icons.Rounded.List, contentDescription = "list", modifier = Modifier.clickable {
                               launcher.launch("image/*")
                           })
                           Image(imageVector = Icons.Rounded.Person, contentDescription = "list", modifier = Modifier.clickable {
                               launcher.launch("image/*")
                           })
                       }
                    }
                )


                Button(
                    modifier = Modifier.padding(start = 10.dp),
                    shape = RoundedCornerShape(corner = CornerSize(5.dp)),
                    onClick = {
                        text.value = ""
                        keyboardController?.hide()
                        coroutineScope.launch {


                            val bitmapList = mutableListOf<Bitmap>()
                            uriImage.value.forEach {
                                bitmapList.add(
                                    MediaStore.Images.Media.getBitmap(
                                        context.contentResolver,
                                        it
                                    )
                                )
                            }


                            viewModel.prompt(input.value, bitmapList)

//                    val generativeModel:GenerativeModel= gemini()
//
////                    val inputContent = content {
////
////                        text(input.value)
////                    }
//
//                    val chat = generativeModel.startChat(
////                        history = listOf(
////                            content(role = "User") {  }
////                        )
//                    )
//                    chat.sendMessageStream(input.value).collect { chunk ->
//                        text.value+=chunk.text.toString()
//                    }
                        }
                    }) {
                    Image(imageVector = Icons.Rounded.ArrowForward, contentDescription = "forward")
                }

            }
            Spacer(modifier = Modifier.size(30.dp))
//            if (!text.value.equals("")) {
//
//            }
//            text.value=chats.last().second
//            Text(text = text.value)
//            text.value = viewModel.chats.last().second
//            if (text != null) {
//                Text(text = text.value)
//            }

        }
        Log.i("chatsize", chats.size.toString())
//        ChatHistory(viewModel)
    }
}

//
//@Composable
//fun ChatHistory(viewModel: GeminiViewModel){
//
//    LazyColumn(Modifier.padding(start = 100.dp)){
//        items(items=viewModel.chats){
//            Text(text = it.second)
//        }
//
//    }
//}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationScreen(
    conversations: SnapshotStateList<Triple<String, String, List<Bitmap>?>>,
    modifier: Modifier = Modifier,

) {

    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(conversations.size) { index ->
            val conversation = conversations[index]
            MessageItem(
                isInComing = conversation.first == "received",
                images = conversation.third ?: emptyList(),
                content = conversation.second,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }
    }
//   LaunchedEffect(key1 = , block ={
//       lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount)
//   } )
}


@Composable
fun MessageItem(
    isInComing: Boolean,
    images: List<Bitmap>,
    content: String,
    modifier: Modifier = Modifier
) {

    val cardShape by remember {
        derivedStateOf {
            if (isInComing) {
                RoundedCornerShape(
                    16.dp,
                    16.dp,
                    16.dp,
                    0.dp
                )
            } else {
                RoundedCornerShape(
                    16.dp,
                    16.dp,
                    0.dp,
                    16.dp
                )
            }
        }
    }

    val cardPadding by remember {
        derivedStateOf {
            if (isInComing) {
                PaddingValues(end = 24.dp)
            } else {
                PaddingValues(start = 24.dp)
            }
        }
    }

    Column(modifier = modifier) {
        if (images.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                reverseLayout = true,
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
            ) {
                items(images.size) { index ->
                    val image = images[index]
                    Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .height(60.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            )
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(cardPadding),
            shape = cardShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isInComing) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.animateContentSize(
                        animationSpec = spring()
                    )
                )
            }
        }
    }
}