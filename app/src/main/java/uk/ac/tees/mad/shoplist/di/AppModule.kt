package uk.ac.tees.mad.shoplist.di

import org.koin.dsl.module

val appModule = module {
    includes(firebaseModule)
    includes(databaseModule)
    includes(viewModelModule)
}

val appModules = listOf(appModule, databaseModule, viewModelModule, firebaseModule)