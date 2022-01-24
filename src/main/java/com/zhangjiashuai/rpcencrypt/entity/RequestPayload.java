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

    /**
     * 签名之前是否加密
     * 默认为true,如果签名前已经显示执行过加密，需手动设置为false
     */
    private boolean encryptBeforeSign;
    /**
     * 验签之后是否解密
     * 默认为true,如果无需验签后自动解密，需手动设置为false
     */
    private boolean decryptAfterValidate;

    public RequestPayload() {
        this(Mode.CLIENT,true, true);
    }

    public RequestPayload(Mode mode, boolean encryptBeforeSign, boolean decryptAfterValidate) {
        this.mode = mode;
        this.encryptBeforeSign = encryptBeforeSign;
        this.decryptAfterValidate = decryptAfterValidate;
    }

    public RequestPayload(ClientInfo clientInfo, String sign, String payload,
                          Mode mode, boolean encryptBeforeSign, boolean decryptAfterValidate) {
        this(mode,encryptBeforeSign, decryptAfterValidate);
        this.clientInfo = clientInfo;
        this.sign = sign;
        this.payload = payload;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public boolean isEncryptBeforeSign() {
        return encryptBeforeSign;
    }

    public void setEncryptBeforeSign(boolean encryptBeforeSign) {
        this.encryptBeforeSign = encryptBeforeSign;
    }

    public boolean isDecryptAfterValidate() {
        return decryptAfterValidate;
    }

    public void setDecryptAfterValidate(boolean decryptAfterValidate) {
        this.decryptAfterValidate = decryptAfterValidate;
    }

    @Override
    public String toString() {
        return "RequestPayload{" +
                "clientInfo=" + clientInfo +
                ", sign='" + sign + '\'' +
                ", payload='" + payload + '\'' +
                ", mode=" + mode +
                ", encryptBeforeSign=" + encryptBeforeSign +
                ", decryptAfterValidate=" + decryptAfterValidate +
                '}';
    }
}
