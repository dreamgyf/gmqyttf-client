package com.dreamgyf.gmqyttf.client.callback;

import com.dreamgyf.gmqyttf.common.params.MqttTopic;
import com.dreamgyf.gmqyttf.common.throwable.exception.MqttException;

public interface MqttClientCallback extends MqttCallback {
    void onConnectSuccess();

    void onConnectionException(MqttException e);

    void onSubscribeFailure(MqttTopic mqttTopic);

    void onMessageReceived(String topic, String message);
}
