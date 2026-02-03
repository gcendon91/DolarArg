package com.gcendon.dolararg

import com.gcendon.dolararg.ui.DolarViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gcendon.dolararg.model.Dolar
import com.gcendon.dolararg.ui.DolarUiState
import com.gcendon.dolararg.ui.theme.DolarArgTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Esto sirve para que la app use todo el alto de pantalla, por ahora comentalo si queres
        setContent {
            DolarArgTheme {
                // 1. Obtenemos el ViewModel
                val viewModel: DolarViewModel = viewModel()

                // 2. Llamamos a la función principal que maneja los estados
                // Ya no llamamos a DolarList directamente acá.
                DolarArgApp(viewModel)
            }
        }
    }
}

@Composable
fun DolarList(dolares: List<Dolar>) {
    // LazyColumn es como un "RecyclerView" moderno o un ListBox eficiente
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dolares) { dolar ->
            DolarCard(dolar)
        }
    }
}

@Composable
fun DolarCard(dolar: Dolar) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = dolar.nombre, style = MaterialTheme.typography.headlineSmall)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Compra: $${dolar.compra}")
                Text(text = "Venta: $${dolar.venta}")
            }
        }
    }
}
@Composable
fun DolarArgApp(viewModel: DolarViewModel) {
    val state = viewModel.uiState.value

    // Box es como un contenedor que permite encimar cosas o centrarlas
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is DolarUiState.Loading -> {
                // El clásico circulito de carga de Android
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is DolarUiState.Success -> {
                // Si hay éxito, dibujamos la lista que ya teníamos
                DolarList(dolares = state.dolares)
            }
            is DolarUiState.Error -> {
                // Si hay error, mostramos mensaje y botón de reintento
                ErrorView(mensaje = state.mensaje, onRetry = { viewModel.retry() })
            }
        }
    }
}

@Composable
fun ErrorView(mensaje: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = mensaje, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}
