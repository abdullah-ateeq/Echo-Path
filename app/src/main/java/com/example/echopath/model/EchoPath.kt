package com.example.echopath.model

import androidx.compose.ui.geometry.Offset

data class EchoPath(
    val generation: Int,
    val points: List<PathPoint>,
    val durationMs: Long = 5000L
) {
    /**
     * Finds interpolated position of ghost at the given round elapsed time in milliseconds.
     */
    fun getPositionAt(timeMs: Long): Offset {
        if (points.isEmpty()) return Offset(0.5f, 0.5f)
        if (points.size == 1) return points[0].toOffset()

        val clampedTime = timeMs.coerceIn(0L, durationMs)
        if (clampedTime <= points.first().timeMs) return points.first().toOffset()
        if (clampedTime >= points.last().timeMs) return points.last().toOffset()

        val searchIndex = points.binarySearch { it.timeMs.compareTo(clampedTime) }
        if (searchIndex >= 0) {
            return points[searchIndex].toOffset()
        }

        val insertionPoint = -(searchIndex + 1)
        val p0 = points[(insertionPoint - 1).coerceAtLeast(0)]
        val p1 = points[insertionPoint.coerceAtMost(points.lastIndex)]

        val span = (p1.timeMs - p0.timeMs).coerceAtLeast(1L)
        val fraction = ((clampedTime - p0.timeMs).toFloat() / span.toFloat()).coerceIn(0f, 1f)

        val x = p0.x + (p1.x - p0.x) * fraction
        val y = p0.y + (p1.y - p0.y) * fraction
        return Offset(x, y)
    }

    /**
     * Returns the recent trail points leading up to the current time within [trailWindowMs].
     */
    fun getRecentTrailOffsets(currentTimeMs: Long, trailWindowMs: Long = 750L): List<Offset> {
        if (points.isEmpty()) return emptyList()

        val startTime = (currentTimeMs - trailWindowMs).coerceAtLeast(0L)
        val result = mutableListOf<Offset>()

        for (p in points) {
            if (p.timeMs in startTime..currentTimeMs) {
                result.add(p.toOffset())
            }
        }

        // Add current interpolated head
        val currentHead = getPositionAt(currentTimeMs)
        result.add(currentHead)

        return result
    }
}
