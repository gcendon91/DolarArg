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
        return response.map {
            Dolar(it.nombre, it.compra, it.venta, it.fechaActualizacion)
        }
    }
}