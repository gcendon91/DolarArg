# 📱 DolarARG Monitor

Monitor de cotizaciones de dólares en Argentina. Una implementación funcional y robusta para practicar arquitectura y componentes modernos en Android.

---

**Características principales:**
* 🔄 **Pull-to-refresh**: Deslizar hacia abajo para actualizar las cotizaciones de forma instantánea.
* 🕒 **Hora Local**: Conversión automática de fechas (de formato UTC a zona horaria de Argentina) para mostrar el momento exacto de actualización.
* 🎨 **Material 3**: Interfaz construida con componentes modernos, tarjetas con elevación y jerarquía visual.
* 🏗️ **Arquitectura**: Separación clara de responsabilidades mediante los patrones **MVVM** (Model-View-ViewModel) y **Repositorio**.

**Stack Tecnológico:**
* **Kotlin** + **Coroutines**: Manejo de hilos y asincronismo para no bloquear la interfaz.
* **Jetpack Compose**: UI declarativa nativa.
* **Retrofit**: Cliente para el consumo de la API REST de `dolarapi.com`.
* **ViewModel & State Management**: Gestión de estados de pantalla (Loading, Success, Error).

---

**Key Features:**
* 🔄 **Pull-to-refresh**: Swipe down gesture to update exchange rates instantly.
* 🕒 **Local Timezone**: Automatic conversion from UTC to local time to track accurately when data was updated.
* 🎨 **Material 3**: Modern UI using Material Design 3 components, card elevations, and visual hierarchy.
* 🏗️ **Architecture**: Clean layer separation using **MVVM** (Model-View-ViewModel) and **Repository** patterns.

**Tech Stack:**
* **Kotlin** + **Coroutines**: Asynchronous programming and thread management.
* **Jetpack Compose**: Native declarative UI.
* **Retrofit**: REST API client for data consumption from `dolarapi.com`.
* **ViewModel & State Management**: Screen state handling (Loading, Success, Error).

---

## 🏗️ Estructura del Proyecto / Project Structure

* **`data/`**: Contiene la configuración de la API (Retrofit) y la implementación del Repositorio.
* **`model/`**: Clases de datos (Data Classes) y funciones de extensión para formateo de moneda y fechas.
* **`ui/`**: Archivos de interfaz (Compose), ViewModels y lógica de estado de la UI.

---

## 📸 Screenshots
![App Screenshot](screenshot.png)