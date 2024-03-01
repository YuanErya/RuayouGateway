package com.ruayou.common.exception;

import com.ruayou.common.enums.ResponseCode;

/**
 * @Author：ruayou
 * @Date：2024/3/1 22:10
 * @Filename：ResponseException
 */
public class ResponseException extends GatewayException{
    private static final long serialVersionUID = -5658783582509025259L;

    public ResponseException() {
        this(ResponseCode.INTERNAL_ERROR);
    }

    public ResponseException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public ResponseException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.code = code;
    }
}
