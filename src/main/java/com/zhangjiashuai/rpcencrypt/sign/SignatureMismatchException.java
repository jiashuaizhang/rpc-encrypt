package com.zhangjiashuai.rpcencrypt.sign;

public class SignatureMismatchException extends Exception {

    public SignatureMismatchException() {
    }

    public SignatureMismatchException(String message) {
        super(message);
    }

    public SignatureMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureMismatchException(Throwable cause) {
        super(cause);
    }

    public SignatureMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
