package com.zhangjiashuai.rpcencrypt.sign;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import com.zhangjiashuai.rpcencrypt.cipher.AESCipher;
import com.zhangjiashuai.rpcencrypt.cipher.Cipher;
import com.zhangjiashuai.rpcencrypt.digest.Digest;
import com.zhangjiashuai.rpcencrypt.digest.HMACDigest;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;

import java.security.KeyPair;

/**
 * RSA非对称加密
 * 用于加密少量数据生成签名
 */
public class RSASignature implements Signature {

    private static final AsymmetricAlgorithm ALGORITHM = AsymmetricAlgorithm.RSA_ECB_PKCS1;
    private static final int MAX_ASYMMETRIC_ENCRYPT_LENGTH = 117;

    private Digest digest;
    private Cipher cipher;

    public RSASignature() {
        this(new HMACDigest(), new AESCipher());
    }

    public RSASignature(Digest digest, Cipher cipher) {
        this.digest = digest;
        this.cipher = cipher;
    }

    @Override
    public String getStr2Sign(StatefulRequestPayload requestPayload) {
        String str2Sign = Signature.super.getStr2Sign(requestPayload);
        if (StrUtil.length(str2Sign) <= MAX_ASYMMETRIC_ENCRYPT_LENGTH) {
            return str2Sign;
        }
        return str2Sign.substring(0, MAX_ASYMMETRIC_ENCRYPT_LENGTH);
    }

    @Override
    public String clientSign(StatefulRequestPayload requestPayload) {
        ClientInfo clientInfo = requestPayload.getClientInfo();
        // signature part 1
        AsymmetricCrypto clientCrypto = new AsymmetricCrypto(ALGORITHM, null, clientInfo.getPublicKeyServer());
        byte[] data = StrUtil.bytes(getStr2Sign(requestPayload));
        byte[] clientEncrypt = clientCrypto.encrypt(data, KeyType.PublicKey);
        String clientEncryptStr = Base64Encoder.encode(clientEncrypt);
        String payload;
        // encrypt the payload
        if (requestPayload.isEncryptBeforeSign()) {
            payload = cipher.encrypt(requestPayload);
        } else {
            payload = requestPayload.getPayload();
        }
        // signature part 2
        String digestStr = digest.digestPayload(payload, clientInfo);
        String sign = clientEncryptStr + SIGN_SEPARATOR + digestStr;
        requestPayload.setSign(sign);
        requestPayload.setPayload(payload);
        return sign;
    }

    @Override
    public boolean serverValidate(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        String clientSign = requestPayload.getSign();
        if (clientSign == null) {
            throw new NullPointerException("signature must not be null");
        }
        String[] signArray = StrUtil.splitToArray(clientSign, SIGN_SEPARATOR);
        if (signArray.length != 2) {
            throw new SignatureMismatchException("invalid sign argument: " + clientSign);
        }
        // digest validate (signature part 2)
        String digestStr = digest.digestPayload(requestPayload);
        if (!digestStr.equals(signArray[1])) {
            throw new SignatureMismatchException("digest mismatch");
        }
        // asymmetric validate (signature part 1)
        ClientInfo clientInfo = requestPayload.getClientInfo();
        AsymmetricCrypto serverCrypto = new AsymmetricCrypto(ALGORITHM, clientInfo.getPrivateKeyServer(), clientInfo.getPublicKeyServer());
        byte[] serverDecrypt = serverCrypto.decrypt(signArray[0], KeyType.PrivateKey);
        String serverDecryptStr = StrUtil.utf8Str(serverDecrypt);
        if (!serverDecryptStr.equals(getStr2Sign(requestPayload))) {
            throw new SignatureMismatchException("asymmetric signature mismatch");
        }
        // decrypt the payload
        if (requestPayload.isDecryptAfterValidate()) {
            String decryptStr = cipher.decrypt(requestPayload);
            requestPayload.setPayload(decryptStr);
        }
        return true;
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM.getValue();
    }

    @Override
    public String[] generateKeyPair() {
        KeyPair keyPair = KeyUtil.generateKeyPair(getAlgorithm());
        return new String[]{Base64Encoder.encode(keyPair.getPrivate().getEncoded()), Base64Encoder.encode(keyPair.getPublic().getEncoded())};
    }

    public Digest getDigest() {
        return digest;
    }

    @Override
    public void setDigest(Digest digest) {
        this.digest = digest;
    }

    public Cipher getCipher() {
        return cipher;
    }

    @Override
    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}
