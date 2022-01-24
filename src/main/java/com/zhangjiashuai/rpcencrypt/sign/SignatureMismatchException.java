package com.zhangjiashuai.rpcencrypt.sign;

public class SignatureMismatchException extends RuntimeException {

    private String serverSign;

    public SignatureMismatchException() {
    }

    public SignatureMismatchException(String serverSign) {
        this(serverSign, null);
    }

    public SignatureMismatchException(String serverSign, Throwable cause) {
        super(serverSign, cause);
        this.serverSign = serverSign;
    }

    public SignatureMismatchException(Throwable cause) {
        super(cause);
    }

    public String getServerSign() {
        return serverSign;
    }

    public void setServerSign(String serverSign) {
        this.serverSign = serverSign;
    }
}
