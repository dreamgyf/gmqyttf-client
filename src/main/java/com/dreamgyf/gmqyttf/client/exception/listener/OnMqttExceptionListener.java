package com.dreamgyf.gmqyttf.client.exception.listener;

import com.dreamgyf.gmqyttf.common.exception.MqttException;

public interface OnMqttExceptionListener {
    void onMqttExceptionThrow(MqttException e);
}
