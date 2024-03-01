package com.ruayou.common.exception;

import com.ruayou.common.enums.ResponseCode;
import lombok.Getter;

/**
 * @Author：ruayou
 * @Date：2024/3/1 22:05
 * @Filename：ConnectException
 */
public class ConnectException extends GatewayException{
    private static final long serialVersionUID = -8503233707913239958L;
    @Getter
    private final String uniqueId;

    @Getter
    private final String requestUrl;

    public ConnectException(String uniqueId, String requestUrl) {
        this.uniqueId = uniqueId;
        this.requestUrl = requestUrl;
    }

    public ConnectException(Throwable cause, String uniqueId, String requestUrl, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.uniqueId = uniqueId;
        this.requestUrl = requestUrl;
    }
}
