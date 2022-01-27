package com.zhangjiashuai.rpcencrypt.sign.asymmetric;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;

import java.security.KeyPair;

/**
 * RSA非对称加密
 */
public class RSACipher implements Asymmetric {

    private static final AsymmetricAlgorithm ALGORITHM = AsymmetricAlgorithm.RSA_ECB_PKCS1;
    private static final int MAX_ASYMMETRIC_ENCRYPT_LENGTH = 117;

    @Override
    public String encrypt(String payload, String key) {
        AsymmetricCrypto clientCrypto = new AsymmetricCrypto(ALGORITHM, null, key);
        byte[] data = payload.getBytes(CHARSET);
        byte[] clientEncrypt = clientCrypto.encrypt(data, KeyType.PublicKey);
        return Base64Encoder.encode(clientEncrypt);
    }

    @Override
    public String decrypt(String payload, String key) {
        AsymmetricCrypto serverCrypto = new AsymmetricCrypto(ALGORITHM, key,null);
        byte[] serverDecrypt = serverCrypto.decrypt(payload, KeyType.PrivateKey);
        return new String(serverDecrypt, CHARSET);
    }

    @Override
    public String getStr2Encrypt(StatefulRequestPayload requestPayload) {
        String str4Cipher = Asymmetric.super.getStr2Encrypt(requestPayload);
        byte[] bytes = str4Cipher.getBytes(CHARSET);
        if (bytes.length <= MAX_ASYMMETRIC_ENCRYPT_LENGTH) {
            return str4Cipher;
        }
        byte[] targetArray = new byte[MAX_ASYMMETRIC_ENCRYPT_LENGTH];
        ArrayUtil.copy(bytes, targetArray, MAX_ASYMMETRIC_ENCRYPT_LENGTH);
        return new String(targetArray, CHARSET);
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM.getValue();
    }

    @Override
    public KeyPair generateKeyPair() {
        return KeyUtil.generateKeyPair(getAlgorithm());
    }
}
