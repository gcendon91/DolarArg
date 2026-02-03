import com.gcendon.dolararg.data.DolarResponse
import retrofit2.http.GET

interface DolarApiService {
    @GET("dolares")
    suspend fun getDolares(): List<DolarResponse>
}

