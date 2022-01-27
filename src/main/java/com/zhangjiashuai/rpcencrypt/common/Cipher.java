package com.zhangjiashuai.rpcencrypt.common;

import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;

/**
 * 加密接口
 */
public interface Cipher extends Algorithm {

    String encrypt(StatefulRequestPayload requestPayload);

    String encrypt(String payload, ClientInfo clientInfo);

    String encrypt(String payload, String key);

    default String decrypt(StatefulRequestPayload requestPayload) {
        return decrypt(requestPayload.getPayload(), requestPayload.getClientInfo());
    }

    String decrypt(String payload, ClientInfo clientInfo);

    String decrypt(String payload, String key);

}
