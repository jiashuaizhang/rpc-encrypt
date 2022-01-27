package com.zhangjiashuai.rpcencrypt.entity;

import com.zhangjiashuai.rpcencrypt.common.Mode;

/**
 * 有状态的请求报文
 */
public class StatefulRequestPayload extends RequestPayload {

    /**
     * 运行模式
     */
    private Mode mode = Mode.CLIENT;

    /**
     * 摘要之前是否加密
     * 用于客户端模式,先对称加密报文，再用密文做摘要
     * 加密后的报文会设置到RequestPayload::payload,当关闭摘要(!this.digest)时，加密仍然会生效
     * 默认为true,如果签名前已经显式执行过加密，需手动设置为false
     */
    private boolean encryptBeforeDigest = true;

    /**
     * 是否生成或校验摘要
     * 默认为true
     */
    private boolean digest = true;

    /**
     * 验签之后是否解密
     * 用于服务端模式,解密后的报文会设置到RequestPayload::payload
     * 默认为true,如果无需验签后自动解密，需手动设置为false
     */
    private boolean decryptAfterValidate = true;

    /**
     * 是否自动查询并填充ClientInfo
     * 调用ClientInfoStorage::findByClientId
     * 默认为true,如果无需自动填充，需手动设置为false
     */
    private boolean fillClientInfo = true;

    /**
     * 是否校验参数
     * 用来防止重复检验
     */
    private boolean checkArguments = true;

    public StatefulRequestPayload(RequestPayload requestPayload) {
        this(requestPayload.getClientInfo(), requestPayload.getSign(), requestPayload.getPayload(), Mode.CLIENT);
    }

    public StatefulRequestPayload() {
    }

    public StatefulRequestPayload(ClientInfo clientInfo, String sign, String payload, Mode mode) {
        super(clientInfo, sign, payload);
        this.mode = mode;
    }

    public boolean isEncryptBeforeDigest() {
        return encryptBeforeDigest;
    }

    public void setEncryptBeforeDigest(boolean encryptBeforeDigest) {
        this.encryptBeforeDigest = encryptBeforeDigest;
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

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean isCheckArguments() {
        return checkArguments;
    }

    public void setCheckArguments(boolean checkArguments) {
        this.checkArguments = checkArguments;
    }

    public boolean isDigest() {
        return digest;
    }

    public void setDigest(boolean digest) {
        this.digest = digest;
    }

    @Override
    public String toString() {
        return "StatefulRequestPayload{" +
                "mode=" + mode +
                ", encryptBeforeDigest=" + encryptBeforeDigest +
                ", digest=" + digest +
                ", decryptAfterValidate=" + decryptAfterValidate +
                ", fillClientInfo=" + fillClientInfo +
                ", checkArguments=" + checkArguments +
                "} " + super.toString();
    }
}
