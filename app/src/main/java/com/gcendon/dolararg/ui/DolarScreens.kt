package com.gcendon.dolararg.ui

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gcendon.dolararg.R
import com.gcendon.dolararg.model.Dolar
import com.gcendon.dolararg.model.formatIsoDate
import com.gcendon.dolararg.model.toArgentineCurrency
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
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
                // Usamos el color de la marca
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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

                is DolarUiState.Success -> DolarList(
                    dolares = state.dolares,
                    onDolarClick = onDolarClick
                )

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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Sombra sutil
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            0.5.dp,
            Color.LightGray.copy(alpha = 0.5f)
        ) // Un borde finito le da "cuerpo"
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Centra todo verticalmente
        ) {
            // LADO IZQUIERDO: Toda la información
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        dolar.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        dolar.fechaActualizacion.formatIsoDate(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "COMPRA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            dolar.compra.toArgentineCurrency(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            "VENTA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            dolar.venta.toArgentineCurrency(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // LADO DERECHO: El indicador de navegación (Sutil)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalles",
                tint = Color.LightGray,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(20.dp)
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DolarDetailScreen(nombreDolar: String, viewModel: DolarViewModel, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    LaunchedEffect(nombreDolar) {
        viewModel.cargarHistorial(nombreDolar)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nombreDolar, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (viewModel.isHistoryLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.historialState.isNotEmpty()) {
            val historial = viewModel.historialState.takeLast(15)
            val preciosVenta = historial.map { it.venta.toFloat() }
            val fechas = historial.map { it.fecha }

            val hoyVenta = preciosVenta.last()
            val hoyCompra = historial.last().compra.toFloat()
            val ayerVenta =
                if (preciosVenta.size > 1) preciosVenta[preciosVenta.size - 2] else hoyVenta
            val variacion = ((hoyVenta - ayerVenta) / ayerVenta) * 100

            // --- LÓGICA DE ZOOM DINÁMICO ---
            val minPrecio = preciosVenta.minOrNull() ?: 0f
            val maxPrecio = preciosVenta.maxOrNull() ?: 0f
            val paddingEjeY =
                (maxPrecio - minPrecio) * 0.3f // 30% de margen para que la línea no toque los bordes

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. HEADER SIMÉTRICO Y COMPACTO
                HeaderSimetrico(hoyCompra, hoyVenta, variacion)

                // 2. ESTADÍSTICAS (Aclaramos que es sobre Venta)
                Text(
                    "Estadísticas de Venta (15 días)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                ResumenMetricas(preciosVenta)

                // 3. GRÁFICO OPTIMIZADO (Altura 240dp + Zoom)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Chart(
                            chart = lineChart(
                                spacing = 50.dp,
                                lines = listOf(
                                    lineSpec(
                                        lineColor = MaterialTheme.colorScheme.primary,
                                        lineBackgroundShader = verticalGradient(
                                            arrayOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                ),
                                // APLICAMOS EL ZOOM DINÁMICO AQUÍ
                                axisValuesOverrider = com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider.fixed(
                                    minY = minPrecio - paddingEjeY,
                                    maxY = maxPrecio + paddingEjeY
                                )
                            ),
                            model = entryModelOf(*preciosVenta.toTypedArray()),
                            startAxis = rememberStartAxis(
                                label = axisLabelComponent(
                                    textSize = 10.sp,
                                    textAlign = Paint.Align.CENTER
                                ),
                                valueFormatter = { value, _ -> "$ ${value.toInt()}" }
                            ),
                            bottomAxis = rememberBottomAxis(
                                label = axisLabelComponent(
                                    textSize = 10.sp,
                                    textAlign = Paint.Align.CENTER
                                ),
                                labelRotationDegrees = 45f,
                                valueFormatter = { value, _ ->
                                    val index = value.toInt()
                                    if (index in fechas.indices && (index == 0 || index == fechas.size - 1 || index % 4 == 0)) {
                                        val f = fechas[index]
                                        "${f.substring(8, 10)}/${f.substring(5, 7)}"
                                    } else ""
                                }
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // 4. TABLA
                Text(
                    "Historial Reciente",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)) {
                    Column {
                        historial.reversed().forEach { punto ->
                            HistorialRow(punto)
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSimetrico(compra: Float, venta: Float, variacion: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("COMPRA", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    "$ ${String.format("%.2f", compra)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("VENTA", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    "$ ${String.format("%.2f", venta)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Variación (Rojo si sube, Verde si baja - Postura neutral)
        val colorVariacion =
            if (variacion > 0) Color(0xFFD32F2F) else if (variacion < 0) Color(0xFF388E3C) else Color.Gray
        val icon = if (variacion > 0) "▲" else if (variacion < 0) "▼" else "●"

        Surface(
            modifier = Modifier.padding(top = 8.dp),
            color = colorVariacion.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "$icon Variación: ${String.format("%.2f", variacion)}%",
                color = colorVariacion,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun HistorialRow(punto: com.gcendon.dolararg.model.DolarHistorico) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(punto.fecha, style = MaterialTheme.typography.bodyMedium)
        Text(
            "$ ${String.format("%.2f", punto.venta)}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ResumenMetricas(precios: List<Float>) {
    val max = precios.maxOrNull() ?: 0f
    val min = precios.minOrNull() ?: 0f
    val promedio = precios.average().toFloat()

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MetricCard(
            "MÁXIMO",
            "$ ${String.format("%.2f", max)}",
            Color(0xFF2E7D32),
            Modifier.weight(1f)
        )
        MetricCard("MÍNIMO", "$ ${String.format("%.2f", min)}", Color.Red, Modifier.weight(1f))
        MetricCard("PROM.", "$ ${String.format("%.0f", promedio)}", Color.Gray, Modifier.weight(1f))
    }
}

@Composable
fun MetricCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color)
            Text(
                value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}