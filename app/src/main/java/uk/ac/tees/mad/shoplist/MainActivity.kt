package uk.ac.tees.mad.shoplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import uk.ac.tees.mad.shoplist.domain.Dest
import uk.ac.tees.mad.shoplist.domain.SubGraph
import uk.ac.tees.mad.shoplist.ui.screens.AddEditItemScreen
import uk.ac.tees.mad.shoplist.ui.screens.HomeScreen
import uk.ac.tees.mad.shoplist.ui.screens.ListDetailScreen
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

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SubGraph.Splash) {
        navigation<SubGraph.Splash>(startDestination = Dest.SplashScreen) {
            composable<Dest.SplashScreen> {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(SubGraph.Home) {
                            popUpTo(SubGraph.Splash) { inclusive = true }
                        }
                    })
            }
        }
        navigation<SubGraph.Home>(startDestination = Dest.HomeScreen) {
            composable<Dest.HomeScreen> {
                HomeScreen(onListClick = { listId ->
                    navController.navigate(Dest.ListDetailScreen(listId))
                }, onAddListClick = {
                    // TODO: Navigate to create new list
                    navController.navigate(Dest.AddEditItemScreen)
                })
            }
            composable<Dest.ListDetailScreen> {
                val args = it.toRoute<Dest.ListDetailScreen>()
                ListDetailScreen(
                    listId = args.listId, onBackClick = {
                        navController.popBackStack()
                    })
            }
            composable<Dest.AddEditItemScreen> {
                AddEditItemScreen(
                    onBackClick = {
                        navController.popBackStack()
                    })
            }
        }

    }
}