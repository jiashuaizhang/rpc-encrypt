package com.zhangjiashuai.rpcencrypt.cipher;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

import javax.crypto.SecretKey;

/**
 * AES对称加密
 */
public class AESCipher implements Cipher {

    private static final SymmetricAlgorithm ALGORITHM = SymmetricAlgorithm.AES;
    private static final int KEY_LENGTH = 32;

    @Override
    public String encrypt(String payload, String key) {
        String wrapperKey = wrapperKey(key);
        SymmetricCrypto crypto = new SymmetricCrypto(ALGORITHM, StrUtil.bytes(wrapperKey));
        return crypto.encryptBase64(payload);
    }

    @Override
    public String decrypt(String payload, String key) {
        String wrapperKey = wrapperKey(key);
        SymmetricCrypto crypto = new SymmetricCrypto(ALGORITHM, StrUtil.bytes(wrapperKey));
        return crypto.decryptStr(payload);
    }

    @Override
    public SecretKey generateKey() {
        return KeyUtil.generateKey(getAlgorithm());
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM.getValue();
    }

    protected String wrapperKey(String key) {
        if (key.length() < KEY_LENGTH) {
            return StrUtil.padAfter(key, KEY_LENGTH, '0');
        }
        if (key.length() > KEY_LENGTH) {
            return key.substring(0, KEY_LENGTH);
        }
        return key;
    }

}
