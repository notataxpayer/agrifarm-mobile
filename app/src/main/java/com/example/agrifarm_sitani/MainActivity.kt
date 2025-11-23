//// MainActivity.kt
//package com.example.agrifarm_sitani
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import com.example.agrifarm_sitani.ui.screen.SeedCalculatorScreen
//import com.example.agrifarm_sitani.ui.theme.AgrifarmSitaniTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            AgrifarmSitaniTheme {
//                SeedCalculatorScreen()
//            }
//        }
//    }
//}

package com.example.agrifarm_sitani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.agrifarm_sitani.navigation.NavGraph
import com.example.agrifarm_sitani.ui.theme.AgrifarmSitaniTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgrifarmSitaniTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}

