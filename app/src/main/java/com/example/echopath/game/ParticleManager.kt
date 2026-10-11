package com.example.echopath.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.PlayerCyan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class GameParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var color: Color,
    var alpha: Float,
    var life: Float,
    val maxLife: Float,
    val isRing: Boolean = false
)

class ParticleManager {

    private val particles = ArrayList<GameParticle>(250)
    private val random = Random(System.currentTimeMillis())

    @Volatile
    var isEnabled: Boolean = true

    @Volatile
    var reducedEffects: Boolean = false

    fun spawnPlayerTrail(pos: Offset) {
        if (!isEnabled) return
        val count = if (reducedEffects) 1 else 2
        for (i in 0 until count) {
            val angle = random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = random.nextFloat() * 0.02f
            particles.add(
                GameParticle(
                    x = pos.x + (random.nextFloat() - 0.5f) * 0.015f,
                    y = pos.y + (random.nextFloat() - 0.5f) * 0.015f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    radius = random.nextFloat() * 3.5f + 2.0f,
                    color = PlayerCyan,
                    alpha = 0.85f,
                    life = 0.35f,
                    maxLife = 0.35f
                )
            )
        }
    }

    fun spawnGhostTrail(pos: Offset, color: Color) {
        if (!isEnabled || reducedEffects) return
        if (random.nextFloat() < 0.45f) {
            particles.add(
                GameParticle(
                    x = pos.x + (random.nextFloat() - 0.5f) * 0.01f,
                    y = pos.y + (random.nextFloat() - 0.5f) * 0.01f,
                    vx = (random.nextFloat() - 0.5f) * 0.01f,
                    vy = (random.nextFloat() - 0.5f) * 0.01f,
                    radius = random.nextFloat() * 2.5f + 1.5f,
                    color = color,
                    alpha = 0.5f,
                    life = 0.3f,
                    maxLife = 0.3f
                )
            )
        }
    }

    fun spawnEchoCreatedPulse(pos: Offset, color: Color) {
        if (!isEnabled) return
        // Expanding temporal rings
        particles.add(
            GameParticle(
                x = pos.x,
                y = pos.y,
                vx = 0f,
                vy = 0f,
                radius = 8f,
                color = color,
                alpha = 0.9f,
                life = 0.65f,
                maxLife = 0.65f,
                isRing = true
            )
        )
        val count = if (reducedEffects) 12 else 24
        for (i in 0 until count) {
            val angle = (i.toFloat() / count) * 2f * Math.PI.toFloat() + (random.nextFloat() * 0.2f)
            val speed = random.nextFloat() * 0.12f + 0.04f
            particles.add(
                GameParticle(
                    x = pos.x,
                    y = pos.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    radius = random.nextFloat() * 4f + 2f,
                    color = color,
                    alpha = 0.9f,
                    life = 0.55f,
                    maxLife = 0.55f
                )
            )
        }
    }

    fun spawnCloseCallSparks(pos: Offset) {
        if (!isEnabled) return
        val count = if (reducedEffects) 4 else 8
        for (i in 0 until count) {
            val angle = random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = random.nextFloat() * 0.08f + 0.02f
            particles.add(
                GameParticle(
                    x = pos.x,
                    y = pos.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    radius = random.nextFloat() * 3f + 1.5f,
                    color = NeonCyan,
                    alpha = 0.8f,
                    life = 0.28f,
                    maxLife = 0.28f
                )
            )
        }
    }

    fun spawnCollisionBurst(pos: Offset) {
        if (!isEnabled) return
        // Shockwave ring
        particles.add(
            GameParticle(
                x = pos.x,
                y = pos.y,
                vx = 0f,
                vy = 0f,
                radius = 12f,
                color = Color.White,
                alpha = 1f,
                life = 0.7f,
                maxLife = 0.7f,
                isRing = true
            )
        )
        // High speed shattered shards
        val count = if (reducedEffects) 22 else 45
        for (i in 0 until count) {
            val angle = random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = random.nextFloat() * 0.22f + 0.06f
            val col = if (i % 2 == 0) Color.White else PlayerCyan
            particles.add(
                GameParticle(
                    x = pos.x,
                    y = pos.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    radius = random.nextFloat() * 5f + 2.5f,
                    color = col,
                    alpha = 1f,
                    life = random.nextFloat() * 0.4f + 0.4f,
                    maxLife = 0.8f
                )
            )
        }
    }

    fun update(dt: Float) {
        if (!isEnabled && particles.isEmpty()) return

        var i = 0
        while (i < particles.size) {
            val p = particles[i]
            p.life -= dt
            if (p.life <= 0f) {
                particles.removeAt(i)
                continue
            }

            p.x += p.vx * dt
            p.y += p.vy * dt

            val progress = (p.life / p.maxLife).coerceIn(0f, 1f)
            p.alpha = progress

            if (p.isRing) {
                p.radius += 110f * dt
            }

            i++
        }
    }

    fun draw(drawScope: DrawScope, arenaWidth: Float, arenaHeight: Float, arenaLeft: Float, arenaTop: Float) {
        if (!isEnabled || particles.isEmpty()) return

        for (p in particles) {
            val px = arenaLeft + p.x * arenaWidth
            val py = arenaTop + p.y * arenaHeight
            val drawColor = p.color.copy(alpha = (p.color.alpha * p.alpha).coerceIn(0f, 1f))

            if (p.isRing) {
                drawScope.drawCircle(
                    color = drawColor,
                    radius = p.radius,
                    center = Offset(px, py),
                    style = Stroke(width = 3.5f * p.alpha)
                )
            } else {
                drawScope.drawCircle(
                    color = drawColor,
                    radius = p.radius,
                    center = Offset(px, py)
                )
            }
        }
    }

    fun clear() {
        particles.clear()
    }
}
