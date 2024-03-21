package dev.kobalt.holdem.android.main

import dev.kobalt.holdem.android.base.BaseApplication
import dev.kobalt.holdem.android.component.Preferences
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.websocket.*


class MainApplication(val native: Native) {

    class Native : BaseApplication() {

        companion object {
            lateinit var instance: MainApplication private set
        }

        override fun onCreate() {
            super.onCreate()
            instance = MainApplication(this)
            if (instance.preferences.name == null) {
                instance
            }
        }

    }

    val httpClient = HttpClient(OkHttp) {
        install(WebSockets)
        expectSuccess = false
    }

    val preferences = Preferences(this).apply {
        if (name == null) name = "Player"
        if (server == null) server = "wss://tom.kobalt.dev/holdem/server/"
    }

}


