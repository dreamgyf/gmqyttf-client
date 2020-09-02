package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.client.callback.MqttClientCallback;
import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.options.MqttPublishOption;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;
import com.dreamgyf.gmqyttf.common.params.MqttTopic;

class Demo {

    public static void main(String[] argv) {
        MqttClient client = new MqttClient.Builder()
                .cleanSession(true)
                .clientId("test")
                .build(MqttVersion.V3_1_1);

        client.setCallback(new MqttClientCallback() {
            @Override
            public void onConnectionException(MqttException e) {
                e.printStackTrace();
                System.err.println("连接异常: " + e + " " + e.getMessage());
            }

            @Override
            public void onSubscribeFailure(MqttTopic mqttTopic) {
                System.err.println("订阅失败: " + mqttTopic.getTopic() + " QoS: " + mqttTopic.getQoS());
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
                client.subscribe(new MqttTopic("/dreamgyf/test", 2));
                client.publish("/dreamgyf/test", "测试publish", new MqttPublishOption().QoS(2));
            }

            @Override
            public void onConnectFailure(MqttException e) {
                e.printStackTrace();
                System.err.println("连接失败");
            }
        });
    }
}
