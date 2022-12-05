package com.digitalconcerthall.dev.challenge.dagger

import com.digitalconcerthall.dev.challenge.main.ChallengeApplication
import com.digitalconcerthall.dev.challenge.main.MainActivity

@javax.inject.Singleton
@dagger.Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(application: ChallengeApplication)
    fun inject(mainActivity: MainActivity)

}
