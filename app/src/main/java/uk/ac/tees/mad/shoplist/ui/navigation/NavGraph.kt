package uk.ac.tees.mad.shoplist.ui.navigation

import kotlinx.serialization.Serializable

sealed class SubGraph {

    @Serializable
    data object Splash : SubGraph()

    @Serializable
    data object Home : SubGraph()

}

sealed class Dest {

    @Serializable
    data object SplashScreen : Dest()

    @Serializable
    data object HomeScreen : Dest()

    @Serializable
    data class ListDetailScreen(val listId: Int, val listTitle: String) : Dest()

    @Serializable
    data class AddEditItemScreen(val listId: Int, val listTitle: String) : Dest()

    @Serializable
    data class AddEditListScreen(val listId: Int) : Dest()

}