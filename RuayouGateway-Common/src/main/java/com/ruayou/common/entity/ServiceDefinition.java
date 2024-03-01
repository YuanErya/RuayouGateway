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
	 * 	唯一的服务ID: serviceId:version
	 */
	private String uniqueId;

	/**
	 * 	服务唯一id
	 */
	private String serviceId;

	/**
	 * 	服务的版本号
	 */
	private String version = "1.0.0";

	/**
	 * 	服务的具体协议：http(mvc http) dubbo ..
	 */
	private String protocol;

	/**
	 * 	路径匹配规则：访问真实ANT表达式：定义具体的服务路径的匹配规则
	 */
	private List<String> patternPath;

	/**
	 * 	分组名称
	 */
	private String group;

	/**
	 * 	服务启用禁用
	 */
	private boolean enable = true;

	public ServiceDefinition() {
		super();
	}

	public ServiceDefinition(String uniqueId, String serviceId, String version, String protocol, String[] patternPath,
							 String group, boolean enable) {
		super();
		this.uniqueId = uniqueId;
		this.serviceId = serviceId;
		this.version = version;
		this.protocol = protocol;
		this.patternPath = Arrays.asList(patternPath);
		this.group = group;
		this.enable = enable;
	}
}
