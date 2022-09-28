package com.telesoftas.justasonboardingapp.utils.di

import android.content.Context
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
    fun providePrefsStore(@ApplicationContext context: Context): PreferencesStore {
        return PreferencesStore(context)
    }
}
