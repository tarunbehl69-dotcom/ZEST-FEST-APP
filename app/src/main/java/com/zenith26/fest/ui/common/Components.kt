package com.zenith26.fest.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Amber300
import com.zenith26.fest.ui.theme.Amber400
import com.zenith26.fest.ui.theme.Amber500
import com.zenith26.fest.ui.theme.Blue400
import com.zenith26.fest.ui.theme.Blue500
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Emerald500
import com.zenith26.fest.ui.theme.Indigo400
import com.zenith26.fest.ui.theme.Pink400
import com.zenith26.fest.ui.theme.Pink500
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Red400
import com.zenith26.fest.ui.theme.Red500
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800

/**
 * Color configuration for category badge styling.
 */
data class CategoryStyle(
    val textColor: Color,
    val backgroundColor: Color,
    val borderColor: Color
)

fun getCategoryStyle(category: String): CategoryStyle {
    return when (category.trim().lowercase()) {
        "technical" -> CategoryStyle(
            textColor = Blue400,
            backgroundColor = Blue500.copy(alpha = 0.12f),
            borderColor = Blue500.copy(alpha = 0.35f)
        )
        "cultural" -> CategoryStyle(
            textColor = Pink400,
            backgroundColor = Pink500.copy(alpha = 0.12f),
            borderColor = Pink500.copy(alpha = 0.35f)
        )
        "sports" -> CategoryStyle(
            textColor = Emerald400,
            backgroundColor = Emerald500.copy(alpha = 0.12f),
            borderColor = Emerald500.copy(alpha = 0.35f)
        )
        "gaming" -> CategoryStyle(
            textColor = Purple400,
            backgroundColor = Purple500.copy(alpha = 0.12f),
            borderColor = Purple500.copy(alpha = 0.35f)
        )
        "workshop" -> CategoryStyle(
            textColor = Amber400,
            backgroundColor = Amber500.copy(alpha = 0.12f),
            borderColor = Amber500.copy(alpha = 0.35f)
        )
        else -> CategoryStyle(
            textColor = Slate400,
            backgroundColor = Slate700.copy(alpha = 0.2f),
            borderColor = Slate700.copy(alpha = 0.4f)
        )
    }
}

/**
 * Category Pill Badge matching the original Tailwind styling.
 */
@Composable
fun CategoryBadge(
    category: String,
    modifier: Modifier = Modifier
) {
    val style = getCategoryStyle(category)
    Box(
        modifier = modifier
            .background(style.backgroundColor, RoundedCornerShape(100.dp))
            .border(BorderStroke(1.dp, style.borderColor), RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = category.uppercase(),
            color = style.textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

/**
 * Status Badge for event capacity (FULL / >90% Cap / Open).
 */
@Composable
fun StatusBadge(
    isFull: Boolean,
    isHighDemand: Boolean,
    availableSeats: Int,
    modifier: Modifier = Modifier
) {
    when {
        isFull -> {
            Box(
                modifier = modifier
                    .background(Red500.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, Red500.copy(alpha = 0.35f)), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "FULL",
                    color = Red400,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            }
        }
        isHighDemand -> {
            Box(
                modifier = modifier
                    .background(Amber500.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, Amber500.copy(alpha = 0.35f)), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "⚠️ >90% CAP",
                    color = Amber300,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }
        }
        else -> {
            Box(
                modifier = modifier
                    .background(Emerald500.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, Emerald500.copy(alpha = 0.25f)), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "OPEN ($availableSeats)",
                    color = Emerald400,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Gradient text representing the hero heading.
 */
@Composable
fun GradientText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Purple400, Pink400, Indigo400)
    )
    Text(
        text = text,
        style = style.copy(brush = gradientBrush),
        modifier = modifier
    )
}
