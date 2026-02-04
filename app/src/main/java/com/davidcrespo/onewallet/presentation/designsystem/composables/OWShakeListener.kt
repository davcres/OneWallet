package com.davidcrespo.onewallet.presentation.designsystem.composables

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.math.abs

@Composable
fun OWShakeListener(
    enabled: Boolean = true,
    onShake: () -> Unit,
    threshold: Float = 5f,      // sensibilidad (2.2–3.2 típico)
    slopTimeMs: Long = 1000L       // evita múltiples disparos seguidos
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val onShakeState by rememberUpdatedState(onShake)

    DisposableEffect(context, lifecycleOwner, enabled, threshold, slopTimeMs) {
        if (!enabled) return@DisposableEffect onDispose { }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accel == null) return@DisposableEffect onDispose { }

        var hasBaseline = false
        var lastX = 0f
        var lastY = 0f
        var lastZ = 0f
        var lastShakeTime = 0L

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                if (!hasBaseline) { // avoid register first event on open app
                    lastX = x; lastY = y; lastZ = z
                    hasBaseline = true
                    return
                }

                val delta = abs(x - lastX) + abs(y - lastY) + abs(z - lastZ)
                lastX = x; lastY = y; lastZ = z

                val now = System.currentTimeMillis()
                if (delta > threshold && (now - lastShakeTime) > slopTimeMs) {
                    lastShakeTime = now
                    onShakeState()
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        fun register() {
            sensorManager.registerListener(listener, accel, SensorManager.SENSOR_DELAY_GAME)
        }
        fun unregister() {
            sensorManager.unregisterListener(listener)
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> register()
                Lifecycle.Event.ON_PAUSE -> unregister()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        // Si ya estás resumed cuando entra el composable, registra ya
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            register()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            unregister()
        }
    }
}
