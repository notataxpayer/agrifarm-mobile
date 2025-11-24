package com.example.agrifarm_sitani.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrifarm_sitani.model.ScheduleItem
import com.example.agrifarm_sitani.model.PlantingSchedule
import com.example.agrifarm_sitani.model.generatePlantingSchedule
import com.example.agrifarm_sitani.data.repository.PlantingScheduleRepository
import com.example.agrifarm_sitani.data.database.AppDatabase
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

private val BackgroundCream = Color(0xFFFAF7F0)
private val AccentGreen = Color(0xFF2F6B4B)
private val SoftGreen = Color(0xFF9BC6A6)
private val CardGreen = Color(0xFFEAF3EC)
private val CardBrown = Color(0xFFF5EFE6) // Light Brown for alternating cards
private val MutedText = Color(0xFF6B6B6B)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantingCalendarScreen(onNavigateBack: () -> Unit, onNavigateToHistory: () -> Unit = {}) {
    var startDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var scheduleName by remember { mutableStateOf("") } // Name state
    // State to hold the generated schedule. Initially null.
    var schedule by remember { mutableStateOf<PlantingSchedule?>(null) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val database: AppDatabase = remember {
        androidx.room.Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "agrifarm_database"
        )
            .allowMainThreadQueries()
            .build()
    }
    val repository = remember { PlantingScheduleRepository(database.plantingScheduleDao()) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .padding(12.dp)
            .border(6.dp, AccentGreen, RoundedCornerShape(24.dp)),
        color = BackgroundCream
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = AccentGreen
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Kalendar Tanam",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, "Riwayat", tint = AccentGreen)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Pengaturan Jadwal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentGreen
                        )
                        Spacer(Modifier.height(12.dp))

                        // Name Input
                        OutlinedTextField(
                            value = scheduleName,
                            onValueChange = { scheduleName = it },
                            label = { Text("Nama Tanaman / Jadwal") },
                            placeholder = { Text("Contoh: Jagung Manis") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftGreen,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = AccentGreen
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFF7F7F7)
                            ),
                            border = null
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    SimpleDateFormat("dd MMMM yyyy", Locale("id")).format(startDate),
                                    color = MutedText
                                )
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = AccentGreen
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                calendar.time = startDate
                                val localDate = LocalDate.of(
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH) + 1,
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                )
                                val newSchedule = generatePlantingSchedule(localDate)
                                schedule = newSchedule

                                coroutineScope.launch {
                                    val finalName = if (scheduleName.isBlank()) 
                                        "Jadwal Tanam (${SimpleDateFormat("dd MMM yyyy", Locale("id")).format(startDate)})" 
                                        else scheduleName

                                    repository.saveSchedule(
                                        name = finalName,
                                        startDate = startDate.time,
                                        items = newSchedule.scheduleItems
                                    )
                                    snackbarHostState.showSnackbar("Jadwal berhasil dibuat dan disimpan!")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftGreen),
                            elevation = ButtonDefaults.buttonElevation(6.dp)
                        ) {
                            Text(
                                "Buat & Simpan Jadwal",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Jadwal Perawatan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentGreen
                )

                Spacer(Modifier.height(12.dp))

                schedule?.let { currentSchedule ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(currentSchedule.scheduleItems) { index, item ->
                            // Alternate colors: Even index = Green (CardGreen), Odd index = Brown (CardBrown)
                            // Note: index starts at 0. 0=Green, 1=Brown, 2=Green...
                            val backgroundColor = if (index % 2 == 0) CardGreen else CardBrown
                            ScheduleItemCard(item, backgroundColor)
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            "Klik tombol di atas untuk membuat jadwal",
                            color = MutedText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        ImprovedDatePickerDialog(
            currentDate = startDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                startDate = date
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun ScheduleItemCard(item: ScheduleItem, backgroundColor: Color) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id"))
    val formattedDate = item.date.format(dateFormatter)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor), // Use dynamic background color
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon background should contrast with card background. 
            // If card is Green(light), icon bg could be White? Or kept as CardGreen (might blend in)?
            // Let's make icon background White for better contrast on colored cards.
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.6f)), 
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        item.activity.contains("Pemupukan") -> Icons.Default.LocalFlorist
                        item.activity.contains("Penyiraman") || item.activity.contains("Perawatan") -> Icons.Default.WaterDrop
                        else -> Icons.Default.Autorenew
                    },
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.activity,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentGreen,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(4.dp))
                Text("Hari ke-${item.day} ($formattedDate)", color = MutedText, fontSize = 13.sp)
                if (item.description.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(item.description, color = MutedText, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ImprovedDatePickerDialog(
    currentDate: Date,
    onDismiss: () -> Unit,
    onDateSelected: (Date) -> Unit
) {
    val calendar = remember { Calendar.getInstance().apply { time = currentDate } }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                calendar.set(selectedYear, selectedMonth, selectedDay)
                onDateSelected(calendar.time)
            }) {
                Text("OK", color = AccentGreen, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = MutedText)
            }
        },
        title = {
            Text("Pilih Tanggal", fontWeight = FontWeight.Bold, color = AccentGreen)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Year selector
                Text("Tahun: $selectedYear", fontSize = 14.sp, color = MutedText)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (selectedYear > 2020) selectedYear-- }) {
                        Icon(Icons.Default.KeyboardArrowLeft, null, tint = AccentGreen)
                    }
                    Text(
                        selectedYear.toString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                    IconButton(onClick = { if (selectedYear < 2030) selectedYear++ }) {
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = AccentGreen)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Month selector
                Text("Bulan: ${selectedMonth + 1}", fontSize = 14.sp, color = MutedText)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (selectedMonth > 0) selectedMonth-- }) {
                        Icon(Icons.Default.KeyboardArrowLeft, null, tint = AccentGreen)
                    }
                    Text(
                        (selectedMonth + 1).toString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                    IconButton(onClick = { if (selectedMonth < 11) selectedMonth++ }) {
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = AccentGreen)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Day selector
                Text("Hari: $selectedDay", fontSize = 14.sp, color = MutedText)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (selectedDay > 1) selectedDay-- }) {
                        Icon(Icons.Default.KeyboardArrowLeft, null, tint = AccentGreen)
                    }
                    Text(
                        selectedDay.toString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                    IconButton(onClick = { if (selectedDay < 31) selectedDay++ }) {
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = AccentGreen)
                    }
                }
            }
        },
        containerColor = BackgroundCream,
        shape = RoundedCornerShape(20.dp)
    )
}
