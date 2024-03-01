package com.ruayou.common.exception;

import com.ruayou.common.enums.ResponseCode;

/**
 * @Author：ruayou
 * @Date：2024/3/1 22:20
 * @Filename：LimitedException
 */
public class LimitedException extends GatewayException{
    private static final long serialVersionUID = -5534707291739256295L;

    public LimitedException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public LimitedException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }
}
