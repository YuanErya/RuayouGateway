package com.ruayou.common.entity;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * 一个服务定义会对应多个服务实例
 */
@Data
public class ServiceInstance implements Serializable {

	@Serial
	private static final long serialVersionUID = -7559569289189228478L;

	/**
	 * 	服务实例ID: ip:port
	 */
	protected String serviceInstanceId;
	
	/**
	 * 	服务定义唯一id： uniqueId
	 */
	protected String uniqueId;

	/**
	 * 	服务实例地址： ip:port
	 */
	protected String ip;

	protected int port;
	
	/**
	 * 	标签信息
	 */
	protected String tags;
	
	/**
	 * 	权重信息
	 */
	protected int weight;
	
	/**
	 * 	服务注册的时间戳：后面我们做负载均衡，warmup预热
	 */
	protected long registerTime;
	
	/**
	 * 	服务实例启用禁用
	 */
	protected boolean enable = true;
	
	/**
	 * 	服务实例对应的版本号
	 */
	protected String version;

	/**
	 * 服务实例是否是灰度的
	 */
	protected boolean gray;
	public ServiceInstance() {
		super();
	}

	
}
