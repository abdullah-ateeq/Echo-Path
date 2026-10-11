package com.example.echopath

import androidx.compose.ui.geometry.Offset
import com.example.echopath.game.CollisionDetector
import com.example.echopath.model.EchoPath
import com.example.echopath.model.PathPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EchoPathGameLogicTest {

    @Test
    fun testEchoPathInterpolation() {
        val points = listOf(
            PathPoint(0.1f, 0.1f, 0L),
            PathPoint(0.5f, 0.5f, 1000L),
            PathPoint(0.9f, 0.9f, 2000L)
        )
        val echo = EchoPath(generation = 1, points = points, durationMs = 2000L)

        // At 0ms -> start point
        val pStart = echo.getPositionAt(0L)
        assertEquals(0.1f, pStart.x, 0.001f)
        assertEquals(0.1f, pStart.y, 0.001f)

        // At 500ms -> midpoint between 0ms and 1000ms: 0.3f
        val pMid1 = echo.getPositionAt(500L)
        assertEquals(0.3f, pMid1.x, 0.001f)
        assertEquals(0.3f, pMid1.y, 0.001f)

        // At 1000ms -> exact waypoint
        val p1000 = echo.getPositionAt(1000L)
        assertEquals(0.5f, p1000.x, 0.001f)
        assertEquals(0.5f, p1000.y, 0.001f)

        // At 1500ms -> midpoint between 1000ms and 2000ms: 0.7f
        val pMid2 = echo.getPositionAt(1500L)
        assertEquals(0.7f, pMid2.x, 0.001f)
        assertEquals(0.7f, pMid2.y, 0.001f)

        // Beyond 2000ms -> clamped to last
        val pEnd = echo.getPositionAt(2500L)
        assertEquals(0.9f, pEnd.x, 0.001f)
        assertEquals(0.9f, pEnd.y, 0.001f)
    }

    @Test
    fun testRecentTrailExtraction() {
        val points = listOf(
            PathPoint(0.1f, 0.1f, 100L),
            PathPoint(0.2f, 0.2f, 300L),
            PathPoint(0.3f, 0.3f, 600L),
            PathPoint(0.4f, 0.4f, 1000L)
        )
        val echo = EchoPath(generation = 1, points = points, durationMs = 1500L)

        // Window of 500ms at time 1000ms (range: 500ms to 1000ms)
        // Should include point at 600ms, 1000ms, plus current head
        val trail = echo.getRecentTrailOffsets(currentTimeMs = 1000L, trailWindowMs = 500L)
        assertTrue(trail.size >= 2)
    }

    @Test
    fun testCollisionDetection() {
        val player = Offset(0.5f, 0.5f)

        // Directly on top -> must collide
        assertTrue(CollisionDetector.checkCollision(player, Offset(0.5f, 0.5f)))

        // Well within forgiving radius (~0.057f)
        assertTrue(CollisionDetector.checkCollision(player, Offset(0.53f, 0.5f)))

        // Far away -> must not collide
        assertFalse(CollisionDetector.checkCollision(player, Offset(0.8f, 0.8f)))
    }

    @Test
    fun testCloseCallDetection() {
        val player = Offset(0.5f, 0.5f)

        // Right outside collision radius, but inside close call radius
        val closeGhost = Offset(0.5f, 0.5f + (CollisionDetector.COLLISION_RADIUS + 0.02f))
        assertFalse(CollisionDetector.checkCollision(player, closeGhost))
        assertTrue(CollisionDetector.isCloseCall(player, closeGhost))

        // Very far away -> not close call
        val farGhost = Offset(0.5f, 0.9f)
        assertFalse(CollisionDetector.isCloseCall(player, farGhost))
    }
}
