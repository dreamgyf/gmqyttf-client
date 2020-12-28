# Implementation of the client part of the MQTT protocol

[![License](https://img.shields.io/badge/license-Apache%202-green)](https://github.com/dreamgyf/gmqyttf-client/blob/master/LICENSE)
[![Forks](https://img.shields.io/github/forks/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/network/members)
[![Starts](https://img.shields.io/github/stars/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/stargazers)
[![Issues](https://img.shields.io/github/issues/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/issues)

### Get Started

1. Dependency

    * Maven

        Add Jcenter repository first

        ```xml
        <repository>
              <id>jcenter</id>
              <name>jcenter</name>
              <url>https://jcenter.bintray.com</url>
        </repository>
        ```

        then

        ```xml
        <dependency>
            <groupId>com.dreamgyf.mqtt</groupId>
            <artifactId>gmqyttf-client</artifactId>
            <version>0.1.0</version>
            <type>pom</type>
        </dependency>
        ```

    * Gradle

        Add Jcenter repository first

        ```groovy
        repositories {
          jcenter()
        }
        ```

        then

        ```groovy
        implementation 'com.dreamgyf.mqtt:gmqyttf-client:0.1.0'
        ```

2. Run

    ```java
    //Build client first
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

    //Set callback
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

    //Connect,subscribe,publish
    client.connect("broker.emqx.io", 1883);

    client.subscribe(new MqttTopic("/dreamgyf/test", 2));
    client.publish("/dreamgyf/test", "publish test", new MqttPublishOption().QoS(2));
    ```
