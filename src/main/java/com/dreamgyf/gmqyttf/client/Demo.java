package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.client.callback.MqttClientCallback;
import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;

class Demo {

    public static void main(String[] argv) {
        MqttClient client = new MqttClient.Builder()
                .cleanSession(true)
                .clientId("test")
                .build(MqttVersion.V3_1_1);

        client.setCallback(new MqttClientCallback() {
            @Override
            public void onConnectionException(MqttException e) {
                System.err.println("连接异常");
            }

            @Override
            public void onMessageReceived(String topic, String message) {
                System.out.println("收到消息, topic: " + topic + " message: " + message);
            }
        });

        client.connect("broker.emqx.io", 1883, new MqttConnectCallback() {
            @Override
            public void onConnectSuccess() {
                System.out.println("连接成功");
                client.publish("/dreamgyf/test", "测试publish");
            }

            @Override
            public void onConnectFailure(MqttException e) {
                e.printStackTrace();
                System.err.println("连接失败");
            }
        });
    }
}
