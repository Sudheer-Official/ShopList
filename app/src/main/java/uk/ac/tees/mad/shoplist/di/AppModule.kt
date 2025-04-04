package uk.ac.tees.mad.shoplist.di

import org.koin.dsl.module

val appModule = module {
    includes(databaseModule)
    includes(viewModelModule)
    includes(firebaseModule)
}

val appModules = listOf(appModule, databaseModule, viewModelModule, firebaseModule)