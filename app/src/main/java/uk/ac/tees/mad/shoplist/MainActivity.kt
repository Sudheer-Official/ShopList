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
import uk.ac.tees.mad.shoplist.ui.navigation.Dest
import uk.ac.tees.mad.shoplist.ui.navigation.SubGraph
import uk.ac.tees.mad.shoplist.ui.screens.AddEditItemScreen
import uk.ac.tees.mad.shoplist.ui.screens.AddEditListScreen
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
                HomeScreen(onListClick = { listId, listTitle ->
                    navController.navigate(Dest.ListDetailScreen(listId, listTitle))
                }, onAddListClick = {
                    // TODO: Navigate to create new list
                    navController.navigate(Dest.AddEditListScreen(listId = 0))
                }, onEditListClick = {listId ->
                    // TODO: Navigate to edit list
                    navController.navigate(Dest.AddEditListScreen(listId = listId))
                })
            }
            composable<Dest.ListDetailScreen> {
                val args = it.toRoute<Dest.ListDetailScreen>()
                ListDetailScreen(
                    listId = args.listId,
                    listTitle = args.listTitle,
                    onBackClick = {
                    navController.popBackStack()
                }, onAddClick = {listId, listTitle ->
                    navController.navigate(Dest.AddEditItemScreen(listId, listTitle))
                })
            }
            composable<Dest.AddEditListScreen> {
                val args = it.toRoute<Dest.AddEditListScreen>()
                AddEditListScreen(
                    listId = args.listId,
                    onBackClick = {
                        navController.popBackStack()
                    })
            }
            composable<Dest.AddEditItemScreen> {
                val args = it.toRoute<Dest.AddEditItemScreen>()
                AddEditItemScreen(
                    listId = args.listId,
                    listTitle = args.listTitle,
                    onBackClick = {
                        navController.popBackStack()
                    })
            }
        }

    }
}