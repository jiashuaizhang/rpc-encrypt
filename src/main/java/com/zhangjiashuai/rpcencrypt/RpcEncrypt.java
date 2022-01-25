package com.zhangjiashuai.rpcencrypt;

import com.zhangjiashuai.rpcencrypt.common.Mode;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.RequestPayload;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;
import com.zhangjiashuai.rpcencrypt.sign.Signature;
import com.zhangjiashuai.rpcencrypt.sign.SignatureMismatchException;
import com.zhangjiashuai.rpcencrypt.storage.ClientInfoStorage;

/**
 * 程序入口
 */
public class RpcEncrypt {

    private Signature signature;
    private ClientInfoStorage clientInfoStorage;

    /**
     * 构造器
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    public RpcEncrypt() {

    }

    public RpcEncrypt(Signature signature, ClientInfoStorage clientInfoStorage) {
        this.signature = signature;
        this.clientInfoStorage = clientInfoStorage;
    }

    /**
     * 签名或验签
     * 附带加密（客户端），除非设置requestPayload::encryptBeforeSign为false
     * 附带解密（服务端），除非设置requestPayload::decryptAfterValidate为false
     * @param requestPayload
     * @return
     */
    RequestPayload work(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        if (requestPayload.isFillClientInfo()) {
            ClientInfo clientInfo = clientInfoStorage.find(requestPayload.getClientInfo());
            if (clientInfo == null) {
                throw new NullPointerException("no clientInfo from storage");
            }
            requestPayload.setClientInfo(clientInfo);
        }
        if (requestPayload.getMode() == Mode.CLIENT) {
            return clientSign(requestPayload);
        }
        return serverValidate(requestPayload);
    }

    /**
     * 服务端验签
     * 附带解密，除非设置requestPayload::decryptAfterValidate为false
     * @param requestPayload
     * @return
     */
    public RequestPayload serverValidate(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        signature.serverValidate(requestPayload);
        return requestPayload;
    }

    /**
     * 客户端签名
     * 附带加密，除非设置requestPayload::encryptBeforeSign为false
     * @param requestPayload
     * @return
     */
    public RequestPayload clientSign(StatefulRequestPayload requestPayload) {
        signature.clientSign(requestPayload);
        return requestPayload;
    }

    public static class Builder {

        private RpcEncrypt rpcEncrypt;

        public Builder() {
            this.rpcEncrypt = new RpcEncrypt();
        }

        public Builder signature(Signature signature) {
            rpcEncrypt.signature = signature;
            return this;
        }

        public Builder clientInfoStorage(ClientInfoStorage clientInfoStorage) {
            rpcEncrypt.clientInfoStorage = clientInfoStorage;
            return this;
        }

        public RpcEncrypt build() {
            return rpcEncrypt;
        }
    }
}
