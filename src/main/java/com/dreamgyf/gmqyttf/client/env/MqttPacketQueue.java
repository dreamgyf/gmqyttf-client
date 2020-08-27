package com.dreamgyf.gmqyttf.client.env;

import com.dreamgyf.gmqyttf.common.packet.*;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPacketQueue {

    public final LinkedBlockingQueue<MqttConnackPacket> connack;

    public final LinkedBlockingQueue<MqttPublishPacket> publish;

    public final LinkedBlockingQueue<MqttPubackPacket> puback;

    public final LinkedBlockingQueue<MqttPubrecPacket> pubrec;

    public final LinkedBlockingQueue<MqttPubrelPacket> pubrel;

    public final LinkedBlockingQueue<MqttPubcompPacket> pubcomp;

    public final LinkedBlockingQueue<MqttSubackPacket> suback;

    public final LinkedBlockingQueue<MqttUnsubackPacket> unsuback;

    public final LinkedBlockingQueue<MqttPingrespPacket> pingresp;

    public MqttPacketQueue() {
        this(1000);
    }

    public MqttPacketQueue(int capacity) {
        connack = new LinkedBlockingQueue<>(capacity);
        publish = new LinkedBlockingQueue<>(capacity);
        puback = new LinkedBlockingQueue<>(capacity);
        pubrec = new LinkedBlockingQueue<>(capacity);
        pubrel = new LinkedBlockingQueue<>(capacity);
        pubcomp = new LinkedBlockingQueue<>(capacity);
        suback = new LinkedBlockingQueue<>(capacity);
        unsuback = new LinkedBlockingQueue<>(capacity);
        pingresp = new LinkedBlockingQueue<>(capacity);
    }

    public void clear() {
        connack.clear();
        publish.clear();
        puback.clear();
        pubrec.clear();
        pubrel.clear();
        pubcomp.clear();
        suback.clear();
        unsuback.clear();
        pingresp.clear();
    }

}
