package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;

class Demo {

    public static void main(String[] argv) {
        MqttClient client = new MqttClient.Builder()
                .cleanSession(true)
                .clientId("test")
                .build(MqttVersion.V3_1_1);

        client.connect("broker.emqx.io", 1883, new MqttConnectCallback() {
            @Override
            public void onConnectSuccess() {
                System.out.println("连接成功");
            }

            @Override
            public void onConnectFailure(MqttException e) {
                e.printStackTrace();
                System.err.println("连接失败");
            }
        });
    }
}
