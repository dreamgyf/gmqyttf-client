package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import junit.framework.TestCase;
import org.junit.Test;

public class MqttClientTest extends TestCase {

    @Test
    public void test() {
        MqttClient client = new MqttClient.Builder()
                .cleanSession(true)
                .clientId("test")
                .build(MqttVersion.V3_1_1);
    }

}