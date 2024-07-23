package com.ruayou.common.exception;

import com.ruayou.common.enums.ResponseCode;

/**
 * @Author：ruayou
 * @Date：2024/7/13 18:24
 * @Filename：AsyncResponseException
 */
public class AsyncResponseException extends GatewayException{

    private static final long serialVersionUID = -8503233707913239791L;
    public AsyncResponseException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public AsyncResponseException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }
}
