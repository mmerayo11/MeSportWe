package com.example.mesportwe.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mesportwe.SessionManager
import com.example.mesportwe.navigation.AppScreens
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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

    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val user = sessionManager.getUserDetails()

    var textoUsu by remember { mutableStateOf(user["username"] ?: "") }
    var textoCorreo by remember { mutableStateOf(user["email"] ?: "") }
    var textoUbi by remember { mutableStateOf("") }
    var textoNombre by remember { mutableStateOf("") }
    var textoDeportes by remember { mutableStateOf("") }
    var textoNivel by remember { mutableStateOf("") }
    var textoDescripcion by remember { mutableStateOf("") }

    var textoErrorUsuario by remember { mutableStateOf("") }
    var textoErrorUbi by remember { mutableStateOf("") }
    var textoErrorDeporte by remember { mutableStateOf("") }
    var textoErrorNivel by remember { mutableStateOf("") }
    var textoErrorDescripcion by remember { mutableStateOf("") }
    var textoSnackbar by remember { mutableStateOf("") }

    var textosActivos by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var expanded3 by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val ciudades = listOf(
        "A Coruña", "Albacete", "Alicante", "Almería", "Ávila", "Badajoz", "Barcelona", "Burgos", "Cáceres", "Cádiz", "Castellón de la Plana",
        "Ceuta", "Ciudad Real", "Córdoba", "Cuenca", "Girona", "Granada", "Guadalajara", "Huelva", "Huesca", "Jaén", "Las Palmas de Gran Canaria",
        "León", "Lleida", "Logroño", "Lugo", "Madrid", "Málaga", "Melilla", "Murcia", "Ourense", "Oviedo", "Palencia", "Palma de Mallorca", "Pamplona",
        "Pontevedra", "Salamanca", "San Sebastián", "Santa Cruz de Tenerife", "Santander", "Segovia", "Sevilla", "Soria", "Tarragona", "Teruel", "Toledo", "Valencia", "Valladolid", "Vitoria", "Zamora", "Zaragoza"
    )

    val deportes = listOf(
        "Atletismo", "Baloncesto", "Balonmano", "Ciclismo", "Fútbol", "Gimnasia", "Golf", "Judo",
        "Natación", "Pádel", "Patinaje", "Piragüismo", "Rugby", "Tenis", "Voleibol"
    )

    val niveles = listOf("principiante", "aficionado", "intermedio", "avanzado", "competitivo")

    val db = Firebase.firestore
    val currentUser = Firebase.auth.currentUser

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            db.collection("usuarios").document(uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("Firestore", "Error escuchando cambios", e)
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        textoUsu = it.getString("NomUser") ?: ""
                        textoUbi = it.getString("Localidad") ?: ""
                        textoNombre = it.getString("Nombre") ?: ""
                        textoDeportes = it.getString("deportes") ?: ""
                        textoNivel = it.getString("nivel") ?: ""
                        textoDescripcion = it.getString("Descripcion") ?: ""
                    }
                }
        }
    }

    Surface{
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 30.dp, vertical = 150.dp),
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

            OutlinedTextField(
                enabled = textosActivos,
                value = textoDescripcion,
                onValueChange = { if (it.length <= 100) textoDescripcion = it },
                label = { Text("Descripción") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().heightIn(min=56.dp),
            )

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
                        textoDescripcion.trim(),
                        { nuevoMensaje -> textoErrorUsuario = nuevoMensaje },
                        { nuevoMensaje -> textoErrorUbi = nuevoMensaje },
                        { nuevoMensaje -> textoErrorDeporte = nuevoMensaje },
                        { nuevoMensaje -> textoErrorNivel = nuevoMensaje },
                        { nuevoMensaje -> textoErrorDescripcion = nuevoMensaje },
                        { nuevoMensaje -> textoSnackbar = nuevoMensaje },
                        sessionManager
                    )
                }
                textosActivos = !textosActivos
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                if (textosActivos == false) {
                    Text("EDITAR DATOS")
                } else {
                    Text("CONFIRMAR CAMBIOS")
                }
            }

            Button(onClick = {
                navController.navigate(route = AppScreens.ScreenCambioContra.route)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("CAMBIAR CONTRASEÑA")
            }

            Button(onClick = {
                cerrarSesion(navController, context)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("CERRAR SESIÓN")
            }

            Button(onClick = {
                eliminarCuenta(navController, context)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
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

fun cambioDatos(
    usuario: String,
    ubicacion: String,
    deporte: String,
    nivel: String,
    descripcion: String,
    actualizarErrorUsuario: (String) -> Unit,
    actualizarErrorUbi: (String) -> Unit,
    actualizarErrorDeportes: (String) -> Unit,
    actualizarErrorNivel: (String) -> Unit,
    actualizarErrorDescripcion: (String) ->Unit,
    actualizarTextoSnackbar: (String) -> Unit,
    sessionManager: SessionManager
    )   {
    val auth = com.google.firebase.Firebase.auth
    val db = com.google.firebase.Firebase.firestore
    val user = com.google.firebase.Firebase.auth.currentUser
    val uid = user?.uid
    actualizarErrorUsuario("")
    actualizarErrorUbi("")
    actualizarErrorDeportes("")
    actualizarErrorNivel("")
    actualizarErrorDescripcion("")
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
        } else if (!isDescriptionValid(descripcion)){
            Log.d("registro", "Descripcion muy extensa")
            actualizarErrorDescripcion("La descripción debe tener menos de 100 caracteres")
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
                                        "nivel" to nivel,
                                        "Descripcion" to descripcion
                                    )
                                )
                                .addOnSuccessListener {
                                    actualizarTextoSnackbar("Modificado con éxito")
                                    Log.d("modificacion", "Datos actualizados correctamente")

                                    if (oldUser != usuario) {
                                        sessionManager.updateUsername(usuario)
                                        val db = Firebase.firestore

                                        db.collection("chats")
                                            .whereArrayContains("usuarios", oldUser)
                                            .get()
                                            .addOnSuccessListener { chatsSnapshot ->

                                                chatsSnapshot.documents.forEach { chatDoc ->
                                                    val chatData = chatDoc.data?.toMutableMap() ?: mutableMapOf()

                                                    val usuarios = (chatData["usuarios"] as? List<*>)?.map { it.toString() }?.map { u ->
                                                        if (u == oldUser) usuario else u
                                                    }?.sorted() ?: listOf(usuario)

                                                    chatData["usuarios"] = usuarios

                                                    val deporteC = chatData["deporte"] as? String ?: "general"
                                                    val newChatId = usuarios.joinToString("_") + "_$deporteC"

                                                    val newChatRef = db.collection("chats").document(newChatId)
                                                    val oldChatRef = chatDoc.reference

                                                    oldChatRef.collection("mensajes").get()
                                                        .addOnSuccessListener { mensajesSnapshot ->
                                                            val batch = db.batch()

                                                            mensajesSnapshot.documents.forEach { mensajeDoc ->
                                                                val mensajeData = mensajeDoc.data?.toMutableMap() ?: return@forEach
                                                                if (mensajeData["emisor"] == oldUser) {
                                                                    mensajeData["emisor"] = usuario
                                                                }
                                                                val newMensajeRef = newChatRef.collection("mensajes").document()
                                                                batch.set(newMensajeRef, mensajeData)
                                                            }

                                                            batch.set(newChatRef, chatData)
                                                            batch.delete(oldChatRef)

                                                            batch.commit()
                                                                .addOnSuccessListener {
                                                                    Log.d("CambioNombre", "Chats actualizados correctamente")
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    Log.e("CambioNombre", "Error al actualizar chats", e)
                                                                }
                                                        }
                                                }
                                            }
                                    }



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