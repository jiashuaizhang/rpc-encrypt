package com.zhangjiashuai.rpcencrypt.sign;

import cn.hutool.core.util.StrUtil;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;
import com.zhangjiashuai.rpcencrypt.sign.asymmetric.Asymmetric;
import com.zhangjiashuai.rpcencrypt.sign.asymmetric.RSACipher;
import com.zhangjiashuai.rpcencrypt.sign.digest.Digest;
import com.zhangjiashuai.rpcencrypt.sign.digest.HMACDigest;
import com.zhangjiashuai.rpcencrypt.symmetric.AESCipher;
import com.zhangjiashuai.rpcencrypt.symmetric.Symmetric;

import java.util.List;

/**
 * 默认签名实现
 * RSA + HMAC
 */
public class DefaultSignature implements Signature {

    private Digest digest;
    private Symmetric symmetricCipher;
    private Asymmetric asymmetricCipher;

    public DefaultSignature() {
        this(new HMACDigest(), new AESCipher(), new RSACipher());
    }

    public DefaultSignature(Digest digest, Symmetric symmetricCipher, Asymmetric asymmetricCipher) {
        this.digest = digest;
        this.symmetricCipher = symmetricCipher;
        this.asymmetricCipher = asymmetricCipher;
    }

    @Override
    public String clientSign(StatefulRequestPayload requestPayload) {
        ClientInfo clientInfo = requestPayload.getClientInfo();
        // signature part 1
        String str2Encrypt = asymmetricCipher.getStr2Encrypt(requestPayload);
        String clientEncryptStr = asymmetricCipher.encrypt(str2Encrypt, clientInfo.getPublicKeyServer());
        String payload;
        // encrypt the payload
        if (requestPayload.isEncryptBeforeDigest()) {
            payload = symmetricCipher.encrypt(requestPayload);
        } else {
            payload = requestPayload.getPayload();
        }
        // signature part 2
        String sign = clientEncryptStr;
        if (requestPayload.isDigest()) {
            String digestStr = digest.digestPayload(payload, clientInfo);
            sign += (SIGN_SEPARATOR + digestStr);
        }
        requestPayload.setSign(sign);
        requestPayload.setPayload(payload);
        return sign;
    }

    @Override
    public boolean serverValidate(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        String clientSign = requestPayload.getSign();
        List<String> signParts = StrUtil.split(clientSign, SIGN_SEPARATOR);
        ClientInfo clientInfo = requestPayload.getClientInfo();
        // digest validate (signature part 2)
        if (requestPayload.isDigest()) {
            if (signParts.size() < 2) {
                throw new SignatureMismatchException("invalid sign argument, digest is missing: " + clientSign);
            }
            String digestStr = digest.digestPayload(requestPayload.getPayload(), clientInfo);
            if (!digestStr.equals(signParts.get(1))) {
                throw new SignatureMismatchException("digest mismatch");
            }
        }
        // asymmetric validate (signature part 1)
        String serverDecryptStr = asymmetricCipher.decrypt(signParts.get(0), clientInfo.getPrivateKeyServer());
        if (!serverDecryptStr.equals(asymmetricCipher.getStr2Encrypt(requestPayload))) {
            throw new SignatureMismatchException("asymmetric signature mismatch");
        }
        // decrypt the payload
        if (requestPayload.isDecryptAfterValidate()) {
            String decryptStr = symmetricCipher.decrypt(requestPayload);
            requestPayload.setPayload(decryptStr);
        }
        return true;
    }

    @Override
    public Digest getDigest() {
        return digest;
    }

    @Override
    public void setDigest(Digest digest) {
        this.digest = digest;
    }

    @Override
    public Symmetric getSymmetricCipher() {
        return symmetricCipher;
    }

    @Override
    public void setSymmetricCipher(Symmetric symmetricCipher) {
        this.symmetricCipher = symmetricCipher;
    }

    @Override
    public Asymmetric getAsymmetricCipher() {
        return asymmetricCipher;
    }

    @Override
    public void setAsymmetricCipher(Asymmetric asymmetricCipher) {
        this.asymmetricCipher = asymmetricCipher;
    }
}
