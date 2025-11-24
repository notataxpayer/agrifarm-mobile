package com.example.agrifarm_sitani.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrifarm_sitani.data.database.AppDatabase
import com.example.agrifarm_sitani.data.entity.SeedCalculationEntity
import java.text.SimpleDateFormat
import java.util.*

private val BackgroundCream = Color(0xFFFAF7F0)
private val AccentGreen = Color(0xFF2F6B4B)
private val SoftGreen = Color(0xFF9BC6A6)
private val CardGreen = Color(0xFFEAF3EC)
private val MutedText = Color(0xFF6B6B6B)

@Composable
fun CalculationHistoryScreen(onNavigateBack: () -> Unit) {
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
    val dao = remember { database.seedCalculationDao() }
    val historyState = dao.getAllCalculations().collectAsState(initial = emptyList())

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .padding(12.dp)
            .border(6.dp, AccentGreen, RoundedCornerShape(24.dp)),
        color = BackgroundCream
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
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
                    "Riwayat Perhitungan",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen
                )
            }

            Spacer(Modifier.height(16.dp))

            if (historyState.value.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat perhitungan", color = MutedText)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(historyState.value) { item ->
                        HistoryCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: SeedCalculationEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.type,
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date(item.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.inputDetails,
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = CardGreen
            )
            Text(
                text = item.resultSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}
