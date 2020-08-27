package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.common.enums.MqttVersion;

public class MqttDemo {
    MqttClient client = new MqttClient.Builder()
            .cleanSession(true)
            .clientId("test")
            .build(MqttVersion.V3_1_1);
}
