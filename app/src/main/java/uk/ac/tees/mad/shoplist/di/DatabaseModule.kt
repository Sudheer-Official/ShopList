package uk.ac.tees.mad.shoplist.di

import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import uk.ac.tees.mad.shoplist.data.local.ShopListDatabase
import uk.ac.tees.mad.shoplist.data.local.dao.ShoppingItemDao
import uk.ac.tees.mad.shoplist.data.local.dao.ShoppingListDao
import uk.ac.tees.mad.shoplist.data.repository.ShoppingItemRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingItemRepositoryImpl
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepositoryImpl

val databaseModule = module {
    fun provideDatabase(application: Application): ShopListDatabase {
        return Room.databaseBuilder(application, ShopListDatabase::class.java, "shoplist_database")
            .fallbackToDestructiveMigration().build()
    }

    fun provideShoppingListDao(database: ShopListDatabase): ShoppingListDao {
        return database.shoppingListDao()
    }

    fun provideShoppingItemDao(database: ShopListDatabase): ShoppingItemDao {
        return database.shoppingItemDao()
    }

    fun provideShoppingListRepository(shoppingListDao: ShoppingListDao): ShoppingListRepository {
        return ShoppingListRepositoryImpl(shoppingListDao)
    }

    fun provideShoppingItemRepository(shoppingItemDao: ShoppingItemDao): ShoppingItemRepository {
        return ShoppingItemRepositoryImpl(shoppingItemDao)
    }

    single { provideDatabase(androidApplication()) }
    single { provideShoppingListDao(get()) }
    single { provideShoppingItemDao(get()) }
    single { provideShoppingListRepository(get()) }
    single { provideShoppingItemRepository(get()) }
}