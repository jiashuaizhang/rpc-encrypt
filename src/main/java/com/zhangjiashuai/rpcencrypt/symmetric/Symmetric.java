package com.zhangjiashuai.rpcencrypt.symmetric;

import com.zhangjiashuai.rpcencrypt.common.Cipher;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;

import javax.crypto.SecretKey;

/**
 * 加密接口
 */
public interface Symmetric extends Cipher {

    @Override
    default String encrypt(StatefulRequestPayload requestPayload) {
        return encrypt(requestPayload.getPayload(), requestPayload.getClientInfo());
    }

    /**
     * 对称加密
     * @param payload 报文
     * @param clientInfo 客户端信息，使用clientSecret作为密钥
     * @return
     */
    @Override
    default String encrypt(String payload, ClientInfo clientInfo) {
        return encrypt(payload, clientInfo.getClientSecret());
    }

    /**
     * 对称解密
     * @param payload 报文
     * @param clientInfo 客户端信息，使用clientSecret作为密钥
     * @return
     */
    @Override
    default String decrypt(String payload, ClientInfo clientInfo) {
        return decrypt(payload, clientInfo.getClientSecret());
    }

    /**
     * 生成一个随机密钥
     * @return
     */
    SecretKey generateKey();

}
