package com.example.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraIMCApp()
        }
    }
}

@Composable
fun CalculadoraIMCApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaIngreso(navController)
        }

        composable(
            route = "resultado/{nombre}/{imc}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("imc") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val imc = backStackEntry.arguments?.getFloat("imc") ?: 0f

            PantallaResultado(
                nombre = nombre,
                imc = imc,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}


@Composable
fun PantallaIngreso(navController: androidx.navigation.NavController) {
    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Calculadora de IMC", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso en kg") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = altura,
            onValueChange = { altura = it },
            label = { Text("Altura en metros") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (error) {
            Text(
                text = "Por favor, ingresa valores válidos",
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            onClick = {
                val pesoValido = peso.toFloatOrNull()
                val alturaValida = altura.toFloatOrNull()

                if (pesoValido != null && alturaValida != null &&
                    pesoValido > 0 && alturaValida > 0
                ) {
                    val imc = pesoValido / (alturaValida * alturaValida)
                    val nombreUrl = URLEncoder.encode(nombre, StandardCharsets.UTF_8.toString())

                    error = false
                    navController.navigate("resultado/$nombreUrl/$imc")
                } else {
                    error = true
                }
            }
        ) {
            Text("Calcular")
        }
    }
}

@Composable
fun PantallaResultado(
    nombre: String,
    imc: Float,
    onVolver: () -> Unit
) {
    val categoria: String
    val colorCategoria: Color

    when {
        imc < 18.5 -> {
            categoria = "Bajo peso"
            colorCategoria = Color.Red
        }
        imc < 25.0 -> {
            categoria = "Peso normal"
            colorCategoria = Color.Green
        }
        imc < 30.0 -> {
            categoria = "Sobrepeso"
            colorCategoria = Color(0xFFFF9800)
        }
        else -> {
            categoria = "Obesidad"
            colorCategoria = Color.Red
        }
    }

