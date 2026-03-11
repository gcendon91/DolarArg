package com.gcendon.dolararg.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.gcendon.dolararg.model.formatIsoDate
import com.gcendon.dolararg.model.toArgentineCurrency
import com.gcendon.dolararg.R
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

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
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp),
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleCalculator() },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "Calculadora",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
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
            if (state is DolarUiState.Success) {
                CalculatorDialog(viewModel, state.dolares)
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

@Composable
fun CalculatorDialog(viewModel: DolarViewModel, dolares: List<Dolar>) {
    if (!viewModel.showCalculator) return

    AlertDialog(
        onDismissRequest = { viewModel.toggleCalculator() },
        confirmButton = {
            TextButton(onClick = { viewModel.toggleCalculator() }) {
                Text("CERRAR", style = MaterialTheme.typography.titleMedium)
            }
        },
        title = {
            // 1. TÍTULO CENTRADO
            Text(
                "Calculadora Comparativa",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // SELECTOR (Flags y Switch)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (viewModel.isUsdToArs) "🇺🇸 Tengo Dólares" else "🇦🇷 Tengo Pesos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = !viewModel.isUsdToArs,
                        onCheckedChange = { viewModel.toggleDirection() },
                        modifier = Modifier.scale(1.1f)
                    )
                }

                // INPUT con etiqueta más grande
                OutlinedTextField(
                    value = viewModel.amountInput,
                    onValueChange = { viewModel.onAmountChange(it) },
                    // --- AQUÍ EL CAMBIO ---
                    label = {
                        Text(
                            "Monto a convertir",
                            style = MaterialTheme.typography.titleMedium, // Aumentamos el tamaño
                            fontWeight = FontWeight.Medium // Le damos un poco más de cuerpo
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                HorizontalDivider()

                // RESULTADOS
                val inputVal = viewModel.amountInput.toDoubleOrNull() ?: 0.0

                if (inputVal > 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        dolares.forEach { dolar ->
                            val resultado =
                                if (viewModel.isUsdToArs) inputVal * dolar.compra else inputVal / dolar.venta

                            // 2. FILA REFORZADA CONTRA DEFORMACIONES
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 3. ABREVIAR CCL Y DARLE PESO FIJO
                                val nombreDolar = if (dolar.nombre.contains(
                                        "liquidación",
                                        ignoreCase = true
                                    )
                                ) "CCL" else dolar.nombre

                                Text(
                                    text = nombreDolar,
                                    modifier = Modifier.weight(0.3f), // Ocupa el 30% del ancho siempre
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = if (viewModel.isUsdToArs) resultado.toArgentineCurrency() else "u\$s ${
                                        String.format(
                                            "%.2f",
                                            resultado
                                        )
                                    }",
                                    modifier = Modifier.weight(0.7f), // El valor se queda con el 70%
                                    textAlign = TextAlign.End,
                                    style = if (inputVal > 1000000) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}