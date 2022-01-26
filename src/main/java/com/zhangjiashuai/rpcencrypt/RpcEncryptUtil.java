package com.zhangjiashuai.rpcencrypt;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.collection.ListUtil;
import com.zhangjiashuai.rpcencrypt.cipher.Cipher;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;
import com.zhangjiashuai.rpcencrypt.sign.Signature;
import com.zhangjiashuai.rpcencrypt.sign.SignatureMismatchException;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.List;

/**
 * 静态访问工具
 */
public class RpcEncryptUtil {

    private static final RpcEncrypt DEFAULT_RPC_ENCRYPT = RpcEncrypt.builder().build();
    private static RpcEncrypt RPC_ENCRYPT = DEFAULT_RPC_ENCRYPT;

    private RpcEncryptUtil() {
    }

    /**
     * 签名或验签
     * 附带加密（客户端），除非设置requestPayload::encryptBeforeSign为false
     * 附带解密（服务端），除非设置requestPayload::decryptAfterValidate为false
     * @param requestPayload
     * @throws SignatureMismatchException 服务端模式下签名不匹配
     * @return
     */
    public static StatefulRequestPayload work(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        return RPC_ENCRYPT.work(requestPayload);
    }

    /**
     * 服务端验签
     * 附带解密，除非设置requestPayload::decryptAfterValidate为false
     * @param requestPayload
     * @throws SignatureMismatchException 签名不匹配
     * @return
     */
    public static StatefulRequestPayload serverValidate(StatefulRequestPayload requestPayload) throws SignatureMismatchException {
        return RPC_ENCRYPT.serverValidate(requestPayload);
    }

    /**
     * 客户端签名
     * 附带加密，除非设置requestPayload::encryptBeforeSign为false
     * @param requestPayload
     * @return
     */
    public static StatefulRequestPayload clientSign(StatefulRequestPayload requestPayload) {
        return RPC_ENCRYPT.clientSign(requestPayload);
    }

    /**
     * 生成随机密钥对
     * 用于非对称加密
     * @return [私钥, 公钥]
     */
    public static List<String> generateKeyPair() {
        Signature signature = RPC_ENCRYPT.getSignature();
        KeyPair keyPair = signature.generateKeyPair();
        return ListUtil.of(Base64Encoder.encode(keyPair.getPrivate().getEncoded()), Base64Encoder.encode(keyPair.getPublic().getEncoded()));
    }

    /**
     * 生成一个随机密钥
     * 用于对称加密
     * @return
     */
    public static String generateKey() {
        Signature signature = RPC_ENCRYPT.getSignature();
        Cipher cipher = signature.getCipher();
        SecretKey secretKey = cipher.generateKey();
        return Base64Encoder.encode(secretKey.getEncoded());
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
