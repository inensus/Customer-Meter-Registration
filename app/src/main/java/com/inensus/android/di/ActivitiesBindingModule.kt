package com.inensus.android.di

import com.inensus.android.view.activity.DashboardActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesBindingModule {
    @ContributesAndroidInjector
    abstract fun dashboardActivity(): DashboardActivity
}