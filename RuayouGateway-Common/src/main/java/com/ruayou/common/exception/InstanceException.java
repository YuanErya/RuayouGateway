package com.ruayou.common.exception;

import com.ruayou.common.enums.ResponseCode;

/**
 * @Author：ruayou
 * @Date：2024/3/1 22:22
 * @Filename：InstanceException
 */
public class InstanceException extends GatewayException{
    private static final long serialVersionUID = -3534707267339256891L;

    public InstanceException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public InstanceException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }
}
