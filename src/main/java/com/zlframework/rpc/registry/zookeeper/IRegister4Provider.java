package com.zlframework.rpc.registry.zookeeper;

import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;

import java.util.List;
import java.util.Map;

/**
 * zlrpc com.zlframework.rpc.registry.zookeeper
 *
 * @author Lichaojie
 * @version 2018/4/26 18:12
 *
 * 服务端注册中心接口
 */
public interface IRegister4Provider {

	/**
	 * 服务端将服务提供者信息注册到zk对应的节点下
	 *
	 * @param serviceMetaData
	 */
	public void registerProvider(final List<ProviderInfo> serviceMetaData);


	/**
	 * 服务端获取服务提供者信息
	 * <p/>
	 * 注:返回对象,Key:服务提供者接口  value:服务提供者服务方法列表
	 *
	 * @return
	 */
	public Map<String, List<ProviderInfo>> getProviderServiceMap();
}
