package com.example.mesportwe.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
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
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp, vertical = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            usuarioActual?.let { usuario ->
                Text(text = usuario.nomUser, color = colorScheme.secondary)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = usuario.nombre, color = colorScheme.secondary)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = usuario.localidad, color = colorScheme.secondary)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "${usuario.deporte}, ${usuario.nivel}", color = colorScheme.secondary)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = usuario.descripcion, color = colorScheme.secondary)
                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )

                Button(onClick = {}) {
                    Text("ENVIAR MENSAJE")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        if (listaUsuarios.isNotEmpty()) {
                            val siguienteUsuario = listaUsuarios.first()
                            val datos = obtenerDatosUser(siguienteUsuario)
                            listaUsuarios = listaUsuarios.drop(1)
                            usuarioActual=datos
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

                }) {
                    Text("SIGUIENTE USUARIO")
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String) {
    var t = titulo.trim()
    if(t == ""){
        t = "Me.Sport.We"
    }
    Column {
        TopAppBar(
            title = {
                Text(
                    text = t,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                )
            }
        )
        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
fun BarraInferior(navController: NavController) {
    val items = listOf(
        BottomNavigationItem("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavigationItem("Chat", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
        BottomNavigationItem("Ubicaciones", Icons.Default.Favorite, Icons.Default.FavoriteBorder),
        BottomNavigationItem("Perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            "item_inicio" -> selectedItemIndex = 0
            "item_chat" -> selectedItemIndex = 1
            "item_favoritos" -> selectedItemIndex = 2
            "item_perfil" -> selectedItemIndex = 3
        }
    }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = if (index == selectedItemIndex){item.selecIcon} else item.unselecIcon, contentDescription = item.titulo)
                },
                label = { Text(text = item.titulo) },
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navController.navigate("screen_" + item.titulo.lowercase())
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

    if(deporte == null || ubicacion == null) return emptyList()

    val usuariosCompatibles = mutableListOf<String>()

    val querySnapshot = db.collection("usuarios")
        .whereEqualTo("deportes", deporte)
        .whereEqualTo("Localidad", ubicacion)
        .get()
        .await()

    for (document in querySnapshot.documents){
        val userId = document.id
        if (userId != uid) {
            val usuario = document.getString("NomUser")?: "Sin nombre"
            usuariosCompatibles.add(usuario)
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