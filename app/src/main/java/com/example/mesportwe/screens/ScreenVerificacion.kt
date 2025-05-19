package com.example.mesportwe.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase
import com.example.mesportwe.SessionManager
import com.example.mesportwe.ui.theme.MeSportWeTheme

@Composable
fun ScreenVerificacion(navController: NavController) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Para poder continuar es necesario verificar el correo introducido. Por favor revisa tu bandeja de entrada",
                        color = colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(
                                top = 24.dp,
                                start = 32.dp,
                                end = 32.dp,
                                bottom = 4.dp
                            )
                    )
                    Button(
                        onClick = {
                            comprobarVerificacion(navController, context) { nuevoMensaje ->
                                message = nuevoMensaje
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 4.dp
                            )
                    ) {
                        Text("Ya estoy verificado")
                    }
                }
            }
            if (message.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 14.dp,
                                bottomEnd = 14.dp
                            )
                        )
                        .background(colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        message,
                        color = colorScheme.onErrorContainer,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            bottom = 12.dp
                        )
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 14.dp,
                                bottomEnd = 14.dp
                            )
                        )
                        .background(colorScheme.secondaryContainer),
                ) {
                    Text(text = "")
                }
            }
            Text(
                text = "¿No lo encuentras? Reenviar correo",
                color = colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable { reenviarCorreo { message = it } }
            )

        }
    }
}

// Función para reenviar el correo de verificación
fun reenviarCorreo(actualizarMensaje: (String) -> Unit) {
    val auth = Firebase.auth
    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("verificacion", "Correo de verificación reenviado")
        } else {
            Log.d("verificacion", "Error al reenviar el correo de verificación")
            actualizarMensaje("Error al enviar el correo")
        }
    }
}

// Función para comprobar si el correo del usuario está verificado
fun comprobarVerificacion(
    navController: NavController,
    context: Context,
    actualizarMensaje: (String) -> Unit
) {
    val auth = Firebase.auth
    auth.currentUser?.reload()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val isVerified = auth.currentUser?.isEmailVerified ?: false
            if (isVerified) {
                actualizarMensaje("")
                navController.navigate("screen_inicio_sesion")
                guardarSesion(context)
            } else {
                actualizarMensaje("El correo aun no esta verificado")
            }
        } else {
            Log.d("verificacion", "Error al recargar el usuario")
            actualizarMensaje("Ha surgido un error inesperado")
        }
    }
}

fun guardarSesion(context: Context) {
    val sessionManager = SessionManager(context)
    val db = com.google.firebase.Firebase.firestore
    val auth = com.google.firebase.Firebase.auth

    // nombre de usuario desde Firestore
    db.collection("usuarios").document(auth.uid!!)
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                Log.d("guardar_sesion", "DocumentSnapshot data: ${document.data}")
                val username = document.getString("NomUsu")
                val correo = document.getString("Correo")

                if (username != null && correo != null) {
                    sessionManager.saveUserDetails(correo, username)
                }
            } else {
                Log.d("guardar_sesion", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("guardar_sesion", "get failed with ", exception)
        }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewVerificacion() {
    MeSportWeTheme (darkTheme = false) {
        val navController = rememberNavController()
        ScreenVerificacion(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkVerificacion() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenVerificacion(navController)
    }
}