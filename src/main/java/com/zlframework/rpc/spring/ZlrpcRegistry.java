package com.zlframework.rpc.spring;

import com.zlframework.rpc.registry.zookeeper.config.ZkConfigHelper;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * zlrpc com.zlframework.rpc.spring
 *
 * @author Lichaojie
 * @version 2018/4/24 19:38
 */
public class ZlrpcRegistry implements ApplicationListener {

	private String protocol;
	private String address;



	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		ZkConfigHelper.setZkAddress(address);
	}
}
