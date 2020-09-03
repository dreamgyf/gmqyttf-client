package com.dreamgyf.gmqyttf.client.task.connection;

import com.dreamgyf.gmqyttf.client.listener.OnMqttConnectSuccessListener;
import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttConnectReturnCode;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;
import com.dreamgyf.gmqyttf.common.exception.UnknownException;
import com.dreamgyf.gmqyttf.common.exception.connect.*;
import com.dreamgyf.gmqyttf.common.packet.MqttConnackPacket;

import java.util.concurrent.LinkedBlockingQueue;

public class MqttConnackTask extends MqttTask {

    private final LinkedBlockingQueue<MqttConnackPacket> mConnackQueue;

    private OnMqttConnectSuccessListener mConnectSuccessListener;

    public MqttConnackTask(MqttVersion version, MqttWritableSocket socket,
                           LinkedBlockingQueue<MqttConnackPacket> connackQueue) {
        super(version, socket);
        mConnackQueue = connackQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        MqttConnackPacket packet = mConnackQueue.take();
        handlerPacket(packet);
    }

    private void handlerPacket(MqttConnackPacket packet) {
        switch (getVersion()) {
            case V3_1_1:
                handlerPacketV311(packet);
                break;
        }
    }

    private void handlerPacketV311(MqttConnackPacket packet) {
        MqttException exception = null;
        switch (packet.getConnectReturnCode()) {
            case MqttConnectReturnCode.V3_1_1.ACCEPT: {
                onConnectSuccess();
                break;
            }
            case MqttConnectReturnCode.V3_1_1.UNSUPPORTED_VERSION: {
                exception = new UnsupportedVersionException();
                break;
            }
            case MqttConnectReturnCode.V3_1_1.UNQUALIFIED_CLIENT_ID: {
                exception = new UnqualifiedClientIdException();
                break;
            }
            case MqttConnectReturnCode.V3_1_1.SERVICE_UNAVAILABLE: {
                exception = new ServiceUnavailableException();
                break;
            }
            case MqttConnectReturnCode.V3_1_1.INVALID_USERNAME_OR_PASSWORD: {
                exception = new InvalidUsernameOrPasswordException();
                break;
            }
            case MqttConnectReturnCode.V3_1_1.UNAUTHORIZED: {
                exception = new UnauthorizedException();
                break;
            }
            default: {
                exception = new UnknownException();
            }
        }
        if (exception != null) {
            onMqttExceptionThrow(exception);
        }
    }

    public void setOnMqttConnectSuccessListener(OnMqttConnectSuccessListener listener) {
        mConnectSuccessListener = listener;
    }

    private void onConnectSuccess() {
        if(mConnectSuccessListener != null) {
            mConnectSuccessListener.onConnectSuccess();
        }
    }
}
