package com.zlframework.rpc.registry.zookeeper;

import com.zlframework.rpc.registry.zookeeper.model.ConsumerInfo;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * zlrpc com.zlframework.rpc.registry.zookeeper
 *
 * @author Lichaojie
 * @version 2018/4/26 17:55
 *
 * 服务治理接口
 */
public interface IRegister4Governance {

	/**
	 * 获取服务提供者列表与服务消费者列表
	 *
	 * @param serviceName
	 * @param appKey
	 * @return
	 */
	public Pair<List<ProviderInfo>, List<ConsumerInfo>> queryProvidersAndInvokers(String serviceName, String appKey);

}
