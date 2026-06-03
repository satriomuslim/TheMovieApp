package com.main.themovieapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.main.themovieapp.presentation.theme.NeonPink
import com.main.themovieapp.presentation.theme.NeonPurple

@Composable
fun GenreChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    val gradientBrush = Brush.horizontalGradient(listOf(NeonPink, NeonPurple))
    val chipModifier = if (isSelected) {
        Modifier.background(brush = gradientBrush, shape = RoundedCornerShape(20.dp))
    } else {
        Modifier
            .background(Color.Transparent)
            .border(1.dp, NeonPurple, RoundedCornerShape(20.dp))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .then(chipModifier)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}