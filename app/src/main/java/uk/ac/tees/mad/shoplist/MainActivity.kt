package uk.ac.tees.mad.shoplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import org.koin.android.ext.android.inject
import uk.ac.tees.mad.shoplist.data.remote.synchronizer.ShopListSynchronizer
import uk.ac.tees.mad.shoplist.ui.navigation.Dest
import uk.ac.tees.mad.shoplist.ui.navigation.SubGraph
import uk.ac.tees.mad.shoplist.ui.screens.AddEditItemScreen
import uk.ac.tees.mad.shoplist.ui.screens.AddEditListScreen
import uk.ac.tees.mad.shoplist.ui.screens.HomeScreen
import uk.ac.tees.mad.shoplist.ui.screens.ListDetailScreen
import uk.ac.tees.mad.shoplist.ui.screens.LogInScreen
import uk.ac.tees.mad.shoplist.ui.screens.SignUpScreen
import uk.ac.tees.mad.shoplist.ui.screens.SplashScreen
import uk.ac.tees.mad.shoplist.ui.theme.ShopListTheme
import uk.ac.tees.mad.shoplist.ui.utils.createNotificationChannel

class MainActivity : ComponentActivity() {
    val shopListSynchronizer: ShopListSynchronizer by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        createNotificationChannel(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopListTheme {
                AppContent(shopListSynchronizer)
            }
        }
    }
}

@Composable
fun AppContent(shopListSynchronizer: ShopListSynchronizer) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SubGraph.Splash) {
        navigation<SubGraph.Splash>(startDestination = Dest.SplashScreen) {
            composable<Dest.SplashScreen> {
                SplashScreen(
                    onSplashFinished = { isSignedIn ->
                        if (isSignedIn) {
                            shopListSynchronizer.startSync()
                            navController.navigate(SubGraph.Home) {
                                popUpTo(SubGraph.Splash) { inclusive = true }
                            }
                        } else {
                            navController.navigate(SubGraph.AuthGraph) {
                                popUpTo(SubGraph.Splash) { inclusive = true }
                            }
                        }
                    })
            }
        }
        navigation<SubGraph.AuthGraph>(startDestination = Dest.LogInScreen) {
            composable<Dest.LogInScreen> {
                LogInScreen(
                    shopListSynchronizer = shopListSynchronizer,
                    onLogIn = {
                    navController.navigate(SubGraph.Home) {
                        popUpTo(SubGraph.AuthGraph) { inclusive = true }
                    }
                }, onSignUp = {
                    navController.navigate(Dest.SignUpScreen)
                })
            }
            composable<Dest.SignUpScreen> {
                SignUpScreen(onBackClick = {
                    navController.popBackStack()
                }, onSignUp = {
                    navController.popBackStack()
                })
            }
        }
        navigation<SubGraph.Home>(startDestination = Dest.HomeScreen) {
            composable<Dest.HomeScreen> {
                HomeScreen(onListClick = { listId, listTitle ->
                    navController.navigate(Dest.ListDetailScreen(listId, listTitle))
                }, onAddListClick = {
                    navController.navigate(Dest.AddEditListScreen(listId = 0))
                }, onEditListClick = { listId ->
                    navController.navigate(Dest.AddEditListScreen(listId = listId))
                }, onLogOut = {
                    navController.navigate(SubGraph.AuthGraph) {
                        popUpTo(SubGraph.Home) { inclusive = true }
                    }
                })
            }
            composable<Dest.ListDetailScreen> {
                val args = it.toRoute<Dest.ListDetailScreen>()
                ListDetailScreen(listId = args.listId, listTitle = args.listTitle, onBackClick = {
                    navController.popBackStack()
                }, onAddClick = { listId, listTitle ->
                    navController.navigate(Dest.AddEditItemScreen(listId, listTitle))
                })
            }
            composable<Dest.AddEditListScreen> {
                val args = it.toRoute<Dest.AddEditListScreen>()
                AddEditListScreen(
                    listId = args.listId, onBackClick = {
                        navController.popBackStack()
                    })
            }
            composable<Dest.AddEditItemScreen> {
                val args = it.toRoute<Dest.AddEditItemScreen>()
                AddEditItemScreen(
                    listId = args.listId, listTitle = args.listTitle, onBackClick = {
                        navController.popBackStack()
                    })
            }
        }

    }
}