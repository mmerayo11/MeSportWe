package com.example.mesportwe.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mesportwe.SessionManager
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun ScreenMensajes(navController: NavController){
    BodyContentMensajes( navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentMensajes(navController: NavController){
    val sessionManager = SessionManager(LocalContext.current)
    val usuarioActivo = sessionManager.getUserDetails()["username"] ?: ""
    val chats by getChats(usuarioActivo)

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp, vertical = 110.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(chats) { chat ->
            val otroUsuario = chat.usuarios.firstOrNull { it != usuarioActivo } ?: ""
            val fechaMensaje = obtenerFecha(chat.fecha)
            val fechaActual =
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val chatId = "${chat.usuarios[0]}_${chat.usuarios[1]}_${chat.deporte}"
            val fechaOHora = if (fechaMensaje == fechaActual) {
                obtenerHora(chat.fecha)
            } else {
                formatoFechaLegible(fechaMensaje!!)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("ScreenChat/$chatId")
                    }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "$otroUsuario · ${chat.deporte}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = chat.ultimoMsj,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = fechaOHora,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

data class Chat(
    val deporte: String = "",
    val fecha: Timestamp? = null,
    val ultimoMsj: String = "",
    val usuarios: List<String> = emptyList()
)

@Composable
fun getChats(user: String): MutableState<List<Chat>> {
    val chats = remember { mutableStateOf<List<Chat>>(emptyList()) }

    LaunchedEffect(user) {
        val db = Firebase.firestore
        db.collection("chats")
            .whereArrayContains("usuarios", user)
            .orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Mensajes", "Error escuchando chats", error)
                    return@addSnapshotListener
                }

                val nuevaLista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Chat::class.java)
                } ?: emptyList()

                if (chats.value != nuevaLista) {
                    chats.value = nuevaLista
                }

            }
    }
    return chats
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewM() {
    MeSportWeTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenMensajes(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkM() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenMensajes(navController)
    }
}