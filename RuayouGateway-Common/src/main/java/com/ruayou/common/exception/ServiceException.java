package com.ruayou.common.exception;

import com.ruayou.common.enums.ResponseCode;

/**
 * @Author：ruayou
 * @Date：2024/3/1 22:21
 * @Filename：ServiceException
 */
public class ServiceException extends GatewayException{
    private static final long serialVersionUID = -3534901653739256982L;

    public ServiceException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public ServiceException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }
}
