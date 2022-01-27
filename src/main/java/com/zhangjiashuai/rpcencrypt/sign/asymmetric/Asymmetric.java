package com.zhangjiashuai.rpcencrypt.sign.asymmetric;

import com.zhangjiashuai.rpcencrypt.common.Cipher;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;

import java.security.KeyPair;

import static com.zhangjiashuai.rpcencrypt.sign.Signature.SIGN_SEPARATOR;

/**
 * 非对称加密接口
 */
public interface Asymmetric extends Cipher {

    /**
     * 获取需要加密的字符串
     * @param requestPayload
     * @return clientId + clientSecret
     */
    default String getStr2Encrypt(StatefulRequestPayload requestPayload) {
        ClientInfo clientInfo = requestPayload.getClientInfo();
        return clientInfo.getClientId() + clientInfo.getClientSecret();
    }

    /**
     * 获取需要解密的字符串
     * @param requestPayload
     * @return 签名的第一部分
     */
    default String getStr2Decrypt(StatefulRequestPayload requestPayload) {
        String sign = requestPayload.getSign();
        return sign.substring(0, sign.indexOf(SIGN_SEPARATOR));
    }


    @Override
    default String encrypt(StatefulRequestPayload requestPayload) {
        return encrypt(getStr2Encrypt(requestPayload), requestPayload.getClientInfo());
    }

    /**
     * 公钥加密
     * @param payload 报文
     * @param clientInfo 客户端信息，使用publicKeyServer作为密钥
     * @return
     */
    @Override
    default String encrypt(String payload, ClientInfo clientInfo) {
        return encrypt(payload, clientInfo.getPublicKeyServer());
    }

    @Override
    default String decrypt(StatefulRequestPayload requestPayload) {
        ClientInfo clientInfo = requestPayload.getClientInfo();
        return decrypt(getStr2Decrypt(requestPayload), clientInfo.getPrivateKeyServer());
    }

    /**
     * 私钥解密
     * @param payload 报文
     * @param clientInfo 客户端信息，使用privateKeyServer作为密钥
     * @return
     */
    @Override
    default String decrypt(String payload, ClientInfo clientInfo) {
        return decrypt(payload, clientInfo.getPrivateKeyServer());
    }

    /**
     * 生成随机密钥对
     * @return
     */
    KeyPair generateKeyPair();

}
