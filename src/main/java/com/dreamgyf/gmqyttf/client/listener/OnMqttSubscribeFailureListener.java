package com.dreamgyf.gmqyttf.client.listener;

import com.dreamgyf.gmqyttf.common.params.MqttTopic;

public interface OnMqttSubscribeFailureListener {
    void onSubscribeFailure(MqttTopic mqttTopic);
}
