package com.example.echopath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.echopath.data.PreferencesRepository
import com.example.echopath.game.GameEngine
import com.example.echopath.game.HapticManager
import com.example.echopath.game.ParticleManager
import com.example.echopath.game.SoundManager
import com.example.echopath.model.GameSettings
import com.example.echopath.ui.navigation.Screen
import com.example.echopath.ui.screens.AboutScreen
import com.example.echopath.ui.screens.GameScreen
import com.example.echopath.ui.screens.HomeScreen
import com.example.echopath.ui.screens.ResultScreen
import com.example.echopath.ui.screens.SettingsScreen
import com.example.echopath.ui.screens.SplashScreen
import com.example.echopath.ui.theme.EchoPathTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var hapticManager: HapticManager
    private lateinit var particleManager: ParticleManager
    private lateinit var gameEngine: GameEngine
    private lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferencesRepository = PreferencesRepository(applicationContext)
        soundManager = SoundManager()
        hapticManager = HapticManager(applicationContext)
        particleManager = ParticleManager()
        gameEngine = GameEngine(soundManager, hapticManager, particleManager)

        setContent {
            val settings by preferencesRepository.settingsFlow.collectAsState(initial = GameSettings())
            val bestRound by preferencesRepository.bestRoundFlow.collectAsState(initial = 1)
            val scope = rememberCoroutineScope()

            // Sync settings with managers
            soundManager.isEnabled = settings.soundEnabled
            hapticManager.isEnabled = settings.hapticsEnabled
            particleManager.isEnabled = settings.particlesEnabled
            particleManager.reducedEffects = settings.reducedEffects

            var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

            // Pause game when app is backgrounded
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_PAUSE && currentScreen == Screen.Game) {
                        gameEngine.pause()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            EchoPathTheme(themeMode = settings.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(300))
                        },
                        label = "screenTransition"
                    ) { target ->
                        when (target) {
                            Screen.Splash -> {
                                SplashScreen(
                                    onSplashFinished = {
                                        currentScreen = Screen.Home
                                    }
                                )
                            }

                            Screen.Home -> {
                                HomeScreen(
                                    bestRound = bestRound,
                                    soundManager = soundManager,
                                    hapticManager = hapticManager,
                                    onPlayClick = {
                                        gameEngine.initNewGame(bestRound, settings.tutorialCompleted)
                                        currentScreen = Screen.Game
                                    },
                                    onSettingsClick = {
                                        currentScreen = Screen.Settings
                                    },
                                    onAboutClick = {
                                        currentScreen = Screen.About
                                    }
                                )
                            }

                            Screen.Game -> {
                                GameScreen(
                                    gameEngine = gameEngine,
                                    ghostTrailsEnabled = settings.ghostTrailsEnabled,
                                    onGameOver = {
                                        val stats = gameEngine.currentStats
                                        if (stats.roundSurvived > bestRound) {
                                            scope.launch {
                                                preferencesRepository.updateBestRound(stats.roundSurvived)
                                            }
                                        }
                                        if (!settings.tutorialCompleted && stats.roundSurvived >= 2) {
                                            scope.launch {
                                                preferencesRepository.setTutorialCompleted(true)
                                            }
                                        }
                                        currentScreen = Screen.Result
                                    },
                                    onHomeClick = {
                                        currentScreen = Screen.Home
                                    },
                                    onRestartClick = {
                                        gameEngine.initNewGame(bestRound, settings.tutorialCompleted)
                                    }
                                )
                            }

                            Screen.Result -> {
                                ResultScreen(
                                    stats = gameEngine.currentStats,
                                    soundManager = soundManager,
                                    hapticManager = hapticManager,
                                    onPlayAgainClick = {
                                        gameEngine.initNewGame(bestRound, settings.tutorialCompleted)
                                        currentScreen = Screen.Game
                                    },
                                    onHomeClick = {
                                        currentScreen = Screen.Home
                                    }
                                )
                            }

                            Screen.Settings -> {
                                SettingsScreen(
                                    settings = settings,
                                    soundManager = soundManager,
                                    hapticManager = hapticManager,
                                    onThemeModeChange = { mode ->
                                        scope.launch { preferencesRepository.setThemeMode(mode) }
                                    },
                                    onSoundToggle = { enabled ->
                                        scope.launch { preferencesRepository.setSoundEnabled(enabled) }
                                    },
                                    onHapticsToggle = { enabled ->
                                        scope.launch { preferencesRepository.setHapticsEnabled(enabled) }
                                    },
                                    onParticlesToggle = { enabled ->
                                        scope.launch { preferencesRepository.setParticlesEnabled(enabled) }
                                    },
                                    onGhostTrailsToggle = { enabled ->
                                        scope.launch { preferencesRepository.setGhostTrailsEnabled(enabled) }
                                    },
                                    onReducedEffectsToggle = { enabled ->
                                        scope.launch { preferencesRepository.setReducedEffects(enabled) }
                                    },
                                    onResetBestScore = {
                                        scope.launch { preferencesRepository.resetBestRound() }
                                    },
                                    onBackClick = {
                                        currentScreen = Screen.Home
                                    }
                                )
                            }

                            Screen.About -> {
                                AboutScreen(
                                    soundManager = soundManager,
                                    hapticManager = hapticManager,
                                    onBackClick = {
                                        currentScreen = Screen.Home
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}