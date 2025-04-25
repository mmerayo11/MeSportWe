package com.example.mesportwe.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mesportwe.SessionManager
import com.example.mesportwe.navigation.AppScreens
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@Composable
fun ScreenPerfil(navController: NavController) {
    BodyContentPerfil(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentPerfil(navController: NavController){

    var textoUbi by remember { mutableStateOf("") }
    var textoNombre by remember { mutableStateOf("") }
    var textoDeportes by remember { mutableStateOf("") }
    var textoNivel by remember { mutableStateOf("") }
    var textosActivos by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sessionManager = SessionManager(LocalContext.current)
    val user = sessionManager.getUserDetails()
    var textoUsu by remember { mutableStateOf(user["username"] ?: "") }
    var textoCorreo by remember { mutableStateOf(user["email"] ?: "") }
    var textoErrorUsuario by remember { mutableStateOf("") }
    var textoErrorUbi by remember { mutableStateOf("") }
    var textoErrorDeporte by remember { mutableStateOf("") }
    var textoErrorNivel by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val ciudades = listOf(
        "A Coruña","Albacete", "Alicante", "Almería", "Ávila", "Badajoz", "Barcelona", "Burgos", "Cáceres", "Cádiz", "Castellón de la Plana",
        "Ceuta", "Ciudad Real", "Córdoba", "Cuenca", "Girona", "Granada", "Guadalajara", "Huelva", "Huesca", "Jaén", "Las Palmas de Gran Canaria",
        "León", "Lleida", "Logroño", "Lugo", "Madrid", "Málaga", "Melilla", "Murcia", "Ourense", "Oviedo", "Palencia", "Palma de Mallorca", "Pamplona",
        "Pontevedra", "Salamanca", "San Sebastián", "Santa Cruz de Tenerife", "Santander", "Segovia", "Sevilla", "Soria", "Tarragona", "Teruel", "Toledo", "Valencia", "Valladolid", "Vitoria", "Zamora", "Zaragoza")
    var textoSnackbar by remember { mutableStateOf("") }

    val deportes = listOf(
        "Atletismo", "Baloncesto", "Balonmano", "Ciclismo", "Fútbol", "Gimnasia", "Golf", "Judo",
        "Natación", "Pádel", "Patinaje", "Piragüismo", "Rugby", "Tenis", "Voleibol"
    )
    var expanded2 by remember { mutableStateOf(false) }

    val niveles = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    var expanded3 by remember { mutableStateOf(false) }

    LaunchedEffect(textoUsu) {
        try {
            textoUbi = getUbi()
        } catch (e: Exception) {
            Log.e("Firestore", "Error obteniendo la ubicación", e)
            textoUbi = "Ubicación no disponible" // Valor por defecto para evitar fallos
        }

        try {
            textoNombre = getNombre()
        } catch (e: Exception) {
            Log.e("Firestore", "Error obteniendo el nombre", e)
            textoNombre = "Nombre no disponible" // Valor por defecto para evitar fallos
        }

        try {
            textoDeportes = getDeporte()
        } catch (e: Exception) {
            Log.e("Firestore", "Error obteniendo el deporte", e)
            textoDeportes = "Deporte no disponible" // Valor por defecto para evitar fallos
        }

        try {
            textoNivel = getNivel()
        } catch (e: Exception) {
            Log.e("Firestore", "Error obteniendo el nivel", e)
            textoNivel = "Nivel no disponible" // Valor por defecto para evitar fallos
        }
    }


    Surface{
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp, vertical = 150.dp),
            horizontalAlignment= Alignment.CenterHorizontally
        ) {

            if (textoUsu != null) {
                OutlinedTextField(
                    enabled = textosActivos,
                    value = textoUsu!!,
                    onValueChange = { if (it.length <= 20) textoUsu = it },
                    label = { Text("Usuario") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    isError = textoErrorUsuario.isNotEmpty()
                )
            }

            if (textoCorreo != null) {
                OutlinedTextField(
                    enabled = false,
                    value = textoCorreo!!,
                    onValueChange = { if (it.length <= 20) textoCorreo = it },
                    label = {Text("Correo")},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = textoUbi,
                    enabled = textosActivos,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Localidad") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ciudades.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                textoUbi = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                enabled = false,
                value = textoNombre,
                onValueChange = { if (it.length <= 20) textoNombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenuBox(
                expanded = expanded2,
                onExpandedChange = { expanded2 = !expanded2 }
            ) {
                OutlinedTextField(
                    value = textoDeportes,
                    enabled = textosActivos,
                    onValueChange = { },
                    label = { Text("Deporte") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false }
                ) {
                    deportes.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                textoDeportes = opcion
                                expanded2 = false
                            }
                        )
                    }
                }
            }
            ExposedDropdownMenuBox(
                expanded = expanded3,
                onExpandedChange = { expanded3 = !expanded3 }
            ) {
                OutlinedTextField(
                    value = textoNivel,
                    enabled = textosActivos,
                    onValueChange = { },
                    label = { Text("Nivel") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded3,
                    onDismissRequest = { expanded3 = false }
                ) {
                    niveles.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                textoNivel = opcion
                                expanded3 = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                if (textosActivos){
                    cambioDatos(
                        textoUsu.trim(),
                        textoUbi.trim(),
                        textoDeportes.trim(),
                        textoNivel.trim(),
                        { nuevoMensaje -> textoErrorUsuario = nuevoMensaje },
                        { nuevoMensaje -> textoErrorUbi = nuevoMensaje },
                        { nuevoMensaje -> textoErrorDeporte = nuevoMensaje },
                        { nuevoMensaje -> textoErrorNivel = nuevoMensaje },
                        { nuevoMensaje -> textoSnackbar = nuevoMensaje },
                    )
                }
                textosActivos = !textosActivos
            }) {
                if (textosActivos == false) {
                    Text("EDITAR DATOS")
                } else {
                    Text("CONFIRMAR CAMBIOS")
                }
            }

            Button(onClick = {
                navController.navigate(route = AppScreens.ScreenCambioContra.route)
            }) {
                Text("CAMBIAR CONTRASEÑA")
            }

            Button(onClick = {
                cerrarSesion(navController, context)
            }) {
                Text("CERRAR SESIÓN")
            }

            Button(onClick = {
                eliminarCuenta(navController, context)
            }) {
                Text("ELIMINAR CUENTA")
            }
        }
    }
}

fun cerrarSesion(navController: NavController, context: Context) {
    val auth = Firebase.auth
    auth.signOut()
    val sessionManager = SessionManager(context)
    sessionManager.logoutUser()
    navController.navigate(route = AppScreens.ScreenInicioSesion.route)
}

fun eliminarCuenta(navController: NavController, context: Context) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val user = auth.currentUser

    // Borrar los datos del usuario en Firestore
    db.collection("usuarios").document(user?.uid!!)
        .delete()
        .addOnSuccessListener {
            Log.d("Borrar_Cuenta", "Datos de usuario borrados exitosamente de Firestore")
        }
        .addOnFailureListener { e ->
            Log.w("Borrar_Cuenta", "Error al borrar los datos de usuario en Firestore", e)
        }

    // Borrar la cuenta del usuario
    user.delete().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("Borrar_Cuenta", "Cuenta de usuario borrada exitosamente")
        } else {
            Log.d("Borrar_Cuenta", "Error al borrar la cuenta de usuario")
        }
    }

    // Hacer logout al usuario y reiniciar la navegación
    val sessionManager = SessionManager(context)
    sessionManager.logoutUser()
    navController.navigate(route = AppScreens.ScreenInicioSesion.route)
}

