package de.bahnpilot.app

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.bahnpilot.app.data.*
import kotlinx.coroutines.launch

class BahnpilotViewModel : ViewModel() {
    var fromStation by mutableStateOf<Location?>(null)
    var toStation by mutableStateOf<Location?>(null)
    var fromQuery by mutableStateOf("")
    var toQuery by mutableStateOf("")

    var journeys by mutableStateOf<List<Journey>>(emptyList())
    var departures by mutableStateOf<List<Departure>>(emptyList())
    var departuresLoading by mutableStateOf(false)
    var journeysLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var selectedJourneyIndex by mutableStateOf(0)
    var directOnly by mutableStateOf(false)

    val api = BahnpilotRepository.api

    suspend fun searchLocations(q: String): List<Location> {
        return api.searchLocations(q)
    }

    fun resolveAndSearch() {
        // If user typed free text without picking suggestion, resolve first result
        viewModelScope.launch {
            try {
                journeysLoading = true
                errorMessage = null
                var fromId = fromStation?.id
                var toId = toStation?.id
                if (fromId == null && fromQuery.isNotBlank()) {
                    val r = api.searchLocations(fromQuery, results = 1)
                    fromId = r.firstOrNull()?.id
                    fromStation = r.firstOrNull()
                }
                if (toId == null && toQuery.isNotBlank()) {
                    val r = api.searchLocations(toQuery, results = 1)
                    toId = r.firstOrNull()?.id
                    toStation = r.firstOrNull()
                }
                if (fromId == null || toId == null) {
                    errorMessage = "Start oder Ziel nicht gefunden"
                    journeysLoading = false
                    return@launch
                }
                val nowIso = java.time.Instant.now().toString()
                val resp = api.getJourneys(
                    from = fromId, to = toId, departure = nowIso,
                    results = 8, transfers = if (directOnly) 0 else -1
                )
                journeys = resp.journeys
            } catch (e: Exception) {
                errorMessage = e.message ?: "Fehler"
            } finally {
                journeysLoading = false
            }
        }
    }

    fun searchJourneys() {
        // Prefer direct fromStation if already set
        if (fromStation != null && toStation != null) {
            viewModelScope.launch {
                try {
                    journeysLoading = true
                    errorMessage = null
                    val resp = api.getJourneys(
                        from = fromStation!!.id, to = toStation!!.id,
                        departure = java.time.Instant.now().toString(),
                        results = 8, transfers = if (directOnly) 0 else -1
                    )
                    journeys = resp.journeys
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Fehler"
                } finally { journeysLoading = false }
            }
        } else {
            resolveAndSearch()
        }
    }

    fun loadDepartures(stopId: String) {
        viewModelScope.launch {
            try {
                departuresLoading = true
                errorMessage = null
                val deps = try {
                    api.getDepartures(stopId, duration = 60, results = 20)
                } catch (e: Exception) {
                    // fallback wrapped
                    api.getDeparturesWrapped(stopId).departures ?: emptyList()
                }
                // departures API may return List directly or wrapped
                departures = deps
            } catch (e: Exception) {
                errorMessage = e.message
            } finally { departuresLoading = false }
        }
    }

    fun loadDeparturesByQuery(query: String) {
        viewModelScope.launch {
            try {
                departuresLoading = true
                val locs = api.searchLocations(query, 1)
                val id = locs.firstOrNull()?.id ?: return@launch
                loadDepartures(id)
            } catch (e: Exception) { errorMessage = e.message }
        }
    }

    fun refreshJourney(index: Int) {
        val j = journeys.getOrNull(index) ?: return
        val token = j.refreshToken ?: return
        viewModelScope.launch {
            try {
                // transport.rest refresh returns {journey: {...}} or Journey directly
                val raw = api.refreshJourney(token)
                // try parse journey from raw map
                val gson = com.google.gson.Gson()
                val json = gson.toJson(raw)
                // crude: if contains journey key, extract
                val refreshed = try {
                    val map = gson.fromJson(json, Map::class.java)
                    if (map.containsKey("journey")) {
                        gson.fromJson(gson.toJson(map["journey"]), Journey::class.java)
                    } else {
                        gson.fromJson(json, Journey::class.java)
                    }
                } catch (e: Exception) { null }
                if (refreshed != null) {
                    val mutable = journeys.toMutableList()
                    mutable[index] = refreshed
                    journeys = mutable
                }
            } catch (e: Exception) {
                errorMessage = "Refresh fehlgeschlagen: ${e.message}"
            }
        }
    }
}
