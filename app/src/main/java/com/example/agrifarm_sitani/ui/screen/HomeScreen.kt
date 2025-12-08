package com.example.agrifarm_sitani.ui.screen

import com.example.agrifarm_sitani.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val BackgroundCream = Color(0xFFF5EEDC)
private val AccentGreen = Color(0xFF5D8B63)
private val Green = Color(0xFF144224)
private val SoftGreen = Color(0xFFAED4B3)
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
            .padding(24.dp),
        color = BackgroundCream
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text("TaniKu", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
            Text(
                "Asisten Pintar untuk Petani Modern",
                fontSize = 14.sp,
                color = MutedText,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.homescreen_image),
                contentDescription = "Ilustrasi Beranda TaniKu",
                modifier = Modifier
                    .height(353.dp)
                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(24.dp)),
//                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onNavigateToPlantingCalendar,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green),
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
                    onClick = onNavigateToPlantingCalendar,
                    icon = Icons.Default.DateRange,
                )
                OptionCard(
                    title = "Kalkulator Bibit",
                    subtitle = "Hitung kebutuhan benih & lahan",
                    iconTint = AccentGreen,
                    onClick = onNavigateToSeedCalculator,
                    icon = Icons.Default.Calculate,

                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun OptionCard(title: String, subtitle: String, iconTint: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(36.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = AccentGreen, fontSize = 16.sp)
                Text(subtitle, color = MutedText, fontSize = 13.sp)
            }
        }
    }
}
