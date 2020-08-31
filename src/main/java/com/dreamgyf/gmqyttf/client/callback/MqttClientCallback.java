package com.dreamgyf.gmqyttf.client.callback;

import com.dreamgyf.gmqyttf.common.exception.MqttException;

public interface MqttClientCallback extends MqttCallback {
    void onConnectionException(MqttException e);

    void onMessageReceived(String topic, String message);
}
