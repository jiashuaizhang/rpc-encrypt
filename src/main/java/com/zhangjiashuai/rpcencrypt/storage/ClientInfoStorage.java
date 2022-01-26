package com.zhangjiashuai.rpcencrypt.storage;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;

import java.util.Collection;
import java.util.Collections;

/**
 * 客户端信息存储接口
 */
public interface ClientInfoStorage {

    ClientInfo findByClientId(String clientId);

    default ClientInfo find(ClientInfo clientInfo) {
        if (clientInfo == null || StrUtil.isEmpty(clientInfo.getClientId())) {
            return null;
        }
        ClientInfo got = findByClientId(clientInfo.getClientId());
        if (got == null) {
            return null;
        }
        BeanUtil.copyProperties(got, clientInfo, CopyOptions.create().ignoreNullValue());
        return clientInfo;
    }

    /**
     * 初始化
     */
    default void init() {
        init(Collections.emptyList());
    }

    /**
     * 通过传入的集合初始化
     * @param collection
     */
    void init(Collection<ClientInfo> collection);
}
