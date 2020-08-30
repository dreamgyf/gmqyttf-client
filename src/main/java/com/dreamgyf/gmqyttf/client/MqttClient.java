package com.dreamgyf.gmqyttf.client;

import com.dreamgyf.gmqyttf.client.callback.MqttConnectCallback;
import com.dreamgyf.gmqyttf.client.listener.OnMqttExceptionListener;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.exception.MqttException;
import com.dreamgyf.gmqyttf.common.exception.net.IllegalServerException;
import com.dreamgyf.gmqyttf.common.exception.net.MqttConnectedException;
import com.dreamgyf.gmqyttf.common.exception.net.MqttNetworkException;
import com.dreamgyf.gmqyttf.common.packet.MqttConnectPacket;

import java.util.regex.Pattern;

public class MqttClient {

    private MqttClientController controller;

    private boolean isConnected;

    public void connect(String server, int port) {
        connect(server, port, null);
    }

    public void connect(String server, int port, MqttConnectCallback callback) {
        if (server == null || server.equals("") || port == 0) {
            if (callback != null) {
                callback.onConnectFailure(new IllegalServerException("Illegal server or port"));
            }
        }
        if (isConnected()) {
            if (callback != null) {
                callback.onConnectFailure(new MqttConnectedException("Already connected"));
            }
        }

        //开启服务
        initController();
        try {
            controller.start(server, port);
        } catch (MqttNetworkException e) {
            if (callback != null) {
                callback.onConnectFailure(e);
            }
        }

        //构建报文
        MqttConnectPacket packet = new MqttConnectPacket.Builder()
                .cleanSession(cleanSession)
                .willFlag(willFlag)
                .willQoS(willQoS)
                .willRetain(willRetain)
                .usernameFlag(usernameFlag)
                .passwordFlag(passwordFlag)
                .keepAliveTime(keepAliveTime)
                .clientId(clientId)
                .willTopic(willTopic)
                .willMessage(willMessage)
                .username(username)
                .password(password)
                .build(version);
        controller.connect(packet, callback);
    }

    private void initController() {
        controller = new MqttClientController(version, keepAliveTime);
        controller.init();
        controller.setOnMqttExceptionListener(new OnMqttExceptionListener() {
            @Override
            public void onMqttExceptionThrow(MqttException e) {
                isConnected = false;
                controller.stop();
                System.err.println("出现异常，断开连接");
            }
        });
    }

    public void disconnect() {
        controller.disconnect();
        controller.stop();
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 协议版本
     */
    private MqttVersion version;
    /**
     * 清理会话 Clean Session
     */
    private boolean cleanSession;
    /**
     * 遗嘱标志 Will Flag
     */
    private boolean willFlag;
    /**
     * 遗嘱QoS Will QoS
     */
    private int willQoS;
    /**
     * 遗嘱保留 Will Retain
     */
    private boolean willRetain;
    /**
     * 用户名标志 User Name Flag
     */
    private boolean usernameFlag;
    /**
     * 密码标志 Password Flag
     */
    private boolean passwordFlag;
    /**
     * 保持连接 Keep Alive
     */
    private short keepAliveTime;
    /**
     * 客户端标识符 Client Identifier
     */
    private String clientId;
    /**
     * 遗嘱主题 Will Topic
     */
    private String willTopic;
    /**
     * 遗嘱消息 Will Message
     */
    private String willMessage;
    /**
     * 用户名 User Name
     */
    private String username;
    /**
     * 密码 Password
     */
    private String password;

    private MqttClient(MqttVersion version, boolean cleanSession, boolean willFlag, int willQoS,
                       boolean willRetain, boolean usernameFlag, boolean passwordFlag, short keepAliveTime,
                       String clientId, String willTopic, String willMessage, String username, String password) {
        this.version = version;
        this.cleanSession = cleanSession;
        this.willFlag = willFlag;
        this.willQoS = willQoS;
        this.willRetain = willRetain;
        this.usernameFlag = usernameFlag;
        this.passwordFlag = passwordFlag;
        this.keepAliveTime = keepAliveTime;
        this.clientId = clientId;
        this.willTopic = willTopic;
        this.willMessage = willMessage;
        this.username = username;
        this.password = password;
    }

    public MqttVersion getVersion() {
        return version;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public boolean isWillFlag() {
        return willFlag;
    }

    public int getWillQoS() {
        return willQoS;
    }

    public boolean isWillRetain() {
        return willRetain;
    }

    public boolean isUsernameFlag() {
        return usernameFlag;
    }

    public boolean isPasswordFlag() {
        return passwordFlag;
    }

    public short getKeepAliveTime() {
        return keepAliveTime;
    }

    public String getClientId() {
        return clientId;
    }

    public String getWillTopic() {
        return willTopic;
    }

    public String getWillMessage() {
        return willMessage;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder {
        /**
         * 清理会话 Clean Session
         */
        private boolean cleanSession;
        /**
         * 遗嘱标志 Will Flag
         */
        private boolean willFlag;
        /**
         * 遗嘱QoS Will QoS
         */
        private int willQoS = 0;
        /**
         * 遗嘱保留 Will Retain
         */
        private boolean willRetain;
        /**
         * 用户名标志 User Name Flag
         */
        private boolean usernameFlag;
        /**
         * 密码标志 Password Flag
         */
        private boolean passwordFlag;
        /**
         * 保持连接 Keep Alive
         */
        private short keepAliveTime = 10;
        /**
         * 客户端标识符 Client Identifier
         */
        private String clientId = "default";
        /**
         * 遗嘱主题 Will Topic
         */
        private String willTopic = "";
        /**
         * 遗嘱消息 Will Message
         */
        private String willMessage = "";
        /**
         * 用户名 User Name
         */
        private String username = "";
        /**
         * 密码 Password
         */
        private String password = "";

        public Builder cleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public Builder willFlag(boolean willFlag) {
            this.willFlag = willFlag;
            return this;
        }

        public Builder willQoS(int willQoS) {
            if (willQoS < 0 || willQoS > 2)
                throw new IllegalArgumentException("The value of QoS must be between 0 and 2.");
            this.willQoS = willQoS;
            return this;
        }

        public Builder willRetain(boolean willRetain) {
            this.willRetain = willRetain;
            return this;
        }

        public Builder usernameFlag(boolean usernameFlag) {
            this.usernameFlag = usernameFlag;
            return this;
        }

        public Builder passwordFlag(boolean passwordFlag) {
            this.passwordFlag = passwordFlag;
            return this;
        }

        public Builder keepAliveTime(short keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public Builder clientId(String clientId) {
            if (!Pattern.matches("^[a-zA-Z0-9]+$", clientId))
                throw new IllegalArgumentException("illegal character,Client ID can only contain letters and Numbers");
            this.clientId = clientId;
            return this;
        }

        public Builder willTopic(String willTopic) {
            this.willTopic = willTopic;
            return this;
        }

        public Builder willMessage(String willMessage) {
            this.willMessage = willMessage;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public MqttClient build(MqttVersion version) {
            return new MqttClient(version, cleanSession, willFlag, willQoS,
                    willRetain, usernameFlag, passwordFlag, keepAliveTime,
                    clientId, willTopic, willMessage, username, password);
        }
    }

}
