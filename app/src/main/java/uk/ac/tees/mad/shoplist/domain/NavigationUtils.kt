package uk.ac.tees.mad.shoplist.domain

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
    data class ListDetailScreen(val listId: Int) : Dest()

    @Serializable
    data object AddEditItemScreen : Dest()

    @Serializable
    data object CreateNewListScreen : Dest()

}