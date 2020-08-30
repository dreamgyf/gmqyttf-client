package com.dreamgyf.gmqyttf.client.task;

import com.dreamgyf.gmqyttf.client.socket.MqttWritableSocket;
import com.dreamgyf.gmqyttf.client.structure.BlockingObject;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.connect.MqttConnectException;
import com.dreamgyf.gmqyttf.common.exception.net.MqttSocketException;
import com.dreamgyf.gmqyttf.common.packet.MqttPingreqPacket;

public class MqttPingReqTask extends MqttTask {

    private final short mKeepAliveTime;

    private final BlockingObject<Long> mPingReqContainer;

    private volatile long mLastReqTime = System.currentTimeMillis();

    public MqttPingReqTask(MqttVersion version, MqttWritableSocket socket,
                           short keepAliveTime, BlockingObject<Long> pingReqContainer) {
        super(version, socket);
        mKeepAliveTime = keepAliveTime;
        mPingReqContainer = pingReqContainer;
    }

    @Override
    public void onLoop() throws InterruptedException {
        if (needSendPingReq()) {
            MqttPingreqPacket packet = new MqttPingreqPacket.Builder().build(MqttVersion.V3_1_1);
            try {
                writeSocket(packet.getPacket());
                onMqttPacketSend();
                if (!mPingReqContainer.offer(mLastReqTime)) {
                    onMqttExceptionThrow(new MqttConnectException());
                }
            } catch (MqttSocketException e) {
                e.printStackTrace();
                onMqttExceptionThrow(e);
            }
        }
        Thread.sleep(100);
    }

    private boolean needSendPingReq() {
        return System.currentTimeMillis() - mLastReqTime >= mKeepAliveTime * 1000L;
    }

    public void updateLastReqTime() {
        mLastReqTime = System.currentTimeMillis();
    }

}
