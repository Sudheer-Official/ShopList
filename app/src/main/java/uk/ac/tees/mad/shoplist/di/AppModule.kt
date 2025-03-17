package uk.ac.tees.mad.shoplist.di

import org.koin.dsl.module

val appModule = module {
    includes(databaseModule)
}

val appModules = listOf(appModule, databaseModule)