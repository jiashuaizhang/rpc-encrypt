package com.zhangjiashuai.rpcencrypt.common;

import java.util.concurrent.TimeUnit;

public class Constants {

    private Constants() {
    }

    public static final int DEFAULT_CACHE_CAPACITY = 10000;
    public static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.DAYS.toMillis(7);
}
