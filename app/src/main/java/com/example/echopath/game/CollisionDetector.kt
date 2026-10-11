package com.example.echopath.game

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

object CollisionDetector {

    const val PLAYER_RADIUS = 0.038f
    const val GHOST_RADIUS = 0.034f
    const val FORGIVENESS_FACTOR = 0.80f
    const val CLOSE_CALL_MULTIPLIER = 1.65f

    val COLLISION_RADIUS = (PLAYER_RADIUS + GHOST_RADIUS) * FORGIVENESS_FACTOR
    val CLOSE_CALL_RADIUS = (PLAYER_RADIUS + GHOST_RADIUS) * CLOSE_CALL_MULTIPLIER

    fun checkCollision(playerPos: Offset, ghostPos: Offset): Boolean {
        val dx = playerPos.x - ghostPos.x
        val dy = playerPos.y - ghostPos.y
        val distSq = dx * dx + dy * dy
        return distSq <= (COLLISION_RADIUS * COLLISION_RADIUS)
    }

    fun isCloseCall(playerPos: Offset, ghostPos: Offset): Boolean {
        val dx = playerPos.x - ghostPos.x
        val dy = playerPos.y - ghostPos.y
        val distSq = dx * dx + dy * dy
        val colRadiusSq = COLLISION_RADIUS * COLLISION_RADIUS
        val closeRadiusSq = CLOSE_CALL_RADIUS * CLOSE_CALL_RADIUS
        return distSq in colRadiusSq..closeRadiusSq
    }

    fun distance(p1: Offset, p2: Offset): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return sqrt(dx * dx + dy * dy)
    }
}
