package com.zhangjiashuai.rpcencrypt.entity;

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

    public RequestPayload(ClientInfo clientInfo, String sign, String payload) {
        this.clientInfo = clientInfo;
        this.sign = sign;
        this.payload = payload;
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

    @Override
    public String toString() {
        return "RequestPayload{" +
                "clientInfo=" + clientInfo +
                ", sign='" + sign + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
