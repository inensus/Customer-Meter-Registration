package com.inensus.android.di

import com.inensus.android.InensusApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            AppModule::class,
            ActivitiesBindingModule::class
        ]
)
interface AppComponent : AndroidInjector<InensusApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun create(app: InensusApplication): Builder

        fun build(): AppComponent
    }
}