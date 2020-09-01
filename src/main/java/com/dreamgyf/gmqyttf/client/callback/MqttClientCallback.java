package com.dreamgyf.gmqyttf.client.callback;

import com.dreamgyf.gmqyttf.common.exception.MqttException;
import com.dreamgyf.gmqyttf.common.params.MqttTopic;

public interface MqttClientCallback extends MqttCallback {
    void onConnectionException(MqttException e);

    void onSubscribeFailure(MqttTopic mqttTopic);

    void onMessageReceived(String topic, String message);
}
