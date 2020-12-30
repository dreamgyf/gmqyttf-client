# MQTT协议客户端

[![License](https://img.shields.io/badge/license-Apache%202-green)](https://github.com/dreamgyf/gmqyttf-client/blob/master/LICENSE)
[![Forks](https://img.shields.io/github/forks/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/network/members)
[![Starts](https://img.shields.io/github/stars/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/stargazers)
[![Issues](https://img.shields.io/github/issues/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/issues)

[English](https://github.com/dreamgyf/gmqyttf-client/blob/master/README.md)
|
[中文](https://github.com/dreamgyf/gmqyttf-client/blob/master/README-zh.md)

### 开始使用

1. 依赖

    * Maven

        先添加Jcenter仓库

        ```xml
        <repository>
            <id>jcenter</id>
            <name>jcenter</name>
            <url>https://jcenter.bintray.com</url>
        </repository>
        ```

        然后

        ```xml
        <dependency>
            <groupId>com.dreamgyf.mqtt</groupId>
            <artifactId>gmqyttf-client</artifactId>
            <version>0.1.0</version>
            <type>pom</type>
        </dependency>
        ```

    * Gradle

        先添加Jcenter仓库

        ```groovy
        repositories {
          jcenter()
        }
        ```

        然后

        ```groovy
        implementation 'com.dreamgyf.mqtt:gmqyttf-client:0.1.0'
        ```

2. 运行

    ```java
    //首先，创建一个MqttClient实例
    MqttClient client = new MqttClient.Builder()
       .cleanSession(true)
       .clientId("test1")
       .usernameFlag(true)
       .passwordFlag(true)
       .username("dreamgyf")
       .password("dreamgyf")
       .keepAliveTime((short) 10)
       .willFlag(true)
       .willQoS(2)
       .willTopic("/willTopic")
       .willMessage("")
       .willRetain(true)
       .build(MqttVersion.V3_1_1);

    //设置回调
    client.setCallback(new MqttClientCallback() {
        @Override
        public void onConnectSuccess() {
            System.out.println("Connection success");
        }

        @Override
        public void onConnectionException(MqttException e) {
            e.printStackTrace();
            System.err.println("Connection exception: " + e + " " + e.getMessage());
        }

        @Override
        public void onSubscribeFailure(MqttTopic mqttTopic) {
            System.err.println("Subscription failed: " + mqttTopic.getTopic() + " QoS: " + mqttTopic.getQoS());
        }

        @Override
        public void onMessageReceived(String topic, String message) {
            System.out.println("Message received, topic: " + topic + " message: " + message);
        }
    });

    //连接，订阅和发布
    client.connect("broker.emqx.io", 1883);

    client.subscribe(new MqttTopic("/dreamgyf/test", 2));
    client.publish("/dreamgyf/test", "publish test", new MqttPublishOption().QoS(2));
    ```
