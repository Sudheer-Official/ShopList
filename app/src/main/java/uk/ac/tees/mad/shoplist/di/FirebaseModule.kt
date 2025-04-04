package uk.ac.tees.mad.shoplist.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import uk.ac.tees.mad.shoplist.data.repository.FirebaseAuthRepositoryImpl

val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    singleOf(::FirebaseAuthRepositoryImpl)
}