package com.example.mesportwe.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mesportwe.navigation.AppScreens
import com.example.mesportwe.ui.theme.MeSportWeTheme
import com.google.firebase.Timestamp


@Composable
fun ScreenMensajes(navController: NavController){
    BodyContentMensajes(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContentMensajes(navController: NavController){

}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewM() {
    MeSportWeTheme(darkTheme = false) {
        val navController = rememberNavController()
        ScreenMensajes(navController)
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreviewDarkM() {
    MeSportWeTheme(darkTheme = true) {
        val navController = rememberNavController()
        ScreenMensajes(navController)
    }
}