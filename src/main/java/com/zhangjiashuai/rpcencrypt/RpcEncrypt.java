package com.zhangjiashuai.rpcencrypt;

import cn.hutool.core.lang.Assert;
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
     * 附带加密（客户端），除非设置requestPayload::encryptBeforeDigest为false
     * 附带解密（服务端），除非设置requestPayload::decryptAfterValidate为false
     * @param requestPayload
     * @return
     */
    public StatefulRequestPayload work(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        beforeWork(requestPayload);
        if (requestPayload.getMode() == Mode.SERVER) {
            return serverValidate(requestPayload);
        }
        return clientSign(requestPayload);
    }

    /**
     * 服务端验签
     * 附带解密，除非设置requestPayload::decryptAfterValidate为false
     * @param requestPayload
     * @throws SignatureMismatchException 签名不匹配
     * @return
     */
    public StatefulRequestPayload serverValidate(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        beforeWork(requestPayload);
        signature.serverValidate(requestPayload);
        return requestPayload;
    }

    /**
     * 客户端签名
     * 附带加密，除非设置requestPayload::encryptBeforeDigest为false
     * @param requestPayload
     * @return
     */
    public StatefulRequestPayload clientSign(StatefulRequestPayload requestPayload) {
        beforeWork(requestPayload);
        signature.clientSign(requestPayload);
        return requestPayload;
    }

    protected void checkArguments(StatefulRequestPayload requestPayload) {
        Assert.notEmpty(requestPayload.getPayload(), "payload can't be empty");
        ClientInfo clientInfo = requestPayload.getClientInfo();
        Assert.notNull(clientInfo, "clientInfo can't be null");
        Assert.notEmpty(clientInfo.getClientId(), "clientId can't be empty");
        Assert.notEmpty(clientInfo.getClientSecret(), "clientSecret can't be empty");
        Assert.notEmpty(clientInfo.getPublicKeyServer(), "publicKeyServer can't be empty");
        if (requestPayload.getMode() == Mode.SERVER) {
            Assert.notEmpty(clientInfo.getPrivateKeyServer(), "privateKeyServer can't be empty");
            Assert.notEmpty(requestPayload.getSign(), "sign can't be empty");
        }
    }

    /**
     * 查询并填充clientInfo
     * @param requestPayload
     */
    protected void fillClientInfo(StatefulRequestPayload requestPayload) {
        ClientInfo clientInfo = clientInfoStorage.find(requestPayload.getClientInfo());
        Assert.notNull(clientInfo, "no clientInfo found in the storage: " + requestPayload.getClientInfo());
        requestPayload.setClientInfo(clientInfo);
    }

    protected void beforeWork(StatefulRequestPayload requestPayload) {
        Assert.notNull(requestPayload, "requestPayload can't be null");
        if (requestPayload.isCheckArguments()) {
            checkArguments(requestPayload);
            requestPayload.setCheckArguments(false);
        }
        if (requestPayload.isFillClientInfo()) {
            fillClientInfo(requestPayload);
            requestPayload.setFillClientInfo(false);
        }
    }

    public Signature getSignature() {
        return signature;
    }

    public ClientInfoStorage getClientInfoStorage() {
        return clientInfoStorage;
    }

    public static final class Builder {

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
