package com.dreamgyf.gmqyttf.client.task.ping;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.structure.BlockingObject;
import com.dreamgyf.gmqyttf.client.task.MqttTask;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.packet.MqttPingrespPacket;
import com.dreamgyf.gmqyttf.common.throwable.exception.connect.MqttConnectException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MqttPingRespTask extends MqttTask {

    private final short mKeepAliveTime;

    private final BlockingObject<Long> mPingReqContainer;

    private final LinkedBlockingQueue<MqttPingrespPacket> mPingRespPacketQueue;

    public MqttPingRespTask(MqttVersion version, MqttWritableSocket socket,
                            short keepAliveTime, BlockingObject<Long> pingReqContainer,
                            LinkedBlockingQueue<MqttPingrespPacket> pingRespPacketQueue) {
        super(version, socket);
        mKeepAliveTime = keepAliveTime;
        mPingReqContainer = pingReqContainer;
        mPingRespPacketQueue = pingRespPacketQueue;
    }

    @Override
    public void onLoop() throws InterruptedException {
        mPingReqContainer.take();
        MqttPingrespPacket packet = mPingRespPacketQueue.poll(getTimeOutTime(), TimeUnit.MILLISECONDS);
        if (packet == null) {
            onMqttExceptionThrow(new MqttConnectException());
        }
    }

    private long getTimeOutTime() {
        return (long) (mKeepAliveTime * 1.5 * 1000L);
    }
}
