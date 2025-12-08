package com.example.agrifarm_sitani.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrifarm_sitani.calculator.SeedCalculator
import com.example.agrifarm_sitani.model.GenerasiBibit
import com.example.agrifarm_sitani.data.database.AppDatabase
import com.example.agrifarm_sitani.data.entity.SeedCalculationEntity
import com.example.agrifarm_sitani.navigation.Screen
import kotlinx.coroutines.launch

private val BackgroundCream = Color(0xFFF5EEDC)
private val AccentGreen = Color(0xFF5D8B63)
private val Green = Color(0xFF144224)
private val SoftGreen = Color(0xFFAED4B3)
private val MutedText = Color(0xFF6B6B6B)
private val CardBrown = Color(0xFFDDB892)
private val CardGreen = Color(0xFFEAF3EC)
private val TextCharcoal = Color(0xFF333333)
private val TextDarkSlate = Color(0xFF2D3436)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedCalculatorScreen(onNavigateBack: () -> Unit = {}, onNavigateToHistory: () -> Unit = {}) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Hitung Kebutuhan Bibit", "Hitung Luas Lahan")
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember {
        androidx.room.Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "agrifarm_database"
        )
        .allowMainThreadQueries() // Use coroutines in real app
        .build()
    }
    val dao = remember { database.seedCalculationDao() }
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = AccentGreen)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Kalkulator Bibit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onNavigateToHistory) {
                     Icon(Icons.Default.History, "Riwayat", tint = AccentGreen)
                }
            }

            Spacer(Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = AccentGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SoftGreen
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        selectedContentColor = AccentGreen,
                        unselectedContentColor = MutedText
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            when (selectedTab) {
                0 -> NormalCalculatorTab(dao, coroutineScope)
                1 -> ReverseCalculatorTab(dao, coroutineScope)
            }
        }
    }
}

