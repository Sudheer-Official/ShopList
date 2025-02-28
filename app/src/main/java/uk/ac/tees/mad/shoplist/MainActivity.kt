package uk.ac.tees.mad.shoplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import uk.ac.tees.mad.shoplist.ui.screens.HomeScreen
import uk.ac.tees.mad.shoplist.ui.screens.SplashScreen
import uk.ac.tees.mad.shoplist.ui.theme.ShopListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopListTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val scale = remember { Animatable(0f) }
    val showSplash = remember { mutableStateOf(true) }

    // Animation effect
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(1000L) // Total splash duration (800ms animation + 1000ms delay)
        showSplash.value = false
    }

    if (showSplash.value) {
        SplashScreen(scale = scale)
    } else {
        HomeScreen()
    }
}