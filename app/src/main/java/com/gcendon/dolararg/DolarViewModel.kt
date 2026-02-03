import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gcendon.dolararg.Dolar

class DolarViewModel : ViewModel() {

    // 1. El estado privado (encapsulamiento)
    private val _uiState = mutableStateOf<List<Dolar>>(emptyList())

    // 2. Lo que la UI puede leer
    val uiState: State<List<Dolar>> = _uiState

    init {
        // Al iniciar, cargamos datos "muckeados" (de prueba)
        loadDolares()
    }

    private fun loadDolares() {
        _uiState.value = listOf(
            Dolar("Dólar Blue", 1200.0, 1225.0, "Ahora"),
            Dolar("Dólar Oficial", 850.0, 890.0, "Ahora"),
            Dolar("Dólar MEP", 1150.0, 1170.0, "Ahora")
        )
    }
}