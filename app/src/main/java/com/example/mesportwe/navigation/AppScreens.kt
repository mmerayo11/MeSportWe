package com.example.mesportwe.navigation

sealed class AppScreens(val route : String) {
    object ScreenInicioSesion: AppScreens("screen_inicio_sesion")
    object ScreenCrearCuenta: AppScreens("screen_crear_cuenta")
    object ScreenVerificacion: AppScreens("screen_verificacion")
    object ScreenInicio: AppScreens("screen_inicio")
    object ScreenPerfil: AppScreens("screen_perfil")
    object ScreenCambioContra: AppScreens("screen_cambio_contra")
}