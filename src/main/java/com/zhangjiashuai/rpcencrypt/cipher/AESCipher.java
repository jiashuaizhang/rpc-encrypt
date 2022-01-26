package com.zhangjiashuai.rpcencrypt.cipher;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.sun.istack.internal.NotNull;

import javax.crypto.SecretKey;

/**
 * AES对称加密
 */
public class AESCipher implements Cipher {

    private static final SymmetricAlgorithm ALGORITHM = SymmetricAlgorithm.AES;
    private static final int KEY_LENGTH = 32;

    @Override
    public String encrypt(String payload, String key) {
        SymmetricCrypto crypto = new SymmetricCrypto(ALGORITHM, wrapperKey(key));
        return crypto.encryptBase64(payload.getBytes(CHARSET));
    }

    @Override
    public String decrypt(String payload, String key) {
        SymmetricCrypto crypto = new SymmetricCrypto(ALGORITHM, wrapperKey(key));
        return crypto.decryptStr(payload.getBytes(CHARSET), CHARSET);
    }

    @Override
    public SecretKey generateKey() {
        return KeyUtil.generateKey(getAlgorithm());
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM.getValue();
    }

    protected byte[] wrapperKey(@NotNull String key) {
        byte[] bytes = key.getBytes(CHARSET);
        int lengthIn = bytes.length;
        if (lengthIn == KEY_LENGTH) {
            return bytes;
        }
        byte[] targetArray = new byte[KEY_LENGTH];
        if (lengthIn < KEY_LENGTH) {
            ArrayUtil.copy(bytes, targetArray, lengthIn);
            for (int i = KEY_LENGTH - lengthIn; i < KEY_LENGTH; i++) {
                // '0'
                targetArray[i] = 48;
            }
        } else {
            ArrayUtil.copy(bytes, targetArray, KEY_LENGTH);
        }
        return targetArray;
    }

}
