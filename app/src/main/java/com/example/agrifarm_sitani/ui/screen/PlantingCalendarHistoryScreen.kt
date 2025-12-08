package com.example.agrifarm_sitani.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrifarm_sitani.data.database.AppDatabase
import com.example.agrifarm_sitani.data.entity.PlantingScheduleEntity
import com.example.agrifarm_sitani.data.entity.ScheduleItemEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.time.Instant
import java.time.ZoneId

private val BackgroundCream = Color(0xFFF5EEDC)
private val AccentGreen = Color(0xFF5D8B63)
private val Green = Color(0xFF144224)
private val SoftGreen = Color(0xFFAED4B3)
private val MutedText = Color(0xFF6B6B6B)
private val CardBrown = Color(0xFFDDB892)
private val CardGreen = Color(0xFFEAF3EC)

@Composable
fun PlantingCalendarHistoryScreen(onNavigateBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember {
        androidx.room.Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "agrifarm_database"
        )
            .allowMainThreadQueries()
            .build()
    }
    val dao = remember { database.plantingScheduleDao() }
    val historyState = dao.getAllSchedules().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .padding(24.dp),
        color = BackgroundCream
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp, 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = AccentGreen)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Riwayat Jadwal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen
                )
            }

            Spacer(Modifier.height(16.dp))

            if (historyState.value.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada jadwal tersimpan", color = MutedText)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyState.value) { schedule ->
                        // Fetch items for this schedule
                        val itemsState = dao.getScheduleItems(schedule.id).collectAsState(initial = emptyList())
                        
                        ScheduleHistoryCard(
                            schedule = schedule, 
                            items = itemsState.value,
                            onDelete = {
                                coroutineScope.launch {
                                    dao.deleteSchedule(it)
                                    dao.deleteScheduleItems(it.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleHistoryCard(
    schedule: PlantingScheduleEntity, 
    items: List<ScheduleItemEntity>,
    onDelete: (PlantingScheduleEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Mulai: ${SimpleDateFormat("dd MMM yyyy", Locale("id")).format(Date(schedule.startDate))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                }
                Row {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Detail",
                            tint = AccentGreen
                        )
                    }
                    IconButton(onClick = { onDelete(schedule) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                    }
                }
            }
            
            if (expanded) {
                HorizontalDivider(color = CardGreen)
                Column(modifier = Modifier.padding(16.dp)) {
                    if (items.isEmpty()) {
                        Text("Memuat detail...", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    } else {
                        items.forEach { item ->
                            // Calculate actual date: startDate + day
                            val itemDate = Date(schedule.startDate + (item.day * 24L * 60 * 60 * 1000))
                            val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale("id")).format(itemDate)
                            
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when {
                                        item.activity.contains("Pemupukan") || item.activity.contains("Pupuk") -> Icons.Default.LocalFlorist
                                        item.activity.contains("Penyiraman") || item.activity.contains("Siram") -> Icons.Default.WaterDrop
                                        else -> Icons.Default.Autorenew
                                    },
                                    contentDescription = null,
                                    tint = SoftGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = item.activity,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = AccentGreen
                                    )
                                    Text(
                                        text = "Hari ke-${item.day} ($formattedDate)",
                                        fontSize = 12.sp,
                                        color = MutedText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
