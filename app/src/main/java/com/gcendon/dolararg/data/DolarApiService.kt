package com.gcendon.dolararg.data

import com.gcendon.dolararg.model.DolarHistorico
import retrofit2.http.GET
import retrofit2.http.Path

interface DolarApiService {
    @GET("dolares")
    suspend fun getDolares(): List<DolarResponse>

    // Usamos esta API que SÍ tiene historial y los campos se llaman igual
    @GET("https://api.argentinadatos.com/v1/cotizaciones/dolares/{tipo}")
    suspend fun getHistorial(
        @Path("tipo") tipo: String
    ): List<DolarHistorico>
}