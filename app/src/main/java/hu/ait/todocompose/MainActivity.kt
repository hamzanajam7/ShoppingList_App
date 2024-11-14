package hu.ait.todocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.todocompose.ui.screen.ShoppingListScreen

import hu.ait.todocompose.ui.theme.TodoComposeTheme
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoAppNavHost(Modifier.padding(innerPadding))
                }
            }
        }
    }
}
// TodoAppNavHost composable
@Composable
fun TodoAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "splash"
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable("splash") { SplashScreen(navController) }
        composable("todolist") { ShoppingListScreen() }
    }
}
// SplashScreen composable
@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1485a3))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoImage()
            Spacer(modifier = Modifier.height(16.dp))
            LoadingDotsAnimation()
        }

        LaunchedEffect(Unit) {
            delay(3000)
            navController.navigate("todolist") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}
// LogoImage composable
@Composable
fun LogoImage() {
    Image(
        painter = painterResource(id = R.drawable.shopping_logo),
        contentDescription = "Shopping List Logo",
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f),
        contentScale = ContentScale.Fit,
    )
}
// LoadingDotsAnimation composable
@Composable
fun LoadingDotsAnimation() {
    val infiniteTransition = rememberInfiniteTransition()

    val dot1Alpha by animateDotAlpha(infiniteTransition, delayMillis = 150)
    val dot2Alpha by animateDotAlpha(infiniteTransition, delayMillis = 250)
    val dot3Alpha by animateDotAlpha(infiniteTransition, delayMillis = 350)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(alpha = dot1Alpha)
        Dot(alpha = dot2Alpha)
        Dot(alpha = dot3Alpha)
    }
}
// Helper function to animate the alpha of a dot
@Composable
fun animateDotAlpha(infiniteTransition: InfiniteTransition, delayMillis: Int): State<Float> {
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = delayMillis),
            repeatMode = RepeatMode.Reverse
        )
    )
}
// Dot composable
@Composable
fun Dot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(Color.White.copy(alpha = alpha), shape = MaterialTheme.shapes.small)
    )
}
