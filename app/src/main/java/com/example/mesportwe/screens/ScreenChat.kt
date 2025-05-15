package com.example.mesportwe.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDefaults.color
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.remote.Stream
import com.google.firebase.ktx.Firebase
import com.google.firestore.admin.v1.Index
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDate


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
                BarraMensaje(
                    mensaje = mensaje.value,
                    onMensajeChange = {mensaje.value = it},
                    onEnviarClick = {
                        enviarMensaje(chatId, usuarioActivo, mensaje.value)
                        mensaje.value = ""
                    }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
    )
    { innerPadding ->
        BodyContentChat(innerPadding, navController, chatId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentChat(padding: PaddingValues, navController: NavController, chatId: String){
    val mensajes by getMensajes(chatId)
    val sessionManager = SessionManager(LocalContext.current)
    val usuarioActivo = sessionManager.getUserDetails()["username"] ?: ""

    var fechaAnterior: LocalDate? = null

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(bottom = 70.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        mensajes.forEach { mensaje ->
            val fechaActual = obtenerFecha(mensaje.fecha)

            // Separador de día si cambia
            if (fechaActual != null && fechaActual != fechaAnterior) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatoFechaLegible(fechaActual),
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                fechaAnterior = fechaActual
            }

            // Burbuja del mensaje
            item {
                val esPropio = mensaje.emisor == usuarioActivo
                BubbleMensaje(mensaje, esPropio)
            }
        }
    }
}

data class Mensaje(
    val emisor: String = "",
    val mensaje: String = "",
    val fecha: Timestamp? = null
)


@Composable
fun BarraMensaje(
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

@Composable
fun getMensajes(chatId: String): State<List<Mensaje>> {
    val mensajes = remember { mutableStateOf<List<Mensaje>>(emptyList()) }

    LaunchedEffect(chatId) {
        val db = Firebase.firestore
        db.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener{ snapshot, error ->
                if (error != null) {
                    Log.e("Chat", "Error escuchando mensajes", error)
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Mensaje::class.java)
                } ?: emptyList()

                mensajes.value= lista
            }
    }
    return mensajes
}

@Composable
fun BubbleMensaje(mensaje: Mensaje, esPropio: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (esPropio) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (esPropio) Color(0xFF90CAF9) else Color(0xFFDDDDDD),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(text = mensaje.mensaje, color = Color.Black)
                Text(
                    text = obtenerHora(mensaje.fecha),
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}


fun obtenerHora(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    val instant = Instant.fromEpochSeconds(timestamp.seconds, timestamp.nanoseconds)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hora = dateTime.hour.toString().padStart(2, '0')
    val minuto = dateTime.minute.toString().padStart(2, '0')
    return "$hora:$minuto"
}

fun obtenerFecha(timestamp: Timestamp?): LocalDate? {
    if (timestamp == null) return null
    val instant = Instant.fromEpochSeconds(timestamp.seconds, timestamp.nanoseconds)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun formatoFechaLegible(fecha: LocalDate): String {
    val dia = fecha.dayOfMonth.toString().padStart(2, '0')
    val mes = fecha.month.name.lowercase().replaceFirstChar { it.uppercase() }
    val año = fecha.year
    return "$dia $mes $año"
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