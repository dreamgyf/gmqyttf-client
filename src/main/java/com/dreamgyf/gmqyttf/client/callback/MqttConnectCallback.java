package com.dreamgyf.gmqyttf.client.callback;

import com.dreamgyf.gmqyttf.common.exception.MqttException;

public interface MqttConnectCallback {
    void onConnectSuccess();

    void onConnectFailure(MqttException e);
}
