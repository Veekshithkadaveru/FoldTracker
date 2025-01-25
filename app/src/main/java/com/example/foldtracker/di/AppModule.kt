package com.example.foldtracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.foldtracker.repository.CounterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fold_tracker_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide DataStore instance
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    // Provide CounterRepository
    @Provides
    @Singleton
    fun provideCounterRepository(
        dataStore: DataStore<Preferences>
    ): CounterRepository {
        return CounterRepository(dataStore)
    }
}