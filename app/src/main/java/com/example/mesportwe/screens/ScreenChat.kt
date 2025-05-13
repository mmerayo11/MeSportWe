package com.example.mesportwe.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mesportwe.SessionManager
import com.example.mesportwe.navigation.AppScreens
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@Composable
fun ScreenChat(navController: NavController, chatId: String){
    val sessionManager = SessionManager(LocalContext.current)
    val user = sessionManager.getUserDetails()
    var usuarioActivo by remember { mutableStateOf(user["username"] ?: "") }

    val partes = chatId.split("_")
    val deporte = partes.last()
    val usuarios = partes.dropLast(1)
    val otroUsuario = usuarios.firstOrNull{it != usuarioActivo} ?: ""

    val mensaje = remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                BarraSuperior("$otroUsuario $deporte",navController, true, AppScreens.ScreenMensajes.route)
            },
            bottomBar = {
                barraMensaje(
                    mensaje = mensaje.value,
                    onMensajeChange = {mensaje.value = it},
                    onEnviarClick = {
                        enviarMensaje(chatId, usuarioActivo, mensaje.value)
                        mensaje.value = ""
                    }
                )
            }
    )
    { innerPadding ->
        BodyContentChat(innerPadding, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentChat(padding: PaddingValues, navController: NavController){
    val scrollState = rememberScrollState()


    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(bottom = 70.dp),
            horizontalAlignment = Alignment.Start
        ) {

        }
    }
}

@Composable
fun barraMensaje(
    mensaje : String,
    onMensajeChange: (String) ->Unit,
    onEnviarClick: () -> Unit
){
    BottomAppBar {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            value = mensaje,
            onValueChange = onMensajeChange,
            placeholder = {Text("Escribe un mensaje...")}
        )
        IconButton(
            onClick = onEnviarClick,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(imageVector = Icons.Default.Send,
                contentDescription = "Enviar")
        }
    }
}

 fun enviarMensaje(chatId: String, usuarioActivo: String, value: String) {
     val db = Firebase.firestore

     if (value.isBlank()) return

     val mensajeData = mapOf(
         "emisor" to usuarioActivo,
         "mensaje" to value,
         "fecha" to FieldValue.serverTimestamp()
     )
     db.collection("chats")
         .document(chatId)
         .collection("mensajes")
         .add(mensajeData)
         .addOnSuccessListener {
             Log.d("EnviarMensaje", "Mensaje enviado correctamente")
         }
         .addOnFailureListener{e ->
             Log.e("EnviarMensaje", "Error al enviar mensaje", e)
         }
     db.collection("chats").document(chatId).update(
         mapOf(
             "ultimoMsj" to value,
             "fecha" to FieldValue.serverTimestamp()
         )
     )
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewC() {
    MeSportWeTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenMensajes(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkC() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenMensajes(navController)
    }
}