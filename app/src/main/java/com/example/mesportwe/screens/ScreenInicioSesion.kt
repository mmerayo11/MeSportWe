package com.example.mesportwe.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mesportwe.navigation.AppScreens
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.example.mesportwe.SessionManager
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenInicioSesion(navController: NavController){
    Scaffold () {
        BodyContentIS(navController)
    }
}

@Composable
fun BodyContentIS(navController: NavController){

    var textoCorreo by remember { mutableStateOf("") }
    var textoContra by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    var textoErrorCorreo by remember { mutableStateOf("") }
    var textoErrorPass by remember { mutableStateOf("") }
    var textoSnackbar by remember { mutableStateOf("") }

    Surface{
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment= Alignment.CenterHorizontally
        ){
            Text(
                text = "Iniciar sesión",
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 14.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = textoCorreo,
                onValueChange = {textoCorreo = it},
                label = {Text ("Correo electrónico")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
                modifier = Modifier.fillMaxWidth(),
                isError = textoErrorCorreo.isNotEmpty(),
                supportingText = { Text(textoErrorCorreo) },
                trailingIcon = {
                    (if (textoErrorCorreo.isNotEmpty()) Icons.Default.ErrorOutline else null)?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "Mensaje de error"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value =textoContra,
                onValueChange = { textoContra = it},
                label = {Text ("Contraseña")},
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = textoErrorPass.isNotEmpty(),
                supportingText = { Text(textoErrorPass) },
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

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Text(
                text = "Recupera tu contraseña",
                color = colorScheme.secondary,
                modifier = Modifier
                    .clickable {
                        recuperarContra(
                            textoCorreo,
                            { nuevoMensaje -> textoErrorCorreo = nuevoMensaje },
                            { nuevoMensaje -> textoSnackbar = nuevoMensaje}
                        ) }
                    .padding(top = 8.dp).padding(bottom = 8.dp)
            )

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    IniciarSesion(
                        textoCorreo.trim(),
                        textoContra.trim(),
                        navController,
                        context,
                        { nuevoMensaje -> textoErrorCorreo = nuevoMensaje },
                        { nuevoMensaje -> textoErrorPass = nuevoMensaje },
                        { nuevoMensaje -> textoSnackbar = nuevoMensaje }
                    )
                }){
                Text("INICIAR SESIÓN")
            }

            Button(onClick =  {
                navController.navigate(route = AppScreens.ScreenCrearCuenta.route)
            }) {
                Text("NO TIENES CUENTA?")
            }
        }
    }

}

fun recuperarContra(
    textoCorreo: String,
    actualizarErrorCorreo: (String) -> Unit,
    actualizarTextoSnackbar: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    actualizarErrorCorreo("")
    actualizarTextoSnackbar("")

    if(textoCorreo.isEmpty()){
        actualizarErrorCorreo("Ingresa una dirección de correo para recuperar tu contraseña")
    }
    else if(!isEmailValid(textoCorreo)){
        actualizarErrorCorreo("Ingresa una dirección de correo válida")
    }
    else{
        auth.sendPasswordResetEmail(textoCorreo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("cambio_pass", "Correo recuperación enviado correctamente")
                } else {
                    Log.d("cambio_pass", "Error envio correo recuperación")
                }
                actualizarTextoSnackbar("Correo de recuperación enviado con éxito")
            }
            .addOnFailureListener { exception ->
                Log.d("cambio_pass", "Error tarea enviar correo recuperacion ", exception)
            }
    }
}

fun IniciarSesion(
        textoCorreo: String,
        textoContra: String,
        navController: NavController,
        context: Context,
        actualizarErrorCorreo: (String) -> Unit,
        actualizarErrorPass: (String) -> Unit,
        actualizarTextoSnackbar: (String) -> Unit,
        ) {
            val auth: FirebaseAuth = Firebase.auth
            val db = Firebase.firestore
            actualizarErrorCorreo("")
            actualizarErrorPass("")
            actualizarTextoSnackbar("")

            Log.d("Inicio_Sesion", "Comienzo inicio sesion")
            try {
                // Comprobar la conectividad a Internet
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetworkInfo
                val isConnected = activeNetwork?.isConnectedOrConnecting == true

                if (!isConnected) {
                    // No hay conexión a Internet
                    actualizarTextoSnackbar("No hay conexión a Internet")
                    return
                }

                if (textoCorreo.isEmpty() || textoContra.isEmpty()) {
                    Log.d("Inicio_Sesion", "Campos de inicio sesion vacios")
                    if (textoCorreo.isEmpty()){
                        actualizarErrorCorreo("Este campo es obligatorio")
                    } else if (textoContra.isEmpty()){
                        actualizarErrorPass("Este campo es obligatorio")
                    }
                } else {
                    auth.signInWithEmailAndPassword(textoCorreo, textoContra)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                // Iniciar sesión exitosamente
                                val usuario = auth.currentUser
                                if (usuario?.isEmailVerified == true) {
                                    // El correo está verificado
                                    Log.d("Inicio_Sesion", "Inicio de sesion correcto")
                                    navController.navigate("screen_inicio")
                                    val sessionManager = SessionManager(context)

                                    // nombre de usuario desde Firestore
                                    db.collection("usuarios").document(auth.uid!!)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            if (document != null) {
                                                Log.d(
                                                    "Firestore",
                                                    "DocumentSnapshot data: ${document.data}"
                                                )
                                                val NomUser = document.getString("NomUser")
                                                if (NomUser != null) {
                                                    sessionManager.saveUserDetails(
                                                        textoCorreo,
                                                        NomUser
                                                    )

                                                }
                                            } else {
                                                Log.d("Firestore", "No such document")
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.d("Firestore", "get failed with ", exception)
                                            actualizarTextoSnackbar("Error al intentar iniciar sesión")
                                        }
                                } else {
                                    // El correo no está verificado
                                    usuario?.sendEmailVerification()
                                        ?.addOnCompleteListener { task3 ->
                                            if (task3.isSuccessful) {
                                                Log.d(
                                                    "Inicio_Sesion",
                                                    "Correo de verificación enviado"
                                                )
                                            } else {
                                                Log.d(
                                                    "Inicio_Sesion",
                                                    "Error al enviar el correo de verificación"
                                                )
                                            }
                                        }
                                    navController.navigate("screen_verificacion")
                                }
                            } else {
                                Log.w(
                                    "Inicio_Sesion",
                                    "Falló de credenciales al intentar iniciar sesión"
                                )
                                actualizarErrorCorreo("Correo o contraseña incorrectos")
                            }
                        }
                }
            } catch (e: Exception) {
                Log.w("Inicio_Sesion", "Excepcion en inicio sesion: ", e)
                actualizarTextoSnackbar("Error al iniciar sesión")
            }
}


@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewSesion() {
    MeSportWeTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenInicioSesion(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkSesion() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenInicioSesion(navController)
    }
}