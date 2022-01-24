package com.zhangjiashuai.rpcencrypt;

import com.zhangjiashuai.rpcencrypt.entity.RequestPayload;

/**
 * 静态访问工具
 */
public class RpcEncryptUtil {

    private static final RpcEncrypt RPC_ENCRYPT = new RpcEncrypt();

    private RpcEncryptUtil() {
    }

    public static RequestPayload work(RequestPayload requestPayload) {
        return RPC_ENCRYPT.work(requestPayload);
    }

    public static RequestPayload serverValidate(RequestPayload requestPayload) {
        return RPC_ENCRYPT.serverValidate(requestPayload);
    }

    public RequestPayload clientSign(RequestPayload requestPayload) {
        return RPC_ENCRYPT.clientSign(requestPayload);
    }

}
