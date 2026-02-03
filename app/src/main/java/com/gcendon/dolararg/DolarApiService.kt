import retrofit2.http.GET

interface DolarApiService {
    @GET("dolares")
    suspend fun getDolares(): List<DolarResponse>
}

// Creamos un DTO (Data Transfer Object) 
// A veces la API manda nombres de campos distintos a los que queremos usar
data class DolarResponse(
    val nombre: String,
    val compra: Double,
    val venta: Double,
    val fechaActualizacion: String
)