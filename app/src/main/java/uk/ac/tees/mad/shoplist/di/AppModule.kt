package uk.ac.tees.mad.shoplist.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.shoplist.ui.viewmodels.AddEditListViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ListDetailViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel

val appModule = module {
    includes(databaseModule)
    viewModelOf(::ShoppingListViewModel)
    viewModelOf(::AddEditListViewModel)
    viewModelOf(::ListDetailViewModel)
}

val appModules = listOf(appModule, databaseModule)