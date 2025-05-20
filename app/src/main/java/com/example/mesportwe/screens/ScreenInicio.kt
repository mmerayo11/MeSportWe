package com.example.mesportwe.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mesportwe.SessionManager
import com.example.mesportwe.navigation.AppScreens
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.example.mesportwe.ui.theme.onBackgroundLight
import com.example.mesportwe.ui.theme.onPrimaryContainerLight
import com.example.mesportwe.ui.theme.onPrimaryLight
import com.example.mesportwe.ui.theme.primaryContainerLight
import com.example.mesportwe.ui.theme.primaryLight
import com.example.mesportwe.ui.theme.secondaryLight
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ScreenInicio(navController: NavController) {
    Scaffold(
        topBar = {
            BarraSuperior("")
        },
        bottomBar = { BarraInferior(navController = navController) }

    ) { innerPadding ->
        BodyContentInicio(innerPadding, navController)
    }
}

@Composable
fun BodyContentInicio(padding: PaddingValues, navController: NavController) {
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    var listaUsuarios by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val usuarios = encontrarUsuarios()
        if (usuarios.isNotEmpty()) {
            val nombreUsuario = usuarios.first()
            val datos = obtenerDatosUser(nombreUsuario)
            listaUsuarios = usuarios.drop(1)
            usuarioActual = datos
        }
        else {
            usuarioActual= Usuario(
                nomUser = "No hay usuarios disponibles en este momento",
                nombre = "",
                localidad = "",
                deporte = "",
                nivel = "",
                descripcion = ""
            )
        }
    }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp, vertical = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            usuarioActual?.let { usuario ->
                Text(text = usuario.nomUser, color = secondaryLight)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = usuario.nombre, color = secondaryLight)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = usuario.localidad, color = secondaryLight)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "${usuario.deporte}, ${usuario.nivel}", color = secondaryLight)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = usuario.descripcion, color = secondaryLight)
                Spacer(modifier = Modifier.height(60.dp))

                if (usuario.nomUser != "No hay usuarios disponibles en este momento") {
                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                val chatId = chatNuevo(usuario.nomUser, usuario.deporte)
                                navController.navigate("ScreenChat/$chatId")
                            } catch (e: Exception) {
                                Log.e("chat", "Error al crear chat")
                            }
                        }
                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryLight,
                            contentColor = onPrimaryLight
                        )
                    ) {
                        Text("ENVIAR MENSAJE")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = {
                        coroutineScope.launch {
                            if (listaUsuarios.isNotEmpty()) {
                                val siguienteUsuario = listaUsuarios.first()
                                val datos = obtenerDatosUser(siguienteUsuario)
                                listaUsuarios = listaUsuarios.drop(1)
                                usuarioActual = datos
                            } else {
                                usuarioActual = Usuario(
                                    nomUser = "No hay usuarios disponibles en este momento",
                                    nombre = "",
                                    localidad = "",
                                    deporte = "",
                                    nivel = "",
                                    descripcion = ""
                                )
                            }
                        }
                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryLight,
                            contentColor = onPrimaryLight
                        )
                    ) {
                        Text("SIGUIENTE USUARIO")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    navController: NavController? = null,
    mostrarFlecha: Boolean = false,
    destinoAlVolver: String? = null
) {
    val t = titulo.trim().ifEmpty { "Me.Sport.We" }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = t,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryLight,
                )
            },
            navigationIcon = {
                if (mostrarFlecha && navController != null && destinoAlVolver != null) {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = primaryLight
                        )
                    }
                } else {
                    null
                }
            }
        )
        HorizontalDivider(thickness = 1.dp)
    }
}


@Composable
fun BarraInferior(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    data class BottomNavItem(
        val titulo: String,
        val route: String,
        val selecIcon: ImageVector,
        val unselecIcon: ImageVector
    )

    val items = listOf(
        BottomNavItem("Inicio", "screen_inicio", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Mensajes", "screen_mensajes", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
        BottomNavItem("Perfil", "screen_perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    val selectedItemIndex = items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0

    NavigationBar(
        containerColor = primaryContainerLight,
        contentColor = onBackgroundLight
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) item.selecIcon else item.unselecIcon,
                        contentDescription = item.titulo,
                        tint = primaryLight
                    )
                },
                label = {
                    Text(
                        item.titulo,
                        color = primaryLight
                    )
                },
                selected = index == selectedItemIndex,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}


suspend fun encontrarUsuarios(): List<String> {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val uid = auth.currentUser?.uid ?: return emptyList()

    val usuarioActual = db.collection("usuarios").document(uid).get().await()
    val deporte = usuarioActual.getString("deportes")
    val ubicacion = usuarioActual.getString("Localidad")
    val usernameActual = usuarioActual.getString("NomUser")

    if(deporte == null || ubicacion == null || usernameActual == null) return emptyList()

    val usuariosCompatibles = mutableListOf<String>()

    val querySnapshot = db.collection("usuarios")
        .whereEqualTo("deportes", deporte)
        .whereEqualTo("Localidad", ubicacion)
        .get()
        .await()

    for (document in querySnapshot.documents){
        val userId = document.id
        val nombreOtroUsuario = document.getString("NomUser") ?: continue

        if (userId != uid) {
            val chatExistente = db.collection("chats")
                .whereEqualTo("deporte", deporte)
                .whereArrayContains("usuarios", usernameActual)
                .get()
                .await()
                .documents
                .any { chatDoc ->
                    val usuariosChat = chatDoc.get("usuarios") as? List<*>
                    usuariosChat?.contains(nombreOtroUsuario) == true
                }

            if (!chatExistente) {
                usuariosCompatibles.add(nombreOtroUsuario)
            }
        }
    }
    return usuariosCompatibles
}


suspend fun obtenerDatosUser(nomUser: String): Usuario {
    val db = Firebase.firestore
    val querySnapshot = db.collection("usuarios").whereEqualTo("NomUser", nomUser).get().await()
    val documento = querySnapshot.documents.first()

    return Usuario(
        nomUser = documento.getString("NomUser") ?: "",
        nombre = documento.getString("Nombre") ?: "",
        localidad = documento.getString("Localidad") ?: "",
        deporte = documento.getString("deportes") ?: "",
        nivel = documento.getString("nivel") ?: "",
        descripcion = documento.getString("Descripcion") ?: "",
    )
}

suspend fun chatNuevo(usu2: String, deporte: String): String{
    val db = Firebase.firestore
    val auth = Firebase.auth
    val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

    val userDocument = db.collection("usuarios").document(uid).get().await()
    val usu1 = userDocument.get("NomUser") as String
    val usuarios = listOf(usu1,usu2).sorted()
    val chatId = usuarios.joinToString("_") + "_$deporte"

    val chatRef = db.collection("chats").document(chatId)
    val doc = chatRef.get().await()

    if (!doc.exists()){
        val data =
            mapOf(
                "usuarios" to usuarios,
                "deporte" to deporte,
                "ultimoMsj" to "",
                "fecha" to FieldValue.serverTimestamp()
            )
        chatRef.set(data).await()
    }
    return chatId
}

data class Usuario(
    val nomUser: String,
    val nombre: String,
    val localidad: String,
    val deporte: String,
    val nivel: String,
    val descripcion: String
)


@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewI() {
    MeSportWeTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenInicio(navController)
    }
}

data class BottomNavigationItem(
    val titulo: String,
    val selecIcon: ImageVector,
    val unselecIcon: ImageVector
)

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkI() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenInicio(navController)
    }
}