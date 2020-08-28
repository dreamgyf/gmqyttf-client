package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.common.enums.MqttVersion;

public class Demo {

    public static void main(String[] argv) {
        MqttClient client = new MqttClient.Builder()
                .cleanSession(true)
                .clientId("test")
                .build(MqttVersion.V3_1_1);

        client.connect("broker.emqx.io", 1883);
    }
}
