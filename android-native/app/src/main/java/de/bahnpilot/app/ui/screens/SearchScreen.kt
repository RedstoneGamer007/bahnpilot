package de.bahnpilot.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bahnpilot.app.BahnpilotViewModel
import de.bahnpilot.app.data.Location
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    vm: BahnpilotViewModel,
    onSearch: () -> Unit
) {
    var fromQuery by remember { mutableStateOf("Hannover Hbf") }
    var toQuery by remember { mutableStateOf("Berlin Hbf") }
    var fromSuggestions by remember { mutableStateOf<List<Location>>(emptyList()) }
    var toSuggestions by remember { mutableStateOf<List<Location>>(emptyList()) }
    var showFromSuggest by remember { mutableStateOf(false) }
    var showToSuggest by remember { mutableStateOf(false) }

    // Debounced autocomplete
    LaunchedEffect(fromQuery) {
        if (fromQuery.length < 2) { fromSuggestions = emptyList(); return@LaunchedEffect }
        delay(300)
        try { fromSuggestions = vm.searchLocations(fromQuery) } catch (_: Exception) {}
    }
    LaunchedEffect(toQuery) {
        if (toQuery.length < 2) { toSuggestions = emptyList(); return@LaunchedEffect }
        delay(300)
        try { toSuggestions = vm.searchLocations(toQuery) } catch (_: Exception) {}
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF07070A)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Header
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEC0000))) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("BAHNPILOT NATIVE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text("100% nativ • Kotlin • Kein WebView • Echtzeit", color = Color.White.copy(alpha=0.9f), fontSize = 12.sp)
            }
        }

        // From field
        Text("VON", color = Color(0xFF9CA3AF), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        OutlinedTextField(
            value = fromQuery, onValueChange = { fromQuery = it; showFromSuggest = true },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            placeholder = { Text("z. B. Hannover Hbf") },
            leadingIcon = { Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(50)).background(Color(0xFFEC0000)), contentAlignment = Alignment.Center) { Text("A", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black) } },
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFF1C1C22), unfocusedContainerColor = Color(0xFF1C1C22), focusedTextColor = Color.White, unfocusedTextColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        )
        if (showFromSuggest && fromSuggestions.isNotEmpty()) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22))) {
                Column {
                    fromSuggestions.take(5).forEach { loc ->
                        Row(modifier = Modifier.fillMaxWidth().clickable { fromQuery = loc.name; vm.fromStation = loc; showFromSuggest = false; fromSuggestions = emptyList() }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(loc.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(loc.id, color = Color(0xFF9CA3AF), fontSize = 11.sp)
                            }
                            Text("›", color = Color(0xFF6B7280))
                        }
                    }
                }
            }
        }

        // Swap
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(onClick = {
                val tmp = fromQuery; fromQuery = toQuery; toQuery = tmp
                val s = vm.fromStation; vm.fromStation = vm.toStation; vm.toStation = s
            }, modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White)) {
                Icon(Icons.Default.SwapVert, contentDescription = " tauschen", tint = Color.Black)
            }
        }

        // To field
        Text("NACH", color = Color(0xFF9CA3AF), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        OutlinedTextField(
            value = toQuery, onValueChange = { toQuery = it; showToSuggest = true },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("z. B. Berlin Hbf") },
            leadingIcon = { Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(50)).background(Color(0xFF1C1C22)), contentAlignment = Alignment.Center) { Text("B", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) } },
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFF1C1C22), unfocusedContainerColor = Color(0xFF1C1C22), focusedTextColor = Color.White, unfocusedTextColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        )
        if (showToSuggest && toSuggestions.isNotEmpty()) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22))) {
                Column {
                    toSuggestions.take(5).forEach { loc ->
                        Row(modifier = Modifier.fillMaxWidth().clickable { toQuery = loc.name; vm.toStation = loc; showToSuggest = false; toSuggestions = emptyList() }.padding(14.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(loc.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(loc.id, color = Color(0xFF9CA3AF), fontSize = 11.sp)
                            }
                            Text("›", color = Color(0xFF6B7280))
                        }
                    }
                }
            }
        }

        // Options - direkt toggle
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilterChip(onClick = { vm.directOnly = !vm.directOnly }, label = { Text("Nur Direkt", fontSize = 12.sp) }, selected = vm.directOnly)
            AssistChip(onClick = {}, label = { Text("Echtzeit via v6.db.transport.rest", fontSize = 10.sp) })
        }

        Button(
            onClick = {
                // Resolve if not picked via suggestion
                if (vm.fromStation == null || vm.fromStation?.name != fromQuery) {
                    // will be resolved in VM via API - we set query temporarily
                    vm.fromQuery = fromQuery
                }
                if (vm.toStation == null || vm.toStation?.name != toQuery) {
                    vm.toQuery = toQuery
                }
                vm.searchJourneys()
                onSearch()
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC0000)),
            shape = RoundedCornerShape(50)
        ) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Verbindung suchen", fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Text("Native Kotlin App • Retrofit + Coroutines • Kein WebView — echte API-Calls", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DeparturesNativeScreen(vm: BahnpilotViewModel) {
    var stationQuery by remember { mutableStateOf("Hannover Hbf") }
    var suggest by remember { mutableStateOf<List<Location>>(emptyList()) }
    LaunchedEffect(stationQuery) {
        if (stationQuery.length < 2) return@LaunchedEffect
        delay(300)
        try { suggest = vm.searchLocations(stationQuery) } catch(_:Exception) {}
    }
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFF07070A)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("ABFAHRTSMONITOR — NATIV", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = stationQuery, onValueChange = { stationQuery = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Bahnhof...") }, colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFF1C1C22), unfocusedContainerColor = Color(0xFF1C1C22), focusedTextColor = Color.White, unfocusedTextColor = Color.White), shape = RoundedCornerShape(16.dp))
            if (suggest.isNotEmpty()) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22)), modifier = Modifier.padding(top = 8.dp)) {
                    Column {
                        suggest.take(4).forEach { loc ->
                            Text(loc.name, modifier = Modifier.fillMaxWidth().clickable { stationQuery = loc.name; vm.loadDepartures(loc.id) }.padding(14.dp), color = Color.White)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.loadDeparturesByQuery(stationQuery) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC0000)), shape = RoundedCornerShape(50)) {
                Text("Abfahrten laden — nativ", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            if (vm.departuresLoading) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFFEC0000)) }
        }
        items(vm.departures) { dep ->
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22))) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(dep.line?.name ?: "Zug", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                            Text("→ ${dep.destination?.name ?: dep.direction ?: ""}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                        }
                        Text("Gl. ${dep.platform ?: dep.plannedPlatform ?: "—"} • ${if (dep.cancelled == true) "ENTFÄLLT" else if ((dep.delay ?: 0) >0) "+${(dep.delay?:0)/60} Min" else "pünktlich"}", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                    }
                    Text(dep.whenTime?.take(16)?.substringAfter("T") ?: "--:--", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}
