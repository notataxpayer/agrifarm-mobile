package com.example.agrifarm_sitani.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BackgroundCream = Color(0xFFFAF7F0)
private val AccentGreen = Color(0xFF2F6B4B)
private val SoftGreen = Color(0xFF9BC6A6)
private val CardGreen = Color(0xFFEAF3EC)
private val MutedText = Color(0xFF6B6B6B)

@Composable
fun HomeScreen(
    onNavigateToSeedCalculator: () -> Unit,
    onNavigateToPlantingCalendar: () -> Unit
) {
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text("TaniKu", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
            Text(
                "Asisten Pintar untuk Petani Modern",
                fontSize = 14.sp,
                color = MutedText,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            Box(
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF0F6F1)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(SoftGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onNavigateToPlantingCalendar,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftGreen),
                shape = RoundedCornerShape(36.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text("Ayo Mulai", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OptionCard(
                    title = "Jadwal Penanaman",
                    subtitle = "Atur jadwal perawatan bibit Anda",
                    iconTint = AccentGreen,
                    onClick = onNavigateToPlantingCalendar
                )
                OptionCard(
                    title = "Kalkulator Bibit",
                    subtitle = "Hitung kebutuhan benih & lahan",
                    iconTint = AccentGreen,
                    onClick = onNavigateToSeedCalculator
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun OptionCard(title: String, subtitle: String, iconTint: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = iconTint)
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = AccentGreen)
                Text(subtitle, color = MutedText, fontSize = 13.sp)
            }
        }
    }
}
