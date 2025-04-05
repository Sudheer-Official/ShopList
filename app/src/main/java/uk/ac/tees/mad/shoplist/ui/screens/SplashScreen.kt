package uk.ac.tees.mad.shoplist.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.R
import uk.ac.tees.mad.shoplist.ui.viewmodels.SplashViewModel

@Composable
fun SplashScreen(
    onSplashFinished: (isSignedIn: Boolean) -> Unit,
    splashViewModel: SplashViewModel = koinViewModel<SplashViewModel>()
) {
    val scale = remember { Animatable(0f) }
    val isSignedIn by splashViewModel.isSignedIn.collectAsState()

    LaunchedEffect(Unit) {
        splashViewModel.checkUserSignedIn()
        scale.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 800)
        )
        delay(1000L)
        onSplashFinished(isSignedIn)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_shoplist_logo),
            contentDescription = "ShopList Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}