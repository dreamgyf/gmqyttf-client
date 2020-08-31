package com.dreamgyf.gmqyttf.client.task;

import com.dreamgyf.gmqyttf.client.env.MqttPacketQueue;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.common.enums.MqttPacketType;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;
import com.dreamgyf.gmqyttf.common.exception.packet.IllegalPacketException;
import com.dreamgyf.gmqyttf.common.packet.*;
import com.dreamgyf.gmqyttf.common.utils.ByteUtils;
import com.dreamgyf.gmqyttf.common.utils.MqttPacketUtils;

public class MqttPacketReceiveTask extends MqttTask {

    private final MqttPacketQueue.Response mPacketRespQueue;

    public MqttPacketReceiveTask(MqttVersion version, MqttWritableSocket socket, MqttPacketQueue.Response packetRespQueue) {
        super(version, socket);
        mPacketRespQueue = packetRespQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        try {
            byte[] header = new byte[1];
            header[0] = readSocketOneBit();
            byte type = MqttPacketUtils.parseType(header[0]);
            if (MqttPacketUtils.isTypeInVersion(type, getVersion())) {
                byte[] tempRemainLength = new byte[4];
                int pos = 0;
                do {
                    tempRemainLength[pos++] = readSocketOneBit();
                } while (MqttPacketUtils.hasNextRemainingLength(tempRemainLength[pos - 1]));
                byte[] remainLength = ByteUtils.getSection(tempRemainLength, 0, pos);
                byte[] fixHeader = ByteUtils.combine(header, remainLength);
                byte[] residue = readSocketBit(MqttPacketUtils.getRemainingLength(remainLength, 0));
                byte[] packet = ByteUtils.combine(fixHeader, residue);
                pushPacket(packet, getVersion());
            }
        } catch (MqttException e) {
            e.printStackTrace();
            onMqttExceptionThrow(e);
        }
    }

    private void pushPacket(byte[] packet, MqttVersion mVersion) throws MqttException, InterruptedException {
        switch (mVersion) {
            case V3_1_1:
                pushPacketV311(packet);
                break;
        }
    }

    private void pushPacketV311(byte[] packet) throws MqttException, InterruptedException {
        byte type = MqttPacketUtils.parseType(packet[0]);
        switch (type) {
            case MqttPacketType.V3_1_1.CONNACK: {
                mPacketRespQueue.connack.put(new MqttConnackPacket(packet, getVersion()));
                break;
            }
            case MqttPacketType.V3_1_1.PUBLISH: {
                mPacketRespQueue.publish.put(new MqttPublishPacket(packet, getVersion()));
                break;
            }
            case MqttPacketType.V3_1_1.PUBACK: {
                mPacketRespQueue.puback.put(new MqttPubackPacket(packet, getVersion()));
                break;
            }
            case MqttPacketType.V3_1_1.PUBREC: {
                mPacketRespQueue.pubrec.put(new MqttPubrecPacket(packet, getVersion()));
                break;
            }
            case MqttPacketType.V3_1_1.PUBREL: {
                mPacketRespQueue.pubrel.put(new MqttPubrelPacket(packet, getVersion()));
                break;
            }
            case MqttPacketType.V3_1_1.PUBCOMP: {
                mPacketRespQueue.pubcomp.put(new MqttPubcompPacket(packet, getVersion()));
                break;
            }
            case MqttPacketType.V3_1_1.SUBACK: {
                mPacketRespQueue.suback.put(new MqttSubackPacket(packet, getVersion()));
                break;
            }
            case MqttPacketType.V3_1_1.UNSUBACK: {
                mPacketRespQueue.unsuback.put(new MqttUnsubackPacket(packet, getVersion()));
                break;
            }
            case MqttPacketType.V3_1_1.PINGRESP: {
                mPacketRespQueue.pingresp.put(new MqttPingrespPacket(packet, getVersion()));
                break;
            }
            default:
                throw new IllegalPacketException();
        }
    }

}
