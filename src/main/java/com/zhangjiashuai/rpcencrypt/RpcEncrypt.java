package com.zhangjiashuai.rpcencrypt;

import com.zhangjiashuai.rpcencrypt.common.Mode;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;
import com.zhangjiashuai.rpcencrypt.sign.DefaultSignature;
import com.zhangjiashuai.rpcencrypt.sign.Signature;
import com.zhangjiashuai.rpcencrypt.sign.SignatureMismatchException;
import com.zhangjiashuai.rpcencrypt.sign.asymmetric.Asymmetric;
import com.zhangjiashuai.rpcencrypt.sign.asymmetric.RSACipher;
import com.zhangjiashuai.rpcencrypt.sign.digest.Digest;
import com.zhangjiashuai.rpcencrypt.sign.digest.HMACDigest;
import com.zhangjiashuai.rpcencrypt.storage.ClientInfoStorage;
import com.zhangjiashuai.rpcencrypt.storage.InMemoryClientInfoStorage;
import com.zhangjiashuai.rpcencrypt.symmetric.AESCipher;
import com.zhangjiashuai.rpcencrypt.symmetric.Symmetric;

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
        this(new DefaultSignature(), new InMemoryClientInfoStorage());
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
    StatefulRequestPayload work(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        if (requestPayload.isFillClientInfo()) {
            ClientInfo clientInfo = clientInfoStorage.find(requestPayload.getClientInfo());
            if (clientInfo == null) {
                throw new NullPointerException("no clientInfo from storage: " + requestPayload.getClientInfo());
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
     * @throws SignatureMismatchException 签名不匹配
     * @return
     */
    public StatefulRequestPayload serverValidate(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        signature.serverValidate(requestPayload);
        return requestPayload;
    }

    /**
     * 客户端签名
     * 附带加密，除非设置requestPayload::encryptBeforeSign为false
     * @param requestPayload
     * @return
     */
    public StatefulRequestPayload clientSign(StatefulRequestPayload requestPayload) {
        signature.clientSign(requestPayload);
        return requestPayload;
    }

    public Signature getSignature() {
        return signature;
    }

    public ClientInfoStorage getClientInfoStorage() {
        return clientInfoStorage;
    }

    public static class Builder {

        private Symmetric symmetric;

        private Asymmetric asymmetric;

        private Digest digest;

        private Signature signature;

        private ClientInfoStorage clientInfoStorage;

        public Builder signature(Signature signature) {
            this.signature = signature;
            return this;
        }

        public Builder clientInfoStorage(ClientInfoStorage clientInfoStorage) {
            this.clientInfoStorage = clientInfoStorage;
            return this;
        }

        public Builder symmetric(Symmetric symmetric) {
            this.symmetric = symmetric;
            return this;
        }

        public Builder asymmetric(Asymmetric asymmetric) {
            this.asymmetric = asymmetric;
            return this;
        }

        public Builder digest(Digest digest) {
            this.digest = digest;
            return this;
        }

        public RpcEncrypt build() {
            if (symmetric == null) {
                symmetric = new AESCipher();
            }
            if (asymmetric == null) {
                asymmetric = new RSACipher();
            }
            if (digest == null) {
                digest = new HMACDigest();
            }
            if (signature == null) {
                signature = new DefaultSignature(digest, symmetric, asymmetric);
            } else {
                signature.setSymmetricCipher(symmetric);
                signature.setAsymmetricCipher(asymmetric);
                signature.setDigest(digest);
            }
            if (clientInfoStorage == null) {
                clientInfoStorage = new InMemoryClientInfoStorage();
            }
            return new RpcEncrypt(signature, clientInfoStorage);
        }
    }
}
