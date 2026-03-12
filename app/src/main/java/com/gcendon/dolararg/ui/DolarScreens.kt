package com.gcendon.dolararg.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DolarArgApp(viewModel: DolarViewModel, onDolarClick: (String) -> Unit) {
    val state = viewModel.uiState.value
    val isRefreshing = viewModel.isRefreshing.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.dolar),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp),
                            tint = Color.Unspecified
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
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.onRefresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is DolarUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )

                is DolarUiState.Success -> DolarList(dolares = state.dolares, onDolarClick = onDolarClick)
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
fun DolarCard(dolar: Dolar, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
fun DolarList(dolares: List<Dolar>, onDolarClick: (String) -> Unit) {
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
                DolarCard(dolar = dolar, onClick = { onDolarClick(dolar.nombre) })
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

                OutlinedTextField(
                    value = viewModel.amountInput,
                    onValueChange = { viewModel.onAmountChange(it) },
                    label = {
                        Text(
                            "Monto a convertir",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
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
                                    modifier = Modifier.weight(0.3f),
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
                                    modifier = Modifier.weight(0.7f),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DolarDetailScreen(nombreDolar: String, viewModel: DolarViewModel, onBack: () -> Unit) {

    LaunchedEffect(nombreDolar) {
        viewModel.cargarHistorial(nombreDolar)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tendencia: $nombreDolar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.isHistoryLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.historialState.isNotEmpty()) {

                // 1. Preparamos los datos (usamos el precio de VENTA para el gráfico)
                // Tomamos los últimos 10 o 15 días para que no se amontone
                val puntos = viewModel.historialState.takeLast(15).map { it.venta.toFloat() }
                val model = entryModelOf(*puntos.toTypedArray())

                Text(
                    "Evolución últimos 15 registros (Venta)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // 2. EL GRÁFICO
                Chart(
                    chart = lineChart(
                        // Personalizamos la línea
                        spacing = 40.dp,
                        lines = listOf(
                            com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec(
                                lineColor = Color(0xFF2E7D32).toArgb(), // Verde dólar
                            )
                        )
                    ),
                    model = model,
                    startAxis = rememberStartAxis(
                        valueFormatter = { value, _ -> "$ ${value.toInt()}" }
                    ),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Mini tabla con el precio más alto y más bajo
                val max = puntos.maxOrNull() ?: 0f
                val min = puntos.minOrNull() ?: 0f

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    InfoChip(label = "MÁXIMO", value = "$ $max", color = Color(0xFF2E7D32))
                    InfoChip(label = "MÍNIMO", value = "$ $min", color = Color.Red)
                }

            } else {
                Text("No hay datos históricos disponibles para este dólar.")
            }
        }
    }
}

// Un pequeño componente para mostrar los máximos y mínimos
@Composable
fun InfoChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.ExtraBold)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DolarNavigation(viewModel: DolarViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        // Pantalla Principal (Lista)
        composable("home") {
            DolarArgApp(
                viewModel = viewModel,
                onDolarClick = { nombre ->
                    // Cuando clickeamos, viajamos a la ruta de detalle
                    navController.navigate("detalle/$nombre")
                }
            )
        }

        // Pantalla de Detalle (Gráfico)
        composable(
            route = "detalle/{nombreDolar}",
            arguments = listOf(navArgument("nombreDolar") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombreDolar") ?: ""

            // Esta es la nueva pantalla que crearemos en el siguiente paso
            DolarDetailScreen(
                nombreDolar = nombre,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
