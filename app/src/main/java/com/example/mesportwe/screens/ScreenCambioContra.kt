package com.example.mesportwe.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mesportwe.SessionManager
import com.example.mesportwe.navigation.AppScreens
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.example.mesportwe.ui.theme.onPrimaryLight
import com.example.mesportwe.ui.theme.primaryLight
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth

@Composable
fun ScreenCambioContra(navController: NavController) {

    var textoContra by remember { mutableStateOf("") }
    var textoContraN by remember { mutableStateOf("") }
    var textoContraN2 by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var passVisibleN by remember { mutableStateOf(false) }
    var confPassVisible by remember { mutableStateOf(false) }
    var textoErrorContra by remember { mutableStateOf("") }
    var textoErrorContraN by remember { mutableStateOf("") }
    var textoErrorContraN2 by remember { mutableStateOf("") }
    var textoSnackBar by remember {mutableStateOf("")}
    val focusRequester = remember { FocusRequester() }
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val user = sessionManager.getUserDetails()
    var textoCorreo by remember { mutableStateOf(user["email"] ?: "") }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp, vertical = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value =textoContra,
                onValueChange = { textoContra = it},
                label = {Text ("Contraseña actual")},
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequester1.requestFocus() }),
                isError = textoErrorContra.isNotEmpty(),
                supportingText = { Text(textoErrorContra) },
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector = if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            OutlinedTextField(
                value =textoContraN,
                onValueChange = { textoContraN = it},
                label = {Text ("Contraseña")},
                singleLine = true,
                visualTransformation = if (passVisibleN) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequester2.requestFocus() }),
                isError = textoErrorContraN.isNotEmpty(),
                supportingText = { Text(textoErrorContraN) },
                trailingIcon = {
                    IconButton(onClick = { passVisibleN = !passVisibleN }) {
                        Icon(
                            imageVector = if (passVisibleN) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            OutlinedTextField(
                value =textoContraN2,
                onValueChange = { textoContraN2 = it},
                label = {Text ("Confirmar contraseña")},
                singleLine = true,
                visualTransformation = if (confPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onNext = { focusRequester3.requestFocus() }),
                isError = textoErrorContraN2.isNotEmpty(),
                supportingText = { Text(textoErrorContraN2) },
                trailingIcon = {
                    IconButton(onClick = { confPassVisible = !confPassVisible }) {
                        Icon(
                            imageVector = if (confPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Button(onClick = {
                if (!textoCorreo.isNullOrEmpty()) {
                    cambiarContrasena(navController,
                        textoContraN,
                        textoContraN2,
                        textoContra,
                        textoCorreo!!,
                        { nuevoMensaje -> textoErrorContraN = nuevoMensaje },
                        { nuevoMensaje -> textoErrorContraN2 = nuevoMensaje },
                        { nuevoMensaje -> textoErrorContra = nuevoMensaje },
                        { nuevoMensaje -> textoSnackBar = nuevoMensaje })
                } else {
                    textoSnackBar = "Error: No se pudo obtener el correo del usuario."
                }

            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryLight,
                    contentColor = onPrimaryLight
                )
            ) {
                Text("CONFIRMAR CAMBIOS")
            }

            Button(onClick = {
                navController.navigate(route = AppScreens.ScreenPerfil.route)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryLight,
                    contentColor = onPrimaryLight
                )
            ) {
                Text("CANCELAR")
            }
        }
    }
}

fun cambiarContrasena(navController: NavController,
                      nuevaContrasena: String,
                      nuevaContrasena2: String,
                      actualContrasena: String,
                      email: String,
                      actualizarErrorContraN: (String) -> Unit,
                      actualizarErrorContraN2: (String) -> Unit,
                      actualizarErrorContra: (String) -> Unit,
                      actualizarTextoSnackbar: (String) -> Unit) {

    actualizarErrorContraN("")
    actualizarErrorContraN2("")
    actualizarErrorContra("")
    actualizarTextoSnackbar("")
    val usuario = Firebase.auth.currentUser
    val credential = EmailAuthProvider.getCredential(email, actualContrasena)

    if (usuario != null ) {
        usuario.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    if (actualContrasena != nuevaContrasena) {
                        if (nuevaContrasena2 == nuevaContrasena) {
                            if (isPasswordStrong(nuevaContrasena)) {
                                usuario.updatePassword(nuevaContrasena)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(
                                                "cambioContra","Contraseña actualizada correctamente"
                                            )
                                            actualizarTextoSnackbar("Contraseña cambiada con éxito")
                                            navController.navigate(route = AppScreens.ScreenPerfil.route)
                                        } else {
                                            Log.d(
                                                "cambioContra",
                                                task.exception?.localizedMessage
                                                    ?: "Error desconocido"
                                            )
                                            actualizarTextoSnackbar("Error desconocido")
                                        }
                                    }
                            } else {
                                Log.d("cambioContra", "Contraseña debil")
                                actualizarErrorContraN("La contraseña es muy debil, debe contener al menos un numero, una mayuscula, una minuscula, no puede tener espacios y debe tener al menos ocho caracteres\"")
                            }

                        } else {
                            Log.d("cambioContra", "Las contraseñas no coinciden")
                            actualizarErrorContraN2("Las contraseñas no coinciden")
                        }
                    } else {
                        Log.d("cambioContra", "Actual y nueva contraseña iguales")
                        actualizarErrorContraN("La nueva contraseña no puede coincidir con la actual")
                    }
                } else{
                    Log.d("cambioContra", "La contraseña no es correcta")
                    actualizarErrorContra("La contraseña no es correcta")
                }
            }
    } else {
        Log.d("cambioContra", "No hay usuario autenticado")
        actualizarTextoSnackbar("Ha surgido un error en el intento de cambio de contraseña")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewCC() {
    MeSportWeTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenCambioContra(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkCC() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenCambioContra(navController)
    }
}
