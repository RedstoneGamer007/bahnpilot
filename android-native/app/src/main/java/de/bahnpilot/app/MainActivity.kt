package de.bahnpilot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.bahnpilot.app.ui.components.JourneyCard
import de.bahnpilot.app.ui.components.formatTime
import de.bahnpilot.app.ui.screens.DeparturesNativeScreen
import de.bahnpilot.app.ui.screens.SearchScreen
import de.bahnpilot.app.ui.theme.BahnpilotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BahnpilotTheme {
                BahnpilotApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BahnpilotApp(vm: BahnpilotViewModel = viewModel()) {
    val nav = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEC0000)), contentAlignment = Alignment.Center) {
                            Text("🚆", fontSize = 16.sp)
                        }
                        Column {
                            Text("BAHNPILOT NATIVE", fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
                            Text("Echtzeit • Nativ • Open Source", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF07070A), titleContentColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF14141A)) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Suche") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; nav.navigate("search") { popUpTo("search") { inclusive = false }; launchSingleTop = true } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Verbindungen") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; nav.navigate("results") { launchSingleTop = true } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Train, contentDescription = null) },
                    label = { Text("Abfahrten") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2; nav.navigate("departures") { launchSingleTop = true } }
                )
            }
        },
        containerColor = Color(0xFF07070A)
    ) { padding ->
        NavHost(navController = nav, startDestination = "search", modifier = Modifier.padding(padding)) {
            composable("search") {
                SearchScreen(vm = vm) {
                    selectedTab = 1
                    nav.navigate("results")
                }
            }
            composable("results") {
                ResultsScreen(vm = vm)
            }
            composable("departures") {
                DeparturesNativeScreen(vm = vm)
            }
        }
    }
}

@Composable
fun ResultsScreen(vm: BahnpilotViewModel) {
    var showDetail by remember { mutableStateOf(false) }
    var detailIndex by remember { mutableStateOf(0) }

    if (vm.journeysLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF07070A)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFEC0000))
        }
        return
    }

    if (vm.errorMessage != null) {
        Card(modifier = Modifier.padding(16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D))) {
            Text(vm.errorMessage ?: "Fehler", modifier = Modifier.padding(16.dp), color = Color.White)
        }
    }

    if (vm.journeys.isEmpty() && !vm.journeysLoading) {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF07070A)).padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF14141A))) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🚆", fontSize = 32.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Bereit zum Abfahren?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Suche zuerst eine Verbindung (z. B. Hannover → Berlin). Nativ + Echtzeit, kein WebView.", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    AssistChip(onClick = {}, label = { Text("Retrofit → v6.db.transport.rest • Kein WebView", fontSize = 11.sp) })
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF07070A))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${vm.journeys.size} VERBINDUNGEN • LIVE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.sp)
                    TextButton(onClick = { vm.journeys.forEachIndexed { i,_ -> vm.refreshJourney(i) } }) { Text("↻ Alle aktualisieren", color = Color(0xFFEC0000), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                }
                if (vm.fromStation != null) {
                    Text("${vm.fromStation?.name} → ${vm.toStation?.name}", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                }
            }
            itemsIndexed(vm.journeys) { idx, journey ->
                JourneyCard(
                    journey = journey,
                    isSelected = detailIndex == idx && showDetail,
                    onClick = { detailIndex = idx; showDetail = true },
                    onRefresh = { vm.refreshJourney(idx) }
                )
            }
        }

        // Detail Sheet as bottom card (native, not WebView!)
        if (showDetail) {
            val j = vm.journeys.getOrNull(detailIndex)
            if (j != null) {
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().heightIn(max = 420.dp).padding(12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("LIVE DETAILS — NATIV", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp)
                                Text("✕", modifier = Modifier.clickable { showDetail = false }.padding(8.dp), color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(8.dp))
                            // Legs nativ rendern
                        }
                        items(j.legs.size) { li ->
                            val leg = j.legs[li]
                            if (leg.walking == true) {
                                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF07070A)), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("🚶", fontSize = 14.sp)
                                        Text("${leg.distance?.let { "${it}m " } ?: ""}Umstieg / Fußweg", color = Color(0xFF9CA3AF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                val depDelay = leg.departureDelay?.div(60)
                                val arrDelay = leg.arrivalDelay?.div(60)
                                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (leg.cancelled == true) Color(0xFF450A0A) else Color(0xFF14141A)), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp))) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                                Text(leg.line?.name ?: "Zug", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                            }
                                            Text("→ ${leg.direction ?: leg.destination?.name ?: ""}", color = Color(0xFF9CA3AF), fontSize = 11.sp, maxLines = 1)
                                            if (leg.cancelled == true) {
                                                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFEC0000)).padding(horizontal = 6.dp, vertical = 2.dp)) { Text("AUSFALL", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                                            } else if (depDelay != null && depDelay != 0) {
                                                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(if (depDelay >5) Color(0xFFEC0000) else Color(0xFFF59E0B)).padding(horizontal = 6.dp, vertical = 2.dp)) { Text("+${depDelay} Min", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                                            }
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column {
                                                Text("AB • ${leg.origin?.name ?: ""}", color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                                Text(formatTime(leg.departure), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text("Gl. ${leg.plannedDeparturePlatform ?: leg.departurePlatform ?: "—"}${if (leg.plannedDeparturePlatform != null && leg.departurePlatform != null && leg.plannedDeparturePlatform != leg.departurePlatform) " → ${leg.departurePlatform} (Wechsel!)" else ""}", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("AN • ${leg.destination?.name ?: ""}", color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                                Text(formatTime(leg.arrival), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text("Gl. ${leg.plannedArrivalPlatform ?: leg.arrivalPlatform ?: "—"}", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                                            }
                                        }
                                        leg.remarks?.take(2)?.forEach { r ->
                                            if (!r.text.isNullOrBlank()) {
                                                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF78350F).copy(alpha=0.4f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                                    Text("⚠ ${r.text}", color = Color(0xFFFDBA74), fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (li < j.legs.size -1) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), contentAlignment = Alignment.Center) {
                                    Box(modifier = Modifier.width(2.dp).height(12.dp).background(Color(0xFF2A2A32)))
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { vm.refreshJourney(detailIndex) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC0000)), shape = RoundedCornerShape(50)) {
                                Text("↻ Live aktualisieren — nativer API-Call", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
