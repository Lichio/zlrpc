package com.zlframework.rpc.cluster;

import com.zlframework.rpc.cluster.impl.RandomStrategyImpl;

/**
 * zlrpc com.zlframework.rpc.cluster
 *
 * @author Lichaojie
 * @version 2018/4/28 16:49
 */
public class ClusterStrategyFrame {

	public static ClusterStrategy select(String cluster){
		ClusterStrategyEnum clusterStrategyEnum = ClusterStrategyEnum.query(cluster);
		if(clusterStrategyEnum == null){
			return new RandomStrategyImpl();
		}
		switch (clusterStrategyEnum){
			case Random:
				default:
					return new RandomStrategyImpl();
		}
	}
}
