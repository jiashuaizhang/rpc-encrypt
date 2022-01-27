package com.zhangjiashuai.rpcencrypt.sign;

import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;
import com.zhangjiashuai.rpcencrypt.sign.asymmetric.Asymmetric;
import com.zhangjiashuai.rpcencrypt.sign.digest.Digest;
import com.zhangjiashuai.rpcencrypt.symmetric.Symmetric;

/**
 * 签名接口
 * 非对称加密少量数据，拼接报文摘要
 */
public interface Signature {

    String SIGN_SEPARATOR = ".";

    boolean serverValidate(StatefulRequestPayload requestPayload) throws SignatureMismatchException;

    String clientSign(StatefulRequestPayload requestPayload);

    void setDigest(Digest digest);

    Digest getDigest();

    Symmetric getSymmetricCipher();

    void setSymmetricCipher(Symmetric symmetricCipher);

    Asymmetric getAsymmetricCipher();

    void setAsymmetricCipher(Asymmetric asymmetricCipher);

}
