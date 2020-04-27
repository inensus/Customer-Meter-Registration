package com.inensus.android.di

import com.inensus.android.InensusApplication
import com.inensus.android.util.SharedPreferencesWrapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideSharedPreference(app : InensusApplication): SharedPreferencesWrapper = SharedPreferencesWrapper(app)
}