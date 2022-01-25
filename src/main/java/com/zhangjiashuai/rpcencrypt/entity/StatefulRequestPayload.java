package com.zhangjiashuai.rpcencrypt.entity;

import com.zhangjiashuai.rpcencrypt.common.Mode;

/**
 * 有状态的请求报文
 */
public class StatefulRequestPayload extends RequestPayload {
    /**
     * 签名之前是否加密
     * 默认为true,如果签名前已经显示执行过加密，需手动设置为false
     */
    private boolean encryptBeforeSign = true;
    /**
     * 验签之后是否解密
     * 默认为true,如果无需验签后自动解密，需手动设置为false
     */
    private boolean decryptAfterValidate = true;

    /**
     * 是否自动查询并填充ClientInfo
     * 调用ClientInfoStorage::findByClientId
     * 默认为true,如果无需自动填充，需手动设置为false
     */
    private boolean fillClientInfo = true;

    public StatefulRequestPayload(RequestPayload requestPayload) {
        this(requestPayload.getClientInfo(), requestPayload.getSign(), requestPayload.getPayload(), requestPayload.getMode());
    }

    public StatefulRequestPayload() {
    }

    public StatefulRequestPayload(ClientInfo clientInfo, String sign, String payload, Mode mode) {
        super(clientInfo, sign, payload, mode);
    }

    public StatefulRequestPayload(boolean encryptBeforeSign, boolean decryptAfterValidate, boolean fillClientInfo) {
        this.encryptBeforeSign = encryptBeforeSign;
        this.decryptAfterValidate = decryptAfterValidate;
        this.fillClientInfo = fillClientInfo;
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

    public boolean isFillClientInfo() {
        return fillClientInfo;
    }

    public void setFillClientInfo(boolean fillClientInfo) {
        this.fillClientInfo = fillClientInfo;
    }

    @Override
    public String toString() {
        return "StatefulRequestPayload{" +
                "encryptBeforeSign=" + encryptBeforeSign +
                ", decryptAfterValidate=" + decryptAfterValidate +
                ", fillClientInfo=" + fillClientInfo +
                "} " + super.toString();
    }
}