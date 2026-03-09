package com.gcendon.dolararg.data

import com.gcendon.dolararg.model.Dolar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DolarRepository {

    // Movimos la configuración de Retrofit acá
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dolarapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(DolarApiService::class.java)

    suspend fun getDolares(): List<Dolar> {
        val response = apiService.getDolares()
        val listaMapeada = response.map { responseItem ->
            val nombreAMostrar = if (responseItem.nombre == "Bolsa") "MEP" else responseItem.nombre
            Dolar(nombreAMostrar, responseItem.compra ?: 0.0, responseItem.venta ?: 0.0, responseItem.fechaActualizacion)
        }

        // Opcional: Ponemos el MEP primero, el Blue segundo, y el resto después
        return listaMapeada.sortedBy {
            when (it.nombre) {
                "MEP" -> 1
                "Blue" -> 2
                "Oficial" -> 3
                "Tarjeta" -> 4
                else -> 5
            }
        }
    }
}