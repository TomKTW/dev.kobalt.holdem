package dev.kobalt.holdem.android.play

import androidx.lifecycle.viewModelScope
import dev.kobalt.holdem.android.base.BaseViewModel
import dev.kobalt.holdem.android.state.StateEntity
import dev.kobalt.holdem.android.state.toStateEntity
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*

class PlayViewModel : BaseViewModel() {

    val pageFlow = MutableSharedFlow<PlayFragment.Page>(replay = 1).apply {
        viewModelScope.launch { emit(PlayFragment.Page.Server) }
    }

    val messageFlow = MutableSharedFlow<String?>(replay = 1).apply {
        viewModelScope.launch { emit(null) }
    }

    val stateFlow = MutableSharedFlow<StateEntity>(replay = 1).apply {
        viewModelScope.launch { emit(StateEntity(null, null, null)) }
    }

    val connectedFlow = MutableSharedFlow<PlayConnectState>(replay = 1).apply {
        viewModelScope.launch { emit(PlayConnectState.Disconnected) }
    }

    var session: WebSocketSession? = null

    fun changeNickname(value: String) {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("name $value")) }
    }

    fun createRoom() {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("create")) }
    }

    fun joinRoom(value: String) {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("join $value")) }
    }

    fun leaveRoom() {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("leave room")) }
    }

    fun startRoom() {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("start")) }
    }

    fun tableFold() {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("fold")) }
    }

    fun tableCheck() {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("check")) }
    }

    fun tableCall() {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("call")) }
    }

    fun tableBet(value: Int) {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("bet $value")) }
    }

    fun tableRaise(value: Int) {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("raise $value")) }
    }

    fun tableAllIn() {
        viewModelScope.launch(Dispatchers.IO) { session?.send(Frame.Text("allin")) }
    }

    private suspend fun disconnectSession() {
        session?.send(Frame.Text("leave"))
        session?.close()
        session = null
        connectedFlow.emit(PlayConnectState.Disconnected)
        stateFlow.emit(StateEntity(null, null, null))
    }

    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            disconnectSession()
        }
    }

    fun connect(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            disconnectSession()
            connectedFlow.emit(PlayConnectState.Connecting)
            application.httpClient.webSocket(url) {
                session = this
                stateFlow.emit(StateEntity(null, null, null))
                connectedFlow.emit(PlayConnectState.Connected)
                launch {
                    while (true) {
                        send(Frame.Text("ping"))
                        delay(1000)
                    }
                }
                incoming.consumeEach { frame ->
                    when (frame) {
                        is Frame.Text -> frame.readText().also { data ->
                            when (data) {
                                "pong" -> {}
                                else -> Json.parseToJsonElement(data).jsonObject.let {
                                    if (it["message"] is JsonPrimitive) {
                                        messageFlow.emit((it["message"] as? JsonPrimitive)?.contentOrNull)
                                    }
                                    if (it["player"] is JsonObject) {
                                        stateFlow.emit(it.toStateEntity())
                                    }
                                }
                            }
                            println(data)
                        }

                        else -> return@consumeEach
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}

enum class PlayConnectState {
    Disconnected, Connecting, Connected
}