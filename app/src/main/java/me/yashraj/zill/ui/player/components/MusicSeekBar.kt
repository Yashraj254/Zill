package me.yashraj.zill.ui.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

import me.yashraj.zill.ui.player.toTimeString
import me.yashraj.zill.ui.theme.IcyAccent
import me.yashraj.zill.ui.theme.IcyPrimary
import me.yashraj.zill.ui.theme.IcySecondary

@Composable
fun MusicSeekBar(
    progressMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
) {
    var isScrubbing by remember { mutableStateOf(false) }
    var seekTargetMs by remember { mutableLongStateOf(0L) }
    var seekLocked by remember { mutableStateOf(false) }

    LaunchedEffect(progressMs, seekLocked) {
        if (seekLocked && kotlin.math.abs(progressMs - seekTargetMs) < 750) {
            seekLocked = false
        }
    }

    val effectivePositionMs = when {
        isScrubbing -> seekTargetMs
        seekLocked -> seekTargetMs
        else -> progressMs
    }

    val sliderValue =
        (effectivePositionMs.toFloat() / durationMs.coerceAtLeast(1L))
            .coerceIn(0f, 1f)

    Column(modifier = Modifier.fillMaxWidth()) {

        Slider(
            value = sliderValue,
            onValueChange = { value ->
                isScrubbing = true
                seekTargetMs = (value * durationMs).toLong()
            },
            onValueChangeFinished = {
                onSeek(seekTargetMs)
                isScrubbing = false
                seekLocked = true
            },
            colors = SliderDefaults.colors(
                thumbColor = IcyPrimary,
                activeTrackColor = IcyAccent,
                inactiveTrackColor = IcySecondary.copy(alpha = 0.25f),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = effectivePositionMs.toTimeString(),
                color = IcySecondary,
                fontSize = 12.sp,
            )
            Text(
                text = durationMs.toTimeString(),
                color = IcySecondary,
                fontSize = 12.sp,
            )
        }
    }
}