@Composable
fun NormalCalculatorTab(dao: com.example.agrifarm_sitani.data.dao.SeedCalculationDao, scope: kotlinx.coroutines.CoroutineScope) {
    var panjangLahan by remember { mutableStateOf("") }
    var lebarLahan by remember { mutableStateOf("") }
    var lebarGuludan by remember { mutableStateOf("80") }
    var lebarParit by remember { mutableStateOf("") }
    var jarakTanam by remember { mutableStateOf("") }
    var selectedGenerasi by remember { mutableStateOf(GenerasiBibit.G3) }
    var jumlahBibitPerKg by remember { mutableStateOf("") }
    var estimasiHarga by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("kg") }
    var result by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ){
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StyledTextField(value = panjangLahan, onValueChange = { panjangLahan = it }, label = "Panjang Lahan (meter)")
                StyledTextField(value = lebarLahan, onValueChange = { lebarLahan = it }, label = "Lebar Lahan (meter)")
                StyledTextField(value = lebarGuludan, onValueChange = { lebarGuludan = it }, label = "Lebar Guludan (cm)")
                StyledTextField(value = lebarParit, onValueChange = { lebarParit = it }, label = "Lebar Parit (cm)")
                StyledTextField(value = jarakTanam, onValueChange = { jarakTanam = it }, label = "Jarak Tanam (cm)")

                Text("Generasi Bibit:", style = MaterialTheme.typography.labelMedium, color = AccentGreen)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GenerasiBibit.entries.forEach { gen ->
                        FilterChip(
                            selected = selectedGenerasi == gen,
                            onClick = { selectedGenerasi = gen },
                            label = { Text(gen.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SoftGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (selectedGenerasi != GenerasiBibit.G0) {
                    StyledTextField(value = jumlahBibitPerKg, onValueChange = { jumlahBibitPerKg = it }, label = "Jumlah Bibit per Kg (opsional)")
                }

                StyledTextField(value = estimasiHarga, onValueChange = { estimasiHarga = it }, label = "Estimasi Harga (opsional)")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedUnit == "kg",
                        onClick = { selectedUnit = "kg" },
                        label = { Text("per Kg") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SoftGreen, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = selectedUnit == "kuintal",
                        onClick = { selectedUnit = "kuintal" },
                        label = { Text("per Kuintal") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SoftGreen, selectedLabelColor = Color.White)
                    )
                }

                Button(
                    onClick = {
                        val calc = SeedCalculator.calculateSeeds(
                            panjangLahan.toDoubleOrNull() ?: 0.0,
                            lebarLahan.toDoubleOrNull() ?: 0.0,
                            lebarGuludan.toDoubleOrNull() ?: 80.0,
                            lebarParit.toDoubleOrNull() ?: 0.0,
                            jarakTanam.toDoubleOrNull() ?: 0.0,
                            selectedGenerasi, jumlahBibitPerKg.toIntOrNull(),
                            estimasiHarga.toDoubleOrNull(), selectedUnit
                        )
                        result = calc?.let {
                            val resultString = buildString {
                                appendLine("RINGKASAN LAHAN")
                                appendLine("Lebar Unit Tanam: ${String.format("%.2f", it.ringkasanLahan.lebarUnitTanam)} m")
                                appendLine("Jumlah Guludan: ${it.ringkasanLahan.jumlahGuludan} baris")
                                appendLine("Panjang per Guludan: ${String.format("%.2f", it.ringkasanLahan.panjangTanamPerGuludan)} m")
                                appendLine()
                                appendLine("KEBUTUHAN TANAM")
                                appendLine("Tanaman per Guludan: ${it.kebutuhanTanam.jumlahTanamanPerGuludan} pohon")
                                appendLine("Total Populasi: ${it.kebutuhanTanam.totalPopulasiTanaman} pohon")
                                appendLine()
                                appendLine("KEBUTUHAN BIBIT")
                                appendLine("Estimasi: ${it.kebutuhanBibit.estimasi}")
                                it.kebutuhanBibit.rangeKg?.let { r -> appendLine("Range: ${r.kgMin} - ${r.kgMax} kg") }
                                appendLine("Note: ${it.kebutuhanBibit.note}")
                                it.estimasiBiaya?.let { b -> appendLine("\nESTIMASI BIAYA\nTotal: ${b.total}") }
                            }

                            // Save to history
                            scope.launch {
                                val inputs = "Lahan: ${panjangLahan}x${lebarLahan}m, Jarak: ${jarakTanam}cm, Gen: ${selectedGenerasi.displayName}"
                                dao.insertCalculation(SeedCalculationEntity(
                                    type = "Kebutuhan Bibit",
                                    inputDetails = inputs,
                                    resultSummary = resultString
                                ))
                            }

                            resultString
                        } ?: "Input tidak valid"
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftGreen)
                ) {
                    Text("Hitung & Simpan", color = Color.White, fontSize = 16.sp)
                }


            }
        }

        result?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(text = it, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }


}

@Composable
fun ReverseCalculatorTab(dao: com.example.agrifarm_sitani.data.dao.SeedCalculationDao, scope: kotlinx.coroutines.CoroutineScope) {
    var jumlahBibit by remember { mutableStateOf("") }
    var jarakTanam by remember { mutableStateOf("") }
    var lebarGuludan by remember { mutableStateOf("80") }
    var lebarParit by remember { mutableStateOf("") }
    var selectedGenerasi by remember { mutableStateOf(GenerasiBibit.G3) }
    var jumlahPerKg by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var calculationResultRaw by remember { mutableStateOf<Any?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ){
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StyledTextField(value = jumlahBibit, onValueChange = { jumlahBibit = it }, label = "Jumlah Bibit (${if (selectedGenerasi == GenerasiBibit.G0) "biji" else "kg"})")
                StyledTextField(value = jarakTanam, onValueChange = { jarakTanam = it }, label = "Jarak Tanam (cm)")
                StyledTextField(value = lebarGuludan, onValueChange = { lebarGuludan = it }, label = "Lebar Guludan (cm)")
                StyledTextField(value = lebarParit, onValueChange = { lebarParit = it }, label = "Lebar Parit (cm)")

                Text("Generasi Bibit:", style = MaterialTheme.typography.labelMedium, color = AccentGreen)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GenerasiBibit.entries.forEach { gen ->
                        FilterChip(
                            selected = selectedGenerasi == gen,
                            onClick = { selectedGenerasi = gen },
                            label = { Text(gen.displayName) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SoftGreen, selectedLabelColor = Color.White)
                        )
                    }
                }

                if (selectedGenerasi != GenerasiBibit.G0) {
                    StyledTextField(value = jumlahPerKg, onValueChange = { jumlahPerKg = it }, label = "Jumlah Bibit per Kg")
                }

                Button(
                    onClick = {
                        val calc = SeedCalculator.calculateReverseSeeds(
                            jumlahBibit.toDoubleOrNull() ?: 0.0,
                            jarakTanam.toDoubleOrNull() ?: 0.0,
                            lebarGuludan.toDoubleOrNull() ?: 80.0,
                            lebarParit.toDoubleOrNull() ?: 0.0,
                            selectedGenerasi, jumlahPerKg.toIntOrNull()
                        )
                        calculationResultRaw = calc
                        result = calc?.let {
                            val resultString = buildString {
                                appendLine("RINGKASAN")
                                appendLine("Estimasi Luas: ${String.format("%.1f", it.ringkasan.estimasiLuasM2)} m²")
                                appendLine("Jumlah Guludan: ${it.ringkasan.jumlahGuludan} baris")
                                appendLine("Panjang per Guludan: ${String.format("%.1f", it.ringkasan.panjangPerGuludan)} m")
                                appendLine()
                                appendLine("ESTIMASI POPULASI")
                                appendLine("Total Tanaman: ${it.estimasiPopulasi.totalTanaman} pohon")
                                appendLine()
                                appendLine(it.estimasiPopulasi.note)
                            }

                            // Save to history
                            scope.launch {
                                val inputs = "Bibit: ${jumlahBibit}, Jarak: ${jarakTanam}cm, Gen: ${selectedGenerasi.displayName}"
                                dao.insertCalculation(SeedCalculationEntity(
                                    type = "Luas Lahan",
                                    inputDetails = inputs,
                                    resultSummary = resultString
                                ))
                            }

                            resultString
                        } ?: "Input tidak valid"
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftGreen)
                ) {
                    Text("Hitung & Simpan", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        result?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(text = it, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
            }

        }
    }
}

@Composable
private fun StyledTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = AccentGreen
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            color = MutedText
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SoftGreen,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedLabelColor = AccentGreen
        )
    )
}

@Composable
private fun ResultDataCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color = Color.White
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = AccentGreen,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconBgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
