import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gcendon.dolararg.Dolar
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DolarViewModel : ViewModel() {

    private val _uiState = mutableStateOf<List<Dolar>>(emptyList())
    val uiState: State<List<Dolar>> = _uiState

    // Configuramos Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dolarapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(DolarApiService::class.java)

    init {
        fetchDolares()
    }

    private fun fetchDolares() {
        // Lanzamos una corrutina en el scope del ViewModel
        // Si el ViewModel muere, la petición se cancela sola. ¡Magia!
        viewModelScope.launch {
            try {
                val response = apiService.getDolares()
                // Mapeamos de DolarResponse (API) a nuestro Dolar (App)
                _uiState.value = response.map {
                    Dolar(it.nombre, it.compra, it.venta, it.fechaActualizacion)
                }
            } catch (e: Exception) {
                // Acá manejaríamos el error (ej: no hay internet)
            }
        }
    }
}