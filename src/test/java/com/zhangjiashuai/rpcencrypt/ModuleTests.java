package com.zhangjiashuai.rpcencrypt;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.zhangjiashuai.rpcencrypt.common.Mode;
import com.zhangjiashuai.rpcencrypt.entity.ClientInfo;
import com.zhangjiashuai.rpcencrypt.entity.StatefulRequestPayload;
import com.zhangjiashuai.rpcencrypt.sign.DefaultSignature;
import com.zhangjiashuai.rpcencrypt.sign.Signature;
import com.zhangjiashuai.rpcencrypt.sign.SignatureMismatchException;
import com.zhangjiashuai.rpcencrypt.storage.ClientInfoStorage;
import com.zhangjiashuai.rpcencrypt.storage.InMemoryClientInfoStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModuleTests {

    private static Signature signature;
    private static StatefulRequestPayload requestPayload;

    @BeforeAll
    static void init() throws IOException {
        signature = new DefaultSignature();
        requestPayload = new StatefulRequestPayload();
        ClientInfo clientInfo = new ClientInfo();
        requestPayload.setClientInfo(clientInfo);
        requestPayload.setPayload("[MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIT/auDnUhe8G/rq5E8EdgNg0rPzd9+f7A+SQctc9YFUHdT7x8Xd1bZgr3WZevgzFglM2X6yxkzeEUscguD0A0zXTzLFcCWveHIEOLi+HTyz+u35kT9j3RY6ZcHoPL+iX/2vU2cDO7fsjUIXrTIaywpqFu9K6+8Mn5f+1chgejuDAgMBAAECgYBWbqOn2803DZHlhIexboW/dkoYlo597zF7gSJvJk+Kp/7nLmXLCnrcFoOQ2pjW+mRE0QO72jUTOXJlrPbFeO83Lcqc/M8SYMkgmgCCsQM+HI5r/qk8E34O6dtRu13n9g9uuFsIcZbLx+IX6qG0XuOKOlA4BAEsjbR3jGql2/edQQJBAMHO5bMXCqwl+03+2oVXCkXNNhC6jf7vsL1AzwDrjCbiF56tS3A9gNBVZZrnBo+0ey/fBp9IrG/qcBhvrYxI7WMCQQCvrQQD/P20sxzgL4/GBOMuWSLWsnTNa/2SMTgVB8p7fUr9xVceOPYTqnGVXbFa5StQJ5T6CJvMjWflbIdOOWNhAkAdnFvR8fpKdP8hWofOiY7jPUg+ZBJf2gU51RYLgPGH21FaiAWXn3331qRQd220NRIBLWUYnwThkIMR6LYuUdIbAkB0XPP1+FPMp3+O97ISBha9EonDEH3Ru6BAf52YQIrcdUeBBIAKIszMhe+qcl8RyA6Cj1VcsOsR+PBCxTpylAzBAkBKfCIS4eJcfWisk7ybOpdRhuoWYIte1w2Iu3O9yp1xRXweFSc24Jl2yU3L1pPbpG2YdezlfopWX9xS6+3jG45b, MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCE/2rg51IXvBv66uRPBHYDYNKz83ffn+wPkkHLXPWBVB3U+8fF3dW2YK91mXr4MxYJTNl+ssZM3hFLHILg9ANM108yxXAlr3hyBDi4vh08s/rt+ZE/Y90WOmXB6Dy/ol/9r1NnAzu37I1CF60yGssKahbvSuvvDJ+X/tXIYHo7gwIDAQAB]");
        clientInfo.setClientId("test");
        clientInfo.setClientSecret("123456嘤嘤嘤");
        clientInfo.setPrivateKeyServer("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJEa7hpQUDTn6r+keBJbG03eoymAWYTdjZID/ylfT3R5sQ00851QcADo/a3P0gLP+01eKZceIfAVQpkI4kTvm7yJ9lA3eiZn0MN4QOtX26Wi46PodZa6JqqcYfrMfgdbW9wLOcRoELyepiQv4hCNkSWSlqDHtQw73iI1fYTirCI3AgMBAAECgYBYbBCr+ETn+GsUXSTOCraYvRKwN4ZVcKzUTZsvmuTQFVfOtHW/Z0TZSSFMyVcwX4zDmJ2/eJ01r77lgoRffaNoVbJjy+bpFJXy1Rfl5D+bn/NOJEQgU5ZFUgNH4s9nFcrmgXdFNKWAAMU+SmwJuVirrL8LDsNXxDFai1ejW7bgAQJBAMV//94Kl5foI52aH5O5OjRhFymuXkK83gadGh5zGGbvVp9c2xh3SWV4+5Q3iUlLu+qnTn7HfbfHhI1agIQ/FXUCQQC8FfIAV2xhJUt5wyLD5OHLIdgU0KCFAxNNGTl2GTqdIQ7HKMQbORzS/SomD7uKNFraH1+8HiFW5tQ9+A0QuCd7AkB0GoZRgwqRK8/cM6HClw5ngng6cZ5KP3uKicq0Addjk+npb44BeQa114SWInvbkeEb1lUn5KC8bFc5Pe31UJyNAkA46RL+k3VRt60svKSqTXkcs7LkKXRIivOuInHGkD+yB6ynCmi8K/3SwZOdTKlcWhcbZN7c1ukTmGikD4g0hOQ9AkEApBDQleoKEi+zdzOJH75WpY3nZ3xe211qrmd2/OcXxqd4Qw2BWFuzbUPzCCFwEcgMOKeylhKRCVBWoNh+YAqZ+A==");
        clientInfo.setPublicKeyServer("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCRGu4aUFA05+q/pHgSWxtN3qMpgFmE3Y2SA/8pX090ebENNPOdUHAA6P2tz9ICz/tNXimXHiHwFUKZCOJE75u8ifZQN3omZ9DDeEDrV9ulouOj6HWWuiaqnGH6zH4HW1vcCznEaBC8nqYkL+IQjZElkpagx7UMO94iNX2E4qwiNwIDAQAB");
//        loadKeyPairFromClasspath(clientInfo);
    }

    private static void loadKeyPairFromClasspath(ClientInfo clientInfo) throws IOException {
        try (InputStream is1 = ModuleTests.class.getResourceAsStream("/app_private_key_ts.pem");
        InputStream is2 = ModuleTests.class.getResourceAsStream("/app_public_key.pem");) {
            List<String> list1 = IoUtil.readUtf8Lines(is1, new ArrayList<>());
            List<String> list2 = IoUtil.readUtf8Lines(is2, new ArrayList<>());
            Predicate<String> predicate = line -> !line.startsWith("--");
            String privateKey = list1.stream().filter(predicate).map(line -> line.replaceAll("\n", "").replaceAll(" ", "")).collect(Collectors.joining());
            String publicKey = list2.stream().filter(predicate).map(line -> line.replaceAll("\n", "").replaceAll(" ", "")).collect(Collectors.joining());
            clientInfo.setPrivateKeyServer(privateKey);
            clientInfo.setPublicKeyServer(publicKey);
        }
    }

    @Test
    public void generateKeyPairTest() {
        List<String> keyPair = RpcEncryptUtil.generateKeyPair();
        Assert.notNull(keyPair);
        Assert.isTrue(keyPair.size() == 2);
        Assert.isTrue(StrUtil.isNotEmpty(keyPair.get(0)) && StrUtil.isNotEmpty(keyPair.get(1)));
        System.out.println("private key:");
        System.out.println(keyPair.get(0));
        System.out.println("public key:");
        System.out.println(keyPair.get(1));
    }

    @Test
    public void generateKeyTest() {
        String key = RpcEncryptUtil.generateKey();
        Assert.notNull(key);
        Assert.isTrue(StrUtil.isNotEmpty(key));
        System.out.println(key);
    }

    @Test
    public void clientSignTest() {
        requestPayload.setMode(Mode.CLIENT);
        String sign = signature.clientSign(requestPayload);
        Assert.notNull(sign);
//        System.out.println(sign);
        System.out.println(requestPayload);
    }

    @Test
    public void serverValidateTest() {
        signature.clientSign(requestPayload);
        System.out.println(requestPayload);
        System.out.println();
        try {
            boolean validate = signature.serverValidate(requestPayload);
            Assert.isTrue(validate);
            System.out.println(requestPayload);
        } catch (SignatureMismatchException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rpcEncryptUtilTest() {
        // init
        ClientInfoStorage clientInfoStorage = new InMemoryClientInfoStorage();
        clientInfoStorage.init(Collections.singletonList(requestPayload.getClientInfo()));
        RpcEncrypt rpcEncrypt = RpcEncrypt.builder().clientInfoStorage(clientInfoStorage).build();
        RpcEncryptUtil.setRpcEncrypt(rpcEncrypt);
        // run
        try {
            StatefulRequestPayload requestPayload = RpcEncryptUtil.work(ModuleTests.requestPayload);
            System.out.println(requestPayload);
            System.out.println();

            requestPayload.setMode(Mode.SERVER);
            requestPayload = RpcEncryptUtil.work(ModuleTests.requestPayload);
            System.out.println(requestPayload);

            Assert.notNull(requestPayload);
        } catch (SignatureMismatchException e) {
            e.printStackTrace();
        }
    }
}
