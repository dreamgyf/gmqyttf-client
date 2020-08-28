package com.dreamgyf.gmqyttf.client.env;

import com.dreamgyf.gmqyttf.common.packet.*;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttPacketQueue {

    public class Request {
        public final LinkedBlockingQueue<MqttConnectPacket> connect;
        public final LinkedBlockingQueue<MqttPublishPacket> publish;
        public final LinkedBlockingQueue<MqttPubackPacket> puback;
        public final LinkedBlockingQueue<MqttPubrecPacket> pubrec;
        public final LinkedBlockingQueue<MqttPubrelPacket> pubrel;
        public final LinkedBlockingQueue<MqttPubcompPacket> pubcomp;
        public final LinkedBlockingQueue<MqttSubscribePacket> subscribe;
        public final LinkedBlockingQueue<MqttUnsubscribePacket> unsubscribe;
        public final LinkedBlockingQueue<MqttPingreqPacket> pingreq;
        public final LinkedBlockingQueue<MqttDisconnectPacket> disconnect;

        public Request(int capacity) {
            connect = new LinkedBlockingQueue<>(capacity);
            publish = new LinkedBlockingQueue<>(capacity);
            puback = new LinkedBlockingQueue<>(capacity);
            pubrec = new LinkedBlockingQueue<>(capacity);
            pubrel = new LinkedBlockingQueue<>(capacity);
            pubcomp = new LinkedBlockingQueue<>(capacity);
            subscribe = new LinkedBlockingQueue<>(capacity);
            unsubscribe = new LinkedBlockingQueue<>(capacity);
            pingreq = new LinkedBlockingQueue<>(capacity);
            disconnect = new LinkedBlockingQueue<>(capacity);
        }

        public void clear() {
            connect.clear();
            publish.clear();
            puback.clear();
            pubrec.clear();
            pubrel.clear();
            pubcomp.clear();
            subscribe.clear();
            unsubscribe.clear();
            pingreq.clear();
            disconnect.clear();
        }
    }

    public class Response {
        public final LinkedBlockingQueue<MqttConnackPacket> connack;
        public final LinkedBlockingQueue<MqttPublishPacket> publish;
        public final LinkedBlockingQueue<MqttPubackPacket> puback;
        public final LinkedBlockingQueue<MqttPubrecPacket> pubrec;
        public final LinkedBlockingQueue<MqttPubrelPacket> pubrel;
        public final LinkedBlockingQueue<MqttPubcompPacket> pubcomp;
        public final LinkedBlockingQueue<MqttSubackPacket> suback;
        public final LinkedBlockingQueue<MqttUnsubackPacket> unsuback;
        public final LinkedBlockingQueue<MqttPingrespPacket> pingresp;

        public Response(int capacity) {
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

    public final Request request;

    public final Response response;

    public MqttPacketQueue() {
        this(1000);
    }

    public MqttPacketQueue(int capacity) {
        request = new Request(capacity);
        response = new Response(capacity);
    }

    public void clear() {
        request.clear();
        response.clear();
    }

}
