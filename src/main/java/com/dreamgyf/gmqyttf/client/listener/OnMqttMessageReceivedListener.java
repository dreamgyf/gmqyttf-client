package com.dreamgyf.gmqyttf.client.listener;

public interface OnMqttMessageReceivedListener {
    void onMessageReceived(String topic, String message);
}
