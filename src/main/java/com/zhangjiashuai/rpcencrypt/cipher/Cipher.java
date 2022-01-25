package com.zhangjiashuai.rpcencrypt.cipher;

import com.zhangjiashuai.rpcencrypt.common.Algorithm;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.RequestPayload;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;

import javax.crypto.SecretKey;

/**
 * 加密接口
 */
public interface Cipher extends Algorithm {

    default String encrypt(StatefulRequestPayload requestPayload) {
        return encrypt(requestPayload.getPayload(), requestPayload.getClientInfo());
    }

    default String encrypt(String payload, ClientInfo clientInfo) {
        return encrypt(payload, clientInfo.getClientSecret());
    }

    String encrypt(String payload, String key);

    default String decrypt(RequestPayload requestPayload) {
        return decrypt(requestPayload.getPayload(), requestPayload.getClientInfo());
    }

    default String decrypt(String payload, ClientInfo clientInfo) {
        return decrypt(payload, clientInfo.getClientSecret());
    }

    String decrypt(String payload, String key);

    /**
     * 生成一个随机密钥
     * @return
     */
    SecretKey generateKey();

}
