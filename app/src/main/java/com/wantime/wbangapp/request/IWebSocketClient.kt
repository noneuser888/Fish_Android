package com.wantime.wbangapp.request

import android.util.Log
import com.wantime.wbangapp.ui.event.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class IWebSocketClient(serverUri: URI?) : WebSocketClient(serverUri, Draft_6455()) {
    override fun onOpen(handshakedata: ServerHandshake) {
        Log.e("JWebSocketClient", "onOpen()")
        send("hello")
    }

    override fun onMessage(message: String) {
        val xEvent = MessageEvent()
        xEvent.dataJson = message
        xEvent.mType = MessageEvent.EventType.socket.ordinal
        EventBus.getDefault().post(xEvent)
    }

    override fun onClose(
        code: Int,
        reason: String,
        remote: Boolean
    ) {
        Log.e("JWebSocketClient", "onClose()")
    }

    override fun onError(ex: Exception) {
        Log.e("JWebSocketClient", "onError()")
    }
}