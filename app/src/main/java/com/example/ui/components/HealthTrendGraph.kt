package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.HealthHistoryLog
import com.example.ui.theme.AttentionAmber
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.HealthyGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HealthTrendGraph(
    healthLogs: List<HealthHistoryLog>,
    modifier: Modifier = Modifier
) {
    if (healthLogs.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No health records yet. Scan to start tracking!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    // Ensure logs are sorted chronologically
    val sortedLogs = remember(healthLogs) { healthLogs.sortedBy { it.timestamp } }
    var selectedIndex by remember(sortedLogs) { mutableStateOf(sortedLogs.size - 1) }
    val selectedLog = sortedLogs.getOrNull(selectedIndex) ?: sortedLogs.last()

    val primaryLineColor = HealthyGreen
    val surfaceColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("health_trend_graph")
    ) {
        // Graph Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(HealthyGreen.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Health Trend",
                        tint = HealthyGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Health Progress Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Interactive checkup trend over time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Current / selected score pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when {
                    selectedLog.healthScore >= 80 -> HealthyGreen.copy(alpha = 0.15f)
                    selectedLog.healthScore >= 50 -> AttentionAmber.copy(alpha = 0.15f)
                    else -> CriticalRed.copy(alpha = 0.15f)
                }
            ) {
                Text(
                    text = "${selectedLog.healthScore}%",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = when {
                        selectedLog.healthScore >= 80 -> HealthyGreen
                        selectedLog.healthScore >= 50 -> AttentionAmber
                        else -> CriticalRed
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected point detail tooltip card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedLog.statusNote.ifBlank { "Routine health assessment" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(selectedLog.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Tap node to inspect",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Canvas Line Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .pointerInput(sortedLogs) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val paddingH = 32f
                            val usableWidth = width - (paddingH * 2)
                            val count = sortedLogs.size
                            if (count <= 1) return@detectTapGestures

                            val stepX = usableWidth / (count - 1)
                            val tappedIndex = ((offset.x - paddingH + (stepX / 2)) / stepX)
                                .toInt()
                                .coerceIn(0, count - 1)
                            selectedIndex = tappedIndex
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val paddingH = 32f
                val paddingV = 24f
                val usableWidth = width - (paddingH * 2)
                val usableHeight = height - (paddingV * 2)

                // Background threshold reference lines (80% and 50%)
                val y80 = paddingV + (1f - 0.8f) * usableHeight
                val y50 = paddingV + (1f - 0.5f) * usableHeight

                // Draw dashed guide lines
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    start = Offset(paddingH, y80),
                    end = Offset(width - paddingH, y80),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    start = Offset(paddingH, y50),
                    end = Offset(width - paddingH, y50),
                    strokeWidth = 1.dp.toPx()
                )

                if (sortedLogs.size == 1) {
                    // Single entry: draw centered circle
                    val score = sortedLogs[0].healthScore.coerceIn(0, 100)
                    val y = paddingV + (1f - (score / 100f)) * usableHeight
                    val x = width / 2f

                    drawCircle(
                        color = HealthyGreen,
                        radius = 8.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                    return@Canvas
                }

                val count = sortedLogs.size
                val stepX = usableWidth / (count - 1)

                val points = sortedLogs.mapIndexed { idx, log ->
                    val x = paddingH + (idx * stepX)
                    val scoreNorm = log.healthScore.coerceIn(0, 100) / 100f
                    val y = paddingV + (1f - scoreNorm) * usableHeight
                    Offset(x, y)
                }

                // Construct line path & gradient area path
                val linePath = Path()
                val areaPath = Path()

                points.forEachIndexed { i, pt ->
                    if (i == 0) {
                        linePath.moveTo(pt.x, pt.y)
                        areaPath.moveTo(pt.x, height - paddingV)
                        areaPath.lineTo(pt.x, pt.y)
                    } else {
                        // Smooth cubic curve between adjacent points
                        val prev = points[i - 1]
                        val midX = (prev.x + pt.x) / 2f
                        linePath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                        areaPath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                    }
                }

                areaPath.lineTo(points.last().x, height - paddingV)
                areaPath.close()

                // Draw gradient underneath the curve
                drawPath(
                    path = areaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryLineColor.copy(alpha = 0.35f),
                            primaryLineColor.copy(alpha = 0.02f)
                        ),
                        startY = paddingV,
                        endY = height - paddingV
                    )
                )

                // Draw the stroke line
                drawPath(
                    path = linePath,
                    color = primaryLineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw each point
                points.forEachIndexed { idx, pt ->
                    val isSelected = idx == selectedIndex
                    val pointColor = when {
                        sortedLogs[idx].healthScore >= 80 -> HealthyGreen
                        sortedLogs[idx].healthScore >= 50 -> AttentionAmber
                        else -> CriticalRed
                    }

                    if (isSelected) {
                        // Halo for active point
                        drawCircle(
                            color = pointColor.copy(alpha = 0.25f),
                            radius = 12.dp.toPx(),
                            center = pt
                        )
                    }

                    drawCircle(
                        color = pointColor,
                        radius = if (isSelected) 6.dp.toPx() else 4.5.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 3.dp.toPx() else 2.dp.toPx(),
                        center = pt
                    )
                }
            }
        }

        // Timeline labels below graph
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val dateFmt = SimpleDateFormat("MMM d", Locale.getDefault())
            Text(
                text = dateFmt.format(Date(sortedLogs.first().timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (sortedLogs.size > 2) {
                Text(
                    text = "Tracked Progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Text(
                text = dateFmt.format(Date(sortedLogs.last().timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
