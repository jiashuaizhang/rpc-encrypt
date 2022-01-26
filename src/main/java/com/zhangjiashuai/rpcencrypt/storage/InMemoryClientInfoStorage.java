package com.zhangjiashuai.rpcencrypt.storage;

import cn.hutool.core.collection.CollUtil;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 客户端信息内存存储实现
 */
public class InMemoryClientInfoStorage implements ClientInfoStorage {

    private volatile Map<String, ClientInfo> clientIdKeyCache;

    public InMemoryClientInfoStorage() {
        init();
    }

    @Override
    public ClientInfo findByClientId(String clientId) {
        return clientIdKeyCache.get(clientId);
    }

    @Override
    public void init(Collection<ClientInfo> collection) {
        if (CollUtil.isNotEmpty(collection)) {
            clientIdKeyCache = collection.stream().collect(Collectors.toMap(ClientInfo::getClientId, clientInfo -> clientInfo,
                    (k, v) -> v, ConcurrentHashMap::new));
        } else {
            clientIdKeyCache = Collections.emptyMap();
        }
    }
}
