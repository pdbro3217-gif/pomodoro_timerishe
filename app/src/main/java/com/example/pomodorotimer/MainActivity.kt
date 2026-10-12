package com.example.pomodorotimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodorotimer.ui.theme.PomodoroTimerTheme
import kotlinx.coroutines.delay
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.rememberNavController
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroTimerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PomodoroScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PomodoroPreview() {
    PomodoroTimerTheme {
        PomodoroScreen()
    }
}
@Composable
fun PomodoroScreen(modifier: Modifier = Modifier) {
    var isTimerRunning by remember { mutableStateOf(false) }
    var time by remember { mutableIntStateOf(1500) }
    var isworkphase by remember { mutableStateOf(true) }

    val minutes = time / 60
    val seconds = time % 60
    val timeformated = "%02d:%02d".format(minutes, seconds)
    val totaltime = if (isworkphase) 1500f else 300f
    val progress = time / totaltime
    val springSpec = spring<Dp>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val activeColor by animateColorAsState(
        targetValue = if (isworkphase) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
        label = "activeColorAnimation"
    )
    //spring
    val containerWitdh by animateDpAsState(
        targetValue = if (isTimerRunning) 260.dp else 150.dp,
        label = "containerWidth",
        animationSpec = springSpec
    )
    //размеры
    val containerHeight by animateDpAsState(
        targetValue = if (isTimerRunning) 260.dp else 60.dp,
        label = "containerHeight",
        animationSpec = springSpec
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isTimerRunning) 75.dp else 30.dp,
        label = "cornerRadius",
        animationSpec = springSpec
    )
    val navController = rememberNavController()


    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (time > 0 && isTimerRunning) {
                delay(1000L)
                time--
            }
            if (time == 0) {
                if (isworkphase) {
                    isworkphase = false
                    time = 300
                } else {
                    isworkphase = true
                    time = 1500
                }
                isTimerRunning = true
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Back")
        }
        Button(onClick = {
                navController.navigate("account")
            }) {
                Text("Account")
            }
        Text(
            text = if (isworkphase) "WORK $timeformated" else "REST $timeformated",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier
                .size(width = containerWitdh, height = containerHeight)
                .clip (RoundedCornerShape (cornerRadius)),
        shape = RoundedCornerShape(cornerRadius)
        ) {

        Box(contentAlignment = Alignment.Center) {
            if (!isTimerRunning) {
                OutlinedButton(
                    onClick = {isTimerRunning = true}) {
                    Text("Start")
                }
            } else {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    strokeWidth = 20.dp,
                    color = activeColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
}
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isTimerRunning) {
                Button(onClick = { isTimerRunning = false }) {
                    Text("Pause")
                }
            }

            OutlinedButton(
                onClick = {
                    isTimerRunning = false
                    time = if (isworkphase) 1500 else 300
                }
            ) {
                Text("Reset")
            }
        }
    }
}
