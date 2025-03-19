package uk.ac.tees.mad.shoplist.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel

val appModule = module {
    includes(databaseModule)
    viewModelOf(::ShoppingListViewModel)
}

val appModules = listOf(appModule, databaseModule)