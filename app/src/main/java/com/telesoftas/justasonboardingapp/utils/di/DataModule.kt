package com.telesoftas.justasonboardingapp.utils.di

import android.content.Context
import androidx.room.Room
import com.telesoftas.justasonboardingapp.utils.data.AppDatabase
import com.telesoftas.justasonboardingapp.utils.data.ArticleDao
import com.telesoftas.justasonboardingapp.utils.data.SourceDao
import com.telesoftas.justasonboardingapp.utils.other.Constants
import com.telesoftas.justasonboardingapp.utils.preferences.PreferencesStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {
    @Singleton
    @Provides
    fun providePrefsStore(@ApplicationContext context: Context): PreferencesStore = PreferencesStore(context)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()

    @Singleton
    @Provides
    fun provideNewsSourceDao(database: AppDatabase): SourceDao = database.newsSourceDao()
}