suspend fun getUbi(): String {
    val db = Firebase.firestore
    val uid = Firebase.auth.currentUser?.uid
    val userDocument = db.collection("usuarios").document(uid!!).get().await()
    val ubi = userDocument.get("Localidad") as String
    return ubi
}

suspend fun getNombre(): String {
    val db = Firebase.firestore
    val uid = Firebase.auth.currentUser?.uid
    val userDocument = db.collection("usuarios").document(uid!!).get().await()
    val nombre = userDocument.get("Nombre") as String
    return nombre
}

suspend fun getDeporte(): String {
    val db = Firebase.firestore
    val uid = Firebase.auth.currentUser?.uid
    val userDocument = db.collection("usuarios").document(uid!!).get().await()
    val deporte = userDocument.get("deportes") as String
    return deporte
}

suspend fun getNivel(): String {
    val db = Firebase.firestore
    val uid = Firebase.auth.currentUser?.uid
    val userDocument = db.collection("usuarios").document(uid!!).get().await()
    val nivel = userDocument.get("nivel") as String
    return nivel
}

fun cambioDatos(
    usuario: String,
    ubicacion: String,
    deporte: String,
    nivel: String,
    actualizarErrorUsuario: (String) -> Unit,
    actualizarErrorUbi: (String) -> Unit,
    actualizarErrorDeportes: (String) -> Unit,
    actualizarErrorNivel: (String) -> Unit,
    actualizarTextoSnackbar: (String) -> Unit,
    )   {
    val auth = com.google.firebase.Firebase.auth
    val db = com.google.firebase.Firebase.firestore
    var user = com.google.firebase.Firebase.auth.currentUser
    val uid = user?.uid
    actualizarErrorUsuario("")
    actualizarErrorUbi("")
    actualizarErrorDeportes("")
    actualizarErrorNivel("")
    actualizarTextoSnackbar("")

    try{
        if (usuario.isEmpty() ||  ubicacion.isEmpty() || deporte.isEmpty() || nivel.isEmpty()) {
            Log.d("modificacion", "Todos los campos deben estar rellenos")
            if (usuario.isEmpty()) {
                actualizarErrorUsuario("Este campo es obligatorio")
            } else if (ubicacion.isEmpty()) {
                actualizarErrorUbi("Este campo es obligatorio")
            } else if (deporte.isEmpty()) {
                actualizarErrorDeportes("Este campo es obligatorio")
            } else if (nivel.isEmpty()) {
                actualizarErrorNivel("Este campo es obligatorio")
            }
        }
        else {
            val userRef = uid?.let { db.collection("usuarios").document(it) }
            if (userRef != null) {
                userRef.get().addOnSuccessListener { document ->
                    val oldUser = document.getString("NomUser") ?: ""

                    // Verificar si el usuario nuevo ya existe en la base de datos
                    db.collection("usuarios").whereEqualTo("NomUser", usuario).get()
                        .addOnSuccessListener { result ->
                            val nombreExiste = result.documents.isNotEmpty()

                            if (nombreExiste && usuario != oldUser) {
                                actualizarErrorUsuario("Nombre de usuario existente")
                                Log.w("modificacion", "El nombre de usuario ya está en uso")
                            } else {
                                // Si el nombre de usuario es nuevo o el mismo, actualizamos los datos
                                userRef.update(
                                    mapOf(
                                        "NomUser" to usuario,
                                        "Localidad" to ubicacion,
                                        "deportes" to deporte,
                                        "nivel" to nivel
                                    )
                                )
                                    .addOnSuccessListener {
                                        actualizarTextoSnackbar("Modificado con éxito")
                                        Log.d("modificacion", "Datos actualizados correctamente")
                                    }
                                    .addOnFailureListener {
                                        Log.w("modificacion", "Error al actualizar los datos")
                                        actualizarTextoSnackbar("Error al actualizar los datos")
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Log.w(
                                "modificacion",
                                "Error al verificar la existencia del nombre de usuario"
                            )
                            actualizarTextoSnackbar("Error al comprobar el nombre de usuario")
                        }
                }.addOnFailureListener {
                    Log.w("modificacion", "Error al obtener los datos del usuario actual")
                    actualizarTextoSnackbar("Error al obtener los datos del usuario actual")
                }
            }
        }
    } catch (e: Exception) {
        Log.w("modificacion", "Excepcion intento mod: ", e)
        actualizarTextoSnackbar("Ha surgido un error en el intento de modificacion")
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewP() {
    MeSportWeTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenPerfil(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkP() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenPerfil(navController)
    }
}