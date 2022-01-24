package com.zhangjiashuai.rpcencrypt;

import com.zhangjiashuai.rpcencrypt.common.Mode;
import com.zhangjiashuai.rpcencrypt.entity.RequestPayload;
import com.zhangjiashuai.rpcencrypt.sign.RSASignature;
import com.zhangjiashuai.rpcencrypt.sign.Signature;

/**
 * 程序入口
 */
public class RpcEncrypt {

    private Signature signature;

    public RpcEncrypt() {
        this(new RSASignature());
    }

    public RpcEncrypt(Signature signature) {
        this.signature = signature;
    }

    /**
     * 签名或验签
     * 附带加密（客户端），除非设置requestPayload::encryptBeforeSign为false
     * 附带解密（服务端），除非设置requestPayload::decryptAfterValidate为false
     * @param requestPayload
     * @return
     */
    RequestPayload work(RequestPayload requestPayload) {
        if (requestPayload.getMode() == Mode.CLIENT) {
            return clientSign(requestPayload);
        }
        return serverValidate(requestPayload);
    }

    /**
     * 服务端验签
     * 附带解密，除非设置requestPayload::decryptAfterValidate为false
     * @param requestPayload
     * @return
     */
    public RequestPayload serverValidate(RequestPayload requestPayload) {
        signature.serverValidate(requestPayload);
        return requestPayload;
    }

    /**
     * 客户端签名
     * 附带加密，除非设置requestPayload::encryptBeforeSign为false
     * @param requestPayload
     * @return
     */
    public RequestPayload clientSign(RequestPayload requestPayload) {
        signature.clientSign(requestPayload);
        return requestPayload;
    }
}
