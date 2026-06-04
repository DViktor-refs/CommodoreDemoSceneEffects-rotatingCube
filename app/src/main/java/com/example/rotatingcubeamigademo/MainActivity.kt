package com.example.rotatingcubeamigademo

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Vector3DScene()
        }
    }
}

data class Vector3(val x: Float, val y: Float, val z: Float)
data class TransformedPoint(val rotatedZ: Float, val screen: Offset)

private val colorBlue      = Color(0.00f, 0.20f, 0.85f)
private val colorLightBlue = Color(0.30f, 0.65f, 1.00f)

@Composable
fun Vector3DScene() {
    val config = LocalConfiguration.current
    val screenWidth  = config.screenWidthDp.dpToPx()
    val screenHeight = config.screenHeightDp.dpToPx()
    val centerX = screenWidth  / 2f
    val centerY = screenHeight / 2f

    var angleX by remember { mutableFloatStateOf(45f) }
    var angleY by remember { mutableFloatStateOf(0f) }
    var angleZ by remember { mutableFloatStateOf(0f) }

    val angleSkipX = 1f
    val angleSkipY = 2f
    val angleSkipZ = 3f
    val proj = 400f

    val objectPoints = remember {
        listOf(
            Vector3(-150f, -150f, -150f),
            Vector3(-150f, -150f,  150f),
            Vector3( 150f, -150f,  150f),
            Vector3( 150f, -150f, -150f),
            Vector3(-150f,    0f,    0f),
            Vector3(   0f,    0f,  150f),
            Vector3( 150f,    0f,    0f),
            Vector3(-150f,  150f, -150f),
            Vector3(-150f,  150f,  150f),
            Vector3( 150f,  150f,  150f),
            Vector3( 150f,  150f, -150f),
            Vector3(   0f,  150f,    0f),
            Vector3(   0f,    0f, -150f),
            Vector3(   0f, -150f,    0f)
        )
    }


    val faceTriangles = remember {
        listOf(

            listOf(4,  0,  1) to true,
            listOf(4,  1,  8) to false,
            listOf(4,  8,  7) to true,
            listOf(4,  7,  0) to false,

            listOf(5,  1,  2) to false,
            listOf(5,  2,  9) to true,
            listOf(5,  9,  8) to false,
            listOf(5,  8,  1) to true,

            listOf(6,  2,  3) to true,
            listOf(6,  3, 10) to false,
            listOf(6, 10,  9) to true,
            listOf(6,  9,  2) to false,

            listOf(13, 0,  3) to true,
            listOf(13, 3,  2) to false,
            listOf(13, 2,  1) to true,
            listOf(13, 1,  0) to false,

            listOf(11, 7,  8) to false,
            listOf(11, 8,  9) to true,
            listOf(11, 9, 10) to false,
            listOf(11,10,  7) to true,

            listOf(12, 0,  7) to true,
            listOf(12, 7, 10) to false,
            listOf(12,10,  3) to true,
            listOf(12, 3,  0) to false,
        )
    }

    val cubeEdges = remember {
        listOf(
            listOf(0, 1), listOf(1, 2), listOf(2, 3), listOf(3, 0),
            listOf(7, 8), listOf(8, 9), listOf(9,10), listOf(10,7),
            listOf(0, 7), listOf(1, 8), listOf(2, 9), listOf(3,10)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val animValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(animValue) {
        angleX = (angleX + angleSkipX) % 360f
        angleY = (angleY + angleSkipY) % 360f
        angleZ = (angleZ + angleSkipZ) % 360f
    }

    val transformedPoints by remember {
        derivedStateOf {
            objectPoints.map { point ->
                val (x, y, z) = point

                // Rotate Z
                val cosZ = cos(Math.toRadians(angleZ.toDouble())).toFloat()
                val sinZ = sin(Math.toRadians(angleZ.toDouble())).toFloat()
                var nx = x * cosZ - y * sinZ
                var ny = x * sinZ + y * cosZ
                var nz = z

                val cosY = cos(Math.toRadians(angleY.toDouble())).toFloat()
                val sinY = sin(Math.toRadians(angleY.toDouble())).toFloat()
                val tmpX = nx; val tmpZ = nz
                nx = tmpX * cosY - tmpZ * sinY
                nz = tmpX * sinY + tmpZ * cosY

                val cosX = cos(Math.toRadians(angleX.toDouble())).toFloat()
                val sinX = sin(Math.toRadians(angleX.toDouble())).toFloat()
                val tmpY = ny; val tmpZ2 = nz
                ny = tmpY * cosX - tmpZ2 * sinX
                nz = tmpY * sinX + tmpZ2 * cosX

                val zInv = if (nz != proj) proj / (proj - nz) else 1f
                TransformedPoint(nz, Offset(nx * zInv + centerX, ny * zInv + centerY))
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color.Black)

        val sorted = faceTriangles.sortedBy { (tri, _) ->
            (transformedPoints[tri[0]].rotatedZ +
             transformedPoints[tri[1]].rotatedZ +
             transformedPoints[tri[2]].rotatedZ) / 3f
        }

        for ((tri, isBlue) in sorted) {
            val p0 = transformedPoints[tri[0]].screen
            val p1 = transformedPoints[tri[1]].screen
            val p2 = transformedPoints[tri[2]].screen
            drawPath(
                path = Path().apply {
                    moveTo(p0.x, p0.y)
                    lineTo(p1.x, p1.y)
                    lineTo(p2.x, p2.y)
                    close()
                },
                color = if (isBlue) colorBlue else colorLightBlue
            )
        }

        for (edge in cubeEdges) {
            drawLine(
                color       = Color.Black.copy(alpha = 0.55f),
                start       = transformedPoints[edge[0]].screen,
                end         = transformedPoints[edge[1]].screen,
                strokeWidth = 2f
            )
        }
    }
}

fun Int.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density
