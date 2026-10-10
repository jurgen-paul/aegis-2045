package com.example.ui.modifiers

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import com.example.neural.GestureVectorData
import com.example.neural.NeuralGestureIntentHandler
import kotlin.math.sqrt

/**
 * Compose Modifier that intercepts physical user gestures (drags, flings, holds, taps)
 * and feeds the computed spatial-temporal vectors directly into the [NeuralGestureIntentHandler].
 */
fun Modifier.interceptNeuralGestures(
    handler: NeuralGestureIntentHandler,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this

    val velocityTracker = remember { VelocityTracker() }
    var startOffset by remember { mutableStateOf(Offset.Zero) }
    var currentOffset by remember { mutableStateOf(Offset.Zero) }
    var touchStartTime by remember { mutableLongStateOf(0L) }
    val trailPoints = remember { mutableStateListOf<Offset>() }

    this
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { offset ->
                    handler.processGestureVector(
                        GestureVectorData(
                            startOffset = offset,
                            endOffset = offset,
                            deltaX = 0f,
                            deltaY = 0f,
                            velocityX = 0f,
                            velocityY = 0f,
                            distancePx = 0f,
                            durationMs = 120L,
                            contactCount = 1
                        )
                    )
                },
                onLongPress = { offset ->
                    handler.processGestureVector(
                        GestureVectorData(
                            startOffset = offset,
                            endOffset = offset,
                            deltaX = 0f,
                            deltaY = 0f,
                            velocityX = 0f,
                            velocityY = 0f,
                            distancePx = 0f,
                            durationMs = 600L,
                            contactCount = 1
                        )
                    )
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    velocityTracker.resetTracking()
                    velocityTracker.addPosition(System.currentTimeMillis(), offset)
                    startOffset = offset
                    currentOffset = offset
                    touchStartTime = System.currentTimeMillis()
                    trailPoints.clear()
                    trailPoints.add(offset)
                    handler.setGestureActive(true)
                    handler.updateGestureTrail(trailPoints.toList())
                },
                onDrag = { change, _ ->
                    change.consume()
                    currentOffset = change.position
                    velocityTracker.addPosition(change.uptimeMillis, change.position)
                    trailPoints.add(change.position)
                    if (trailPoints.size > 20) {
                        trailPoints.removeAt(0)
                    }
                    handler.updateGestureTrail(trailPoints.toList())
                },
                onDragEnd = {
                    val duration = System.currentTimeMillis() - touchStartTime
                    val velocity = velocityTracker.calculateVelocity()
                    val dx = currentOffset.x - startOffset.x
                    val dy = currentOffset.y - startOffset.y
                    val distance = sqrt(dx * dx + dy * dy)

                    val vector = GestureVectorData(
                        startOffset = startOffset,
                        endOffset = currentOffset,
                        deltaX = dx,
                        deltaY = dy,
                        velocityX = velocity.x,
                        velocityY = velocity.y,
                        distancePx = distance,
                        durationMs = duration
                    )

                    handler.processGestureVector(vector)
                    handler.setGestureActive(false)
                },
                onDragCancel = {
                    handler.setGestureActive(false)
                }
            )
        }
}
