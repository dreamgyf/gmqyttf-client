package com.dreamgyf.gmqyttf.client.listener;


import com.dreamgyf.gmqyttf.common.throwable.exception.MqttException;

public interface OnMqttExceptionListener {
    void onMqttExceptionThrow(MqttException e);
}
