package com.ruayou.common.exception;

import com.ruayou.common.enums.ResponseCode;

/**
 * @Author：ruayou
 * @Date：2024/3/1 21:58
 * @Filename：GatewayException
 * 网关异常的根异常
 */
public class GatewayException extends RuntimeException{
    private static final long serialVersionUID = -2858789202563437156L;
    protected ResponseCode code;
    public GatewayException() {
    }
    public GatewayException(ResponseCode code) {
        super(code.getMessage());
        this.code=code;
    }
    public GatewayException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }
    public GatewayException(String message, Throwable cause, ResponseCode code) {
        super(message, cause);
        this.code = code;
    }

    public GatewayException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public GatewayException(String message, Throwable cause,
                         boolean enableSuppression, boolean writableStackTrace, ResponseCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }
}
