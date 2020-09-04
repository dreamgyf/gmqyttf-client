package com.dreamgyf.gmqyttf.client.callback;

import com.dreamgyf.gmqyttf.common.throwable.exception.MqttException;

public interface MqttConnectCallback extends MqttCallback {
    void onConnectSuccess();

    void onConnectFailure(MqttException e);
}
