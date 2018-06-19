package com.zlframework.rpc.registry.zookeeper;

import com.zlframework.rpc.registry.zookeeper.model.ConsumerInfo;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;

import java.util.List;
import java.util.Map;

/**
 * zlrpc com.zlframework.rpc.registry.zookeeper
 *
 * @author Lichaojie
 * @version 2018/4/26 18:09
 *
 * 消费端注册中心接口
 */
public interface IRegister4Consumer {

	/**
	 * 消费端初始化服务提供者信息本地缓存
	 *
	 * @param appKey
	 * @param group
	 */
	public void initProviderMap(String appKey, String group);


	/**
	 * 消费端获取服务提供者信息
	 *
	 * @return
	 */
	public Map<String, List<ProviderInfo>> getServiceMetaDataMap4Consume();


	/**
	 * 消费端将消费者信息注册到zk对应的节点下
	 *
	 * @param consumer
	 */
	public void registerConsumer(final ConsumerInfo consumer);
}
