package com.example.mesportwe.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mesportwe.screens.BarraInferior
import com.example.mesportwe.screens.BarraSuperior
import com.example.mesportwe.screens.ScreenCrearCuenta
import com.example.mesportwe.screens.ScreenInicio
import com.example.mesportwe.screens.ScreenInicioSesion
import com.example.mesportwe.screens.ScreenPerfil
import com.example.mesportwe.screens.ScreenVerificacion
import com.example.mesportwe.screens.ScreenCambioContra
import com.example.mesportwe.screens.ScreenChat
import com.example.mesportwe.screens.ScreenMensajes
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(){

    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser
    val mostrarBarra = remember { mutableStateOf(false) }

    var pantallaInicio = if (user != null){
        AppScreens.ScreenInicio.route
    } else {
        AppScreens.ScreenInicioSesion.route
    }

    Scaffold(
        bottomBar = { if (mostrarBarra.value) BarraInferior(navController) },
        topBar = {if (mostrarBarra.value) BarraSuperior("")}
    ) {
        NavHost(navController = navController, startDestination = pantallaInicio){
            composable(route = AppScreens.ScreenInicioSesion.route){
                mostrarBarra.value = false
                ScreenInicioSesion(navController)
            }
            composable(route = AppScreens.ScreenCrearCuenta.route){
                mostrarBarra.value = false
                ScreenCrearCuenta(navController)
            }
            composable(route = AppScreens.ScreenVerificacion.route){
                mostrarBarra.value = false
                ScreenVerificacion(navController)
            }
            composable(route = AppScreens.ScreenInicio.route){
                mostrarBarra.value = true
                ScreenInicio(navController)
            }
            composable(route = AppScreens.ScreenPerfil.route){
                mostrarBarra.value = true
                ScreenPerfil(navController)
            }
            composable(route = AppScreens.ScreenCambioContra.route){
                mostrarBarra.value = false
                ScreenCambioContra(navController)
            }
            composable(route = AppScreens.ScreenMensajes.route){
                mostrarBarra.value = true
                ScreenMensajes(navController)
            }
            composable(
                route = "ScreenChat/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                mostrarBarra.value = false
                val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
                ScreenChat(navController, chatId)
            }
        }
    }

}