package com.dreamgyf.gmqyttf.client.callback;

import com.dreamgyf.gmqyttf.common.exception.MqttException;

public interface MqttConnectCallback extends MqttCallback {
    void onConnectSuccess();

    void onConnectFailure(MqttException e);
}
