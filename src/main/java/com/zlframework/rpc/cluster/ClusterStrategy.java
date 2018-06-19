package com.zlframework.rpc.cluster;

import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;

import java.util.List;

/**
 * zlrpc com.zlframework.rpc.cluster
 *
 * @author Lichaojie
 * @version 2018/4/28 16:46
 */
public interface ClusterStrategy {

	/**
	 * ∏∫‘ÿ≤ﬂ¬‘À„∑®
	 *
	 * @param providerServices
	 * @return
	 */
	public ProviderInfo select(List<ProviderInfo> providerServices);
}
