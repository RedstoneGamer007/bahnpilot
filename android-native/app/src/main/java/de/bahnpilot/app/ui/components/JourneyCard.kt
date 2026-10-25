package de.bahnpilot.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bahnpilot.app.data.Journey
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatTime(iso: String?): String {
    if (iso == null) return "--:--"
    return try {
        val dt = Instant.parse(iso).atZone(ZoneId.of("Europe/Berlin"))
        dt.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) { iso.take(5) }
}
fun formatDuration(journey: Journey): String {
    val firstDep = journey.legs.firstOrNull { it.walking != true }?.departure ?: return "--"
    val lastArr = journey.legs.lastOrNull { it.walking != true }?.arrival ?: return "--"
    return try {
        val s = Instant.parse(firstDep); val e = Instant.parse(lastArr)
        val mins = (e.epochSecond - s.epochSecond)/60
        val h = mins/60; val m = mins%60
        if (h>0) "${h} Std ${m} Min" else "${m} Min"
    } catch (e: Exception) { "--" }
}
fun productColor(product: String?): Color {
    return when(product) {
        "nationalExpress","national" -> Color.White
        "regionalExpress","regional" -> Color(0xFFEC0000)
        "suburban" -> Color(0xFF0E9D6E)
        else -> Color(0xFF2A2A32)
    }
}

@Composable
fun JourneyCard(journey: Journey, onClick: () -> Unit, onRefresh: () -> Unit, isSelected: Boolean = false) {
    val first = journey.legs.firstOrNull { it.walking != true }
    val last = journey.legs.lastOrNull { it.walking != true }
    val depDelayMin = first?.departureDelay?.div(60)
    val arrDelayMin = last?.arrivalDelay?.div(60)
    val cancelled = journey.legs.any { it.cancelled == true }
    val duration = formatDuration(journey)
    val transfers = journey.legs.count { it.walking == true }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E1E28) else Color(0xFF14141A)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if(isSelected) 4.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // line chips + meta
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                journey.legs.filter { it.walking != true }.forEach { leg ->
                    val bg = productColor(leg.line?.product)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(bg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = leg.line?.name ?: "Zug",
                            color = if (bg == Color.White) Color.Black else Color.White,
                            fontSize = 11.sp, fontWeight = FontWeight.Black
                        )
                    }
                }
                Text(
                    text = "• $duration • $transfers Umstiege",
                    color = Color(0xFF9CA3AF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ABFAHRT", color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(formatTime(first?.departure), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(first?.origin?.name ?: "", color = Color(0xFF9CA3AF), fontSize = 12.sp, maxLines = 1)
                    if (depDelayMin != null && depDelayMin != 0) {
                        BadgeDelay(delay = depDelayMin)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text("— $duration —", color = Color(0xFF6B7280), fontSize = 10.sp)
                    Box(modifier = Modifier.width(80.dp).height(2.dp).clip(RoundedCornerShape(50)).background(Color(0xFF2A2A32)))
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("ANKUNFT", color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(formatTime(last?.arrival), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(last?.destination?.name ?: "", color = Color(0xFF9CA3AF), fontSize = 12.sp, maxLines = 1)
                    if (arrDelayMin != null && arrDelayMin != 0) {
                        BadgeDelay(delay = arrDelayMin)
                    }
                }
            }
            if (cancelled) {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = {}, label = { Text("⛔ Ausfall / Ersatz", fontWeight = FontWeight.Bold, fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFEC0000), labelColor = Color.White))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)) {
                    Text("Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onRefresh) { Text("Live aktualisieren", fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun BadgeDelay(delay: Int) {
    val bg = when { delay <= 0 -> Color(0xFF10B981); delay in 1..5 -> Color(0xFFF59E0B); else -> Color(0xFFEC0000) }
    val label = when { delay == 0 -> "✓ pünktlich"; delay > 0 -> "+$delay Min"; else -> "$delay Min" }
    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(label, color = if (bg == Color(0xFF10B981)) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}
