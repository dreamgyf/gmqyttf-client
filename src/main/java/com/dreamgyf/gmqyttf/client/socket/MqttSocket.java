package com.dreamgyf.gmqyttf.client.socket;

import com.dreamgyf.gmqyttf.common.exception.net.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MqttSocket implements MqttWritableSocket {

    private Socket mSocket;

    private final Object mReadLock = new Object();

    private final Object mWriteLock = new Object();

    public void connect(String host, int port) throws MqttSocketException {
        synchronized (mWriteLock) {
            synchronized (mReadLock) {
                try {
                    mSocket = new Socket(host, port);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MqttSocketConnectException();
                }
            }
        }
    }

    public void close() throws MqttSocketException {
        synchronized (mWriteLock) {
            synchronized (mReadLock) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MqttSocketCloseException();
                }
            }
        }
    }

    @Override
    public void write(byte[] packet) throws MqttSocketException {
        synchronized (mWriteLock) {
            if (isConnected()) {
                try {
                    OutputStream os = mSocket.getOutputStream();
                    os.write(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MqttSocketWriteException();
                }
            } else {
                throw new MqttSocketUnconnectedException();
            }
        }
    }

    @Override
    public byte readOneBit() throws MqttSocketException {
        return readBit(1)[0];
    }

    @Override
    public byte[] readBit(int bitCount) throws MqttSocketException {
        synchronized (mReadLock) {
            if (isConnected()) {
                try {
                    InputStream is = mSocket.getInputStream();
                    byte[] reader = new byte[bitCount];
                    if (is.read(reader) <= 0) {
                        throw new MqttSocketConnectException();
                    }
                    return reader;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MqttSocketReadException();
                }
            } else {
                throw new MqttSocketUnconnectedException();
            }
        }
    }

    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    public boolean isClosed() {
        return mSocket == null || mSocket.isClosed();
    }
}
