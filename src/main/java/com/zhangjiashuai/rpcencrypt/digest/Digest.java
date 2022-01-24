package com.zhangjiashuai.rpcencrypt.digest;

import cn.hutool.core.util.StrUtil;
import com.zhangjiashuai.rpcencrypt.common.Algorithm;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.RequestPayload;

/**
 * 摘要接口
 */
public interface Digest extends Algorithm {

    default String digestPayload(RequestPayload requestPayload) {
        return digestPayload(StrUtil.emptyIfNull(requestPayload.getPayload()), requestPayload.getClientInfo());
    }

    String digestPayload(String payload, ClientInfo clientInfo);
}
