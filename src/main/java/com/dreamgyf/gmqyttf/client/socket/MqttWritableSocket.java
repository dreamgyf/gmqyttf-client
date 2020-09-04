package com.dreamgyf.gmqyttf.client.socket;

import com.dreamgyf.gmqyttf.common.throwable.exception.net.MqttSocketException;

public interface MqttWritableSocket {
    void write(byte[] packet) throws MqttSocketException;

    byte readOneBit() throws MqttSocketException;

    byte[] readBit(int bitCount) throws MqttSocketException;
}
