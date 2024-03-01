package com.ruayou.common.entity;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Data
public class ServiceDefinition implements Serializable {

	@Serial
	private static final long serialVersionUID = -8263365765897285189L;

	/**
	 * 	服务唯一id
	 */
	private String serviceId;

	/**
	 * 	服务的具体协议：http(mvc http) dubbo ..
	 */
	private String protocol;

	/**
	 * 	路径匹配规则：访问真实ANT表达式：定义具体的服务路径的匹配规则
	 */
	private List<String> patternPath;

	/**
	 * 	环境名称
	 */
	private String env;

	/**
	 * 	服务启用禁用
	 */
	private boolean enable = true;

	public ServiceDefinition() {
		super();
	}

	public ServiceDefinition( String serviceId,  String protocol, String[] patternPath,
							 String env, boolean enable) {
		super();
		this.serviceId = serviceId;
		this.protocol = protocol;
		this.patternPath = Arrays.asList(patternPath);
		this.env = env;
		this.enable = enable;
	}
}
