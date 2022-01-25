package com.zhangjiashuai.rpcencrypt.entity;

import com.zhangjiashuai.rpcencrypt.common.Mode;

/**
 * 请求报文
 */
public class RequestPayload {
    /**
     * 客户端配置
     */
    private ClientInfo clientInfo;
    /**
     * 签名
     */
    private String sign;
    /**
     * 报文
     */
    private String payload;
    /**
     * 运行模式
     */
    private Mode mode;

    public RequestPayload(ClientInfo clientInfo, String sign, String payload, Mode mode) {
        this.clientInfo = clientInfo;
        this.sign = sign;
        this.payload = payload;
        this.mode = mode;
    }

    public RequestPayload() {
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "RequestPayload{" +
                "clientInfo=" + clientInfo +
                ", sign='" + sign + '\'' +
                ", payload='" + payload + '\'' +
                ", mode=" + mode +
                '}';
    }
}
