package com.gcendon.dolararg.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gcendon.dolararg.model.Dolar
import com.gcendon.dolararg.model.formatIsoDate // Importamos tus nuevas funciones
import com.gcendon.dolararg.model.toArgentineCurrency
import com.gcendon.dolararg.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DolarArgApp(viewModel: DolarViewModel) {
    val state = viewModel.uiState.value
    val isRefreshing = viewModel.isRefreshing.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Reemplazamos el icono genérico por el tuyo
                        Icon(
                            painter = painterResource(id = R.drawable.dolar), // El nombre que le pusiste en drawable
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp),
                            tint = Color.Unspecified // Usamos 'Unspecified' para que mantenga sus colores originales (verde)
                        )
                        Text(
                            "DolarARG",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues -> // Este padding evita que el contenido se solape con la barra
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.onRefresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // <--- IMPORTANTE: Aplicamos el padding aquí
        ) {
            when (state) {
                is DolarUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )

                is DolarUiState.Success -> DolarList(dolares = state.dolares)
                is DolarUiState.Error -> ErrorView(
                    mensaje = state.mensaje,
                    onRetry = { viewModel.retry() })
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DolarCard(dolar: Dolar) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Nombre y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    dolar.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    dolar.fechaActualizacion.formatIsoDate(),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila inferior: Precios divididos 50/50
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("COMPRA", style = MaterialTheme.typography.labelSmall)
                    Text(
                        dolar.compra.toArgentineCurrency(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("VENTA", style = MaterialTheme.typography.labelSmall)
                    Text(
                        dolar.venta.toArgentineCurrency(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DolarList(dolares: List<Dolar>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(dolares) { dolar ->
            // Agregamos una animación simple de entrada
            androidx.compose.animation.AnimatedVisibility(
                visible = true,
                enter = androidx.compose.animation.fadeIn() +
                        androidx.compose.animation.expandVertically()
            ) {
                DolarCard(dolar)
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
        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}