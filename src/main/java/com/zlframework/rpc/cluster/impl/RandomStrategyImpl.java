package com.zlframework.rpc.cluster.impl;

import com.zlframework.rpc.cluster.ClusterStrategy;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * zlrpc com.zlframework.rpc.cluster.impl
 *
 * @author Lichaojie
 * @version 2018/4/28 16:52
 */
public class RandomStrategyImpl implements ClusterStrategy {

	@Override
	public ProviderInfo select(List<ProviderInfo> providerServices) {
		int MAX_LEN = providerServices.size();
		int index = RandomUtils.nextInt(0, MAX_LEN - 1);
		return providerServices.get(index);
	}
}
