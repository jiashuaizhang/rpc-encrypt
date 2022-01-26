package com.zhangjiashuai.rpcencrypt.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface Algorithm {

    Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * 获取算法名称
     * @return
     */
    String getAlgorithm();
}
