package com.example.mesportwe.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import com.example.mesportwe.ui.theme.onPrimaryLight
import com.example.mesportwe.ui.theme.primaryLight
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.regex.Pattern

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenCrearCuenta(navController: NavController){
    Scaffold {
        BodyContentCC(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentCC(navController: NavController){

    var textoUsu by remember { mutableStateOf("") }
    var textoContra by remember { mutableStateOf("") }
    var textoContra2 by remember { mutableStateOf("") }
    var textoCorreo by remember { mutableStateOf("") }
    var textoNombre by remember { mutableStateOf("") }
    var textoApellidos by remember { mutableStateOf("") }
    var textoDescripcion by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var passVisible by remember { mutableStateOf(false) }
    var confPassVisible by remember { mutableStateOf(false) }
    var textoErrorUsuario by remember { mutableStateOf("") }
    var textoErrorCorreo by remember { mutableStateOf("") }
    var textoErrorContra by remember { mutableStateOf("") }
    var textoErrorContra2 by remember { mutableStateOf("") }
    var textoErrorUbi by remember { mutableStateOf("") }
    var textoErrorNombre by remember { mutableStateOf("") }
    var textoErrorApellidos by remember { mutableStateOf("") }
    var textoErrorDeportes by remember { mutableStateOf("") }
    var textoErrorNivel by remember { mutableStateOf("") }
    var textoErrorDescripcion by remember { mutableStateOf("") }
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    val focusRequester6 = remember { FocusRequester() }
    val focusRequester7 = remember { FocusRequester() }
    val focusRequester8 = remember { FocusRequester() }
    val focusRequester9 = remember { FocusRequester() }
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val ciudades = listOf(
        "A Coruña","Albacete", "Alicante", "Almería", "Ávila", "Badajoz", "Barcelona", "Burgos", "Cáceres", "Cádiz", "Castellón de la Plana",
        "Ceuta", "Ciudad Real", "Córdoba", "Cuenca", "Girona", "Granada", "Guadalajara", "Huelva", "Huesca", "Jaén", "Las Palmas de Gran Canaria",
        "León", "Lleida", "Logroño", "Lugo", "Madrid", "Málaga", "Melilla", "Murcia", "Ourense", "Oviedo", "Palencia", "Palma de Mallorca", "Pamplona",
        "Pontevedra", "Salamanca", "San Sebastián", "Santa Cruz de Tenerife", "Santander", "Segovia", "Sevilla", "Soria", "Tarragona", "Teruel", "Toledo", "Valencia", "Valladolid", "Vitoria", "Zamora", "Zaragoza")
    var ciudadElegida by remember { mutableStateOf(ciudades[0]) }
    var textoSnackbar by remember { mutableStateOf("") }

    val deportes = listOf(
        "Atletismo", "Baloncesto", "Balonmano", "Ciclismo", "Fútbol", "Gimnasia", "Golf", "Judo",
        "Natación", "Pádel", "Patinaje", "Piragüismo", "Rugby", "Tenis", "Voleibol"
    )
    var deporteElegido by remember { mutableStateOf(deportes[0]) }
    var expanded2 by remember { mutableStateOf(false) }

    val niveles = listOf("principiante", "aficionado", "intermedio", "avanzado", "competitivo")
    var nivelElegido by remember { mutableStateOf(niveles[0]) }
    var expanded3 by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Surface {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registro",
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
            )

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(start = 6.dp, end = 6.dp)
            )

            OutlinedTextField(
                value = textoUsu,
                onValueChange = { if (it.length <= 20) textoUsu = it },
                label = { Text("Usuario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester1.requestFocus() }),
                modifier = Modifier.fillMaxWidth(),
                isError = textoErrorUsuario.isNotEmpty(),
                supportingText = { Text(textoErrorUsuario) },
                trailingIcon = {
                    (if (textoErrorUsuario.isNotEmpty()) Icons.Default.ErrorOutline else null)?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "Mensaje de error"
                        )
                    }
                }
            )

            OutlinedTextField(
                value = textoContra,
                onValueChange = { textoContra = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequester2.requestFocus() }),
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
                value = textoContra2,
                onValueChange = { textoContra2 = it },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = if (confPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onNext = { focusRequester3.requestFocus() }),
                isError = textoErrorContra2.isNotEmpty(),
                supportingText = { Text(textoErrorContra2) },
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

            OutlinedTextField(
                value = textoCorreo,
                onValueChange = { textoCorreo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester4.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
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
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = ciudadElegida,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Localidad") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ciudades.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                ciudadElegida = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = textoNombre,
                onValueChange = { textoNombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester6.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            OutlinedTextField(
                value = textoApellidos,
                onValueChange = { textoApellidos = it },
                label = { Text("Apellidos") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester7.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            ExposedDropdownMenuBox(
                expanded = expanded2,
                onExpandedChange = { expanded2 = !expanded2 }
            ) {
                OutlinedTextField(
                    value = deporteElegido,
                    onValueChange = { },
                    label = { Text("Deporte") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                ExposedDropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false }
                ) {
                    deportes.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                deporteElegido = opcion
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
                    value = nivelElegido,
                    onValueChange = { },
                    label = { Text("Nivel") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
                ExposedDropdownMenu(
                    expanded = expanded3,
                    onDismissRequest = { expanded3 = false }
                ) {
                    niveles.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                nivelElegido = opcion
                                expanded3 = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = textoDescripcion,
                onValueChange = { textoDescripcion = it },
                label = { Text("Descripción(Este campo no es obligatorio)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester7.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Button(onClick = {
                registrarUsuario(
                    textoUsu.trim(),
                    textoCorreo.trim(),
                    textoContra.trim(),
                    textoContra2.trim(),
                    ciudadElegida.trim(),
                    textoNombre.trim(),
                    textoApellidos.trim(),
                    deporteElegido.trim(),
                    nivelElegido.trim(),
                    textoDescripcion.trim(),
                    navController,
                    context,
                    { nuevoMensaje -> textoErrorUsuario = nuevoMensaje },
                    { nuevoMensaje -> textoErrorCorreo = nuevoMensaje },
                    { nuevoMensaje -> textoErrorContra = nuevoMensaje },
                    { nuevoMensaje -> textoErrorContra2 = nuevoMensaje },
                    { nuevoMensaje -> textoErrorUbi = nuevoMensaje },
                    { nuevoMensaje -> textoErrorNombre = nuevoMensaje },
                    { nuevoMensaje -> textoErrorApellidos = nuevoMensaje },
                    { nuevoMensaje -> textoErrorDeportes = nuevoMensaje },
                    { nuevoMensaje -> textoErrorNivel = nuevoMensaje },
                    { nuevoMensaje -> textoErrorDescripcion = nuevoMensaje },
                    { nuevoMensaje -> textoSnackbar = nuevoMensaje },
                )
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryLight,
                    contentColor = onPrimaryLight
                )
            ) {
                Text("REGISTRAR")
            }
            Button(onClick = {
                navController.navigate(route = AppScreens.ScreenInicioSesion.route)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryLight,
                    contentColor = onPrimaryLight
                )
            ) {
                Text("YA TIENES UNA CUENTA?")
            }
        }
    }
}

fun registrarUsuario(
    textoUsu: String,
    textoCorreo: String,
    textoContra: String,
    textoContra2: String,
    textoUbi: String,
    textoNombre: String,
    textoApellidos: String,
    textoDeportes: String,
    textoNivel: String,
    textoDescripcion: String,
    navController: NavController,
    context: Context,
    actualizarErrorUsuario: (String) -> Unit,
    actualizarErrorCorreo: (String) -> Unit,
    actualizarErrorContra: (String) -> Unit,
    actualizarErrorContra2: (String) -> Unit,
    actualizarErrorUbi: (String) -> Unit,
    actualizarErrorNombre: (String) -> Unit,
    actualizarErrorApellidos: (String) -> Unit,
    actualizarErrorDeportes: (String) -> Unit,
    actualizarErrorNivel: (String) -> Unit,
    actualizarErrorDescripcion: (String) -> Unit,
    actualizarTextoSnackbar: (String) -> Unit,
) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    actualizarErrorUsuario("")
    actualizarErrorCorreo("")
    actualizarErrorContra("")
    actualizarErrorContra2("")
    actualizarErrorUbi("")
    actualizarErrorNombre("")
    actualizarErrorApellidos("")
    actualizarErrorDeportes("")
    actualizarErrorNivel("")
    actualizarErrorDescripcion("")
    actualizarTextoSnackbar("")

    Log.w("registro", "Inicio de registro")
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

        if (textoUsu.isEmpty() || textoCorreo.isEmpty() || textoContra.isEmpty() || textoContra2.isEmpty() || textoUbi.isEmpty() || textoNombre.isEmpty() || textoApellidos.isEmpty() || textoDeportes.isEmpty() || textoNivel.isEmpty()) {
            Log.d("registro", "Todos estos campos deben estar rellenos")
            if (textoUsu.isEmpty()) {
                actualizarErrorUsuario("Este campo es obligatorio")
            } else if (textoCorreo.isEmpty()) {
                actualizarErrorCorreo("Este campo es obligatorio")
            } else if (textoContra.isEmpty()) {
                actualizarErrorContra("Este campo es obligatorio")
            } else if (textoContra2.isEmpty()) {
                actualizarErrorContra2("Este campo es obligatorio")
            } else if (textoUbi.isEmpty()) {
                actualizarErrorUbi("Este campo es obligatorio")
            } else if (textoNombre.isEmpty()) {
                actualizarErrorNombre("Este campo es obligatorio")
            } else if (textoApellidos.isEmpty()) {
                actualizarErrorApellidos("Este campo es obligatorio")
            } else if (textoDeportes.isEmpty()) {
                actualizarErrorDeportes("Este campo es obligatorio")
            } else if (textoNivel.isEmpty()) {
                actualizarErrorNivel("Este campo es obligatorio")
            }

        } else if (!isEmailValid(textoCorreo)) {
            Log.d("registro", "Correo no válido")
            actualizarErrorCorreo("Dirección de correo no válida")
        } else if (!isPasswordStrong(textoContra)) {
            Log.d("registro", "contraseña débil")
            actualizarErrorContra("La contraseña es muy debil, debe contener al menos un numero, una mayuscula, una minuscula, no puede tener espacios y debe tener al menos ocho caracteres")
        } else if (textoContra != textoContra2) {
            Log.d("registro", "Las contraseñas no coinciden")
            // Las contraseñas no coinciden
            actualizarErrorContra2("Las contraseñas no coinciden")
        } else if (!isDescriptionValid(textoDescripcion)){
            Log.d("registro", "Descripcion muy extensa")
            actualizarErrorDescripcion("La descripción debe tener menos de 100 caracteres")
        } else {
            // Comprobar si el nombre de usuario ya está en uso
            db.collection("usuarios").whereEqualTo("NomUser", textoUsu).get()
                .addOnSuccessListener { result ->
                    if (result.documents.isNotEmpty()) {
                        actualizarErrorUsuario("Usuario existente")
                        Log.w("registro", "documento not null usuario existente")
                    } else {
                        // Registrar al usuario
                        auth.createUserWithEmailAndPassword(textoCorreo, textoContra)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    Log.d("registro", "Usuario registrado exitosamente")
                                    // El usuario se registró exitosamente
                                    // Guardar el nombre de usuario en la base de datos
                                    val usuario = hashMapOf(
                                        "NomUser" to textoUsu,
                                        "Localidad" to textoUbi,
                                        "Correo" to textoCorreo,
                                        "Nombre" to ("$textoNombre, $textoApellidos"),
                                        "deportes" to textoDeportes,
                                        "nivel" to textoNivel,
                                        "Descripcion" to textoDescripcion
                                    )
                                    db.collection("usuarios").document(auth.uid!!)
                                        .set(usuario)
                                        .addOnSuccessListener {
                                            Log.d("registro", "Usuario escrito en el documento con exito")

                                            // Enviar correo de verificación
                                            auth.currentUser?.sendEmailVerification()
                                                ?.addOnCompleteListener { task3 ->
                                                    if (task3.isSuccessful) {
                                                        Log.d("registro", "Correo de verificación enviado")
                                                    } else {
                                                        Log.d("registro", "Error al enviar el correo de verificación")
                                                        navController.popBackStack()
                                                    }
                                                }
                                            navController.navigate(route = AppScreens.ScreenVerificacion.route)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("registro", "Error al escribir el usuario en el documento", e)
                                            actualizarTextoSnackbar("Error al guardar el usuario")
                                        }
                                } else {
                                    Log.d("registro", "Hubo un error al registrar al usuario")
                                    actualizarTextoSnackbar("Error al realizar el registro")
                                }
                            }
                    }
                }.addOnFailureListener { exception ->
                    Log.w("registro", "Error getting document", exception)
                    actualizarTextoSnackbar("Error al comprobar el usuario")
                }
        }
    } catch (e: Exception) {
        Log.w("registro", "Excepcion intento registro: ", e)
        actualizarTextoSnackbar("Ha surgido un error en el intento de registro")
    }
}

fun isPasswordStrong(password: String): Boolean {
    val passwordPattern = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +         // al menos 1 dígito
                "(?=.*[a-z])" +         // al menos 1 letra minúscula
                "(?=.*[A-Z])" +         // al menos 1 letra mayúscula
                /*"(?=.*[@#$%^&+=?!])" +*/  // al menos 1 carácter especial
                "(?=\\S+$)" +           // sin espacios en blanco
                ".{8,}" +               // al menos 8 caracteres
                "$"
    )
    val matcher = passwordPattern.matcher(password)
    return matcher.matches()
}

fun isDescriptionValid(descripcion: String): Boolean {
    return descripcion.length <100
}

fun isEmailValid(email: String): Boolean {
    val emailPattern =
        ("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z.]{2,}$").toRegex(RegexOption.IGNORE_CASE)
    return email.matches(emailPattern)
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewCrear() {
    MeSportWeTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenCrearCuenta(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkCrear() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenCrearCuenta(navController)
    }
}