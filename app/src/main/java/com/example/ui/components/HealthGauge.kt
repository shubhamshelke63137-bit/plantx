package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AttentionAmber
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.HealthyGreen

@Composable
fun HealthGauge(
    score: Int,
    statusLabel: String,
    modifier: Modifier = Modifier,
    size: Dp = 130.dp,
    strokeWidth: Dp = 10.dp
) {
    val clampedScore = score.coerceIn(0, 100)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedScore / 100f,
        animationSpec = tween(durationMillis = 900),
        label = "gaugeProgress"
    )

    val gaugeColor = when {
        clampedScore >= 80 -> HealthyGreen
        clampedScore >= 50 -> AttentionAmber
        else -> CriticalRed
    }

    val trackColor = gaugeColor.copy(alpha = 0.15f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val strokePx = strokeWidth.toPx()
                val radius = (this.size.minDimension - strokePx) / 2f
                val centerOffset = androidx.compose.ui.geometry.Offset(this.size.width / 2f, this.size.height / 2f)

                // Background track arc (240 degrees sweep starting from 150)
                drawArc(
                    color = trackColor,
                    startAngle = 140f,
                    sweepAngle = 260f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(centerOffset.x - radius, centerOffset.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Animated active score arc
                drawArc(
                    color = gaugeColor,
                    startAngle = 140f,
                    sweepAngle = 260f * animatedProgress,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(centerOffset.x - radius, centerOffset.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score%",
                    fontSize = if (size > 100.dp) 28.sp else 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Health",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = gaugeColor.copy(alpha = 0.12f)
        ) {
            Text(
                text = statusLabel,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = gaugeColor
            )
        }
    }
}
