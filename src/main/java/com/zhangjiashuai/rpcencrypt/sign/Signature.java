package com.zhangjiashuai.rpcencrypt.sign;

import com.zhangjiashuai.rpcencrypt.common.Algorithm;
import com.zhangjiashuai.rpcencrypt.common.Mode;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.RequestPayload;

/**
 * 签名接口
 * 非对称加密少量数据，拼接报文摘要
 */
public interface Signature extends Algorithm {

    String SIGN_SEPARATOR = ".";

    default String getStr2Sign(RequestPayload requestPayload) {
        ClientInfo clientInfo = requestPayload.getClientInfo();
        return clientInfo.getClientId() + clientInfo.getClientSecret();
    }

    boolean serverValidate(RequestPayload requestPayload);

    String clientSign(RequestPayload requestPayload);

    /**
     * 生成随机密钥
     * @return [公钥,私钥]
     */
    String[] generateKeyPair();
}
