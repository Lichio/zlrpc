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
 * ��������ӿ�
 */
public interface IRegister4Governance {

	/**
	 * ��ȡ�����ṩ���б�������������б�
	 *
	 * @param serviceName
	 * @param appKey
	 * @return
	 */
	public Pair<List<ProviderInfo>, List<ConsumerInfo>> queryProvidersAndInvokers(String serviceName, String appKey);

}
