package com.telesoftas.justasonboardingapp.utils.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.telesoftas.justasonboardingapp.utils.data.AppDatabase
import com.telesoftas.justasonboardingapp.utils.data.ArticleDao
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceDao
import com.telesoftas.justasonboardingapp.utils.preferences.PreferencesStore
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
object TestDataModule {
    @Singleton
    @Provides
    fun providePrefsStore(@ApplicationContext context: Context): PreferencesStore {
        return PreferencesStore(context)
    }

    @Singleton
    @Provides
    fun provideAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(applicationContext, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()

    @Singleton
    @Provides
    fun provideNewsSourceDao(database: AppDatabase): NewsSourceDao = database.newsSourceDao()

}
