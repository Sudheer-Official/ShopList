package uk.ac.tees.mad.shoplist.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.shoplist.ui.viewmodels.AddEditItemViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.AddEditListViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.HomeViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ListDetailViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingItemViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel

val viewModelModule = module {
    viewModelOf(::ShoppingListViewModel)
    viewModelOf(::ShoppingItemViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddEditListViewModel)
    viewModelOf(::ListDetailViewModel)
    viewModelOf(::AddEditItemViewModel)
}