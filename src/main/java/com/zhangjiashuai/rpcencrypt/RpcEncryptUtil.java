package com.zhangjiashuai.rpcencrypt;

import com.zhangjiashuai.rpcencrypt.entity.RequestPayload;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;
import com.zhangjiashuai.rpcencrypt.sign.SignatureMismatchException;

/**
 * 静态访问工具
 */
public class RpcEncryptUtil {

    private static final RpcEncrypt DEFAULT_RPC_ENCRYPT = RpcEncrypt.builder().build();
    private static RpcEncrypt RPC_ENCRYPT = DEFAULT_RPC_ENCRYPT;

    private RpcEncryptUtil() {
    }

    public static RequestPayload work(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        return RPC_ENCRYPT.work(requestPayload);
    }

    public static RequestPayload serverValidate(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        return RPC_ENCRYPT.serverValidate(requestPayload);
    }

    public RequestPayload clientSign(StatefulRequestPayload requestPayload) {
        return RPC_ENCRYPT.clientSign(requestPayload);
    }

    public static RpcEncrypt getDefaultRpcEncrypt() {
        return DEFAULT_RPC_ENCRYPT;
    }

    public static RpcEncrypt getRpcEncrypt() {
        return RPC_ENCRYPT;
    }

    public static void setRpcEncrypt(RpcEncrypt rpcEncrypt) {
        RPC_ENCRYPT = rpcEncrypt;
    }
}
