package com.ruayou.common.exception;

import com.ruayou.common.enums.ResponseCode;

/**
 * @Author：ruayou
 * @Date：2024/3/1 22:12
 * @Filename：ServiceNotFoundException
 * 服务没有找到注册
 */
public class ServiceNotFoundException extends GatewayException {
    private static final long serialVersionUID = -2619825534739273161L;

    public ServiceNotFoundException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public ServiceNotFoundException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }


}
