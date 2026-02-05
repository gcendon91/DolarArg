package com.gcendon.dolararg.model

import android.os.Build
import androidx.annotation.RequiresApi

data class Dolar(
    val nombre: String,
    val compra: Double,
    val venta: Double,
    val fechaActualizacion: String
)

fun Double.toArgentineCurrency(): String {
    val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "AR"))
    return format.format(this)
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatIsoDate(): String {
    return try {
        val parser = java.time.format.DateTimeFormatter.ISO_DATE_TIME
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm 'hs'")

        // 1. Parseamos el texto original (UTC)
        val dateUtc = java.time.OffsetDateTime.parse(this, parser)

        // 2. LA MAGIA: Convertimos al Timezone del dispositivo (Argentina, en tu caso)
        val dateLocal = dateUtc.atZoneSameInstant(java.time.ZoneId.systemDefault())

        dateLocal.format(formatter)
    } catch (e: Exception) {
        this
    }
}