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
 * ���Ѷ�ע�����Ľӿ�
 */
public interface IRegister4Consumer {

	/**
	 * ���Ѷ˳�ʼ�������ṩ����Ϣ���ػ���
	 *
	 * @param appKey
	 * @param group
	 */
	public void initProviderMap(String appKey, String group);


	/**
	 * ���Ѷ˻�ȡ�����ṩ����Ϣ
	 *
	 * @return
	 */
	public Map<String, List<ProviderInfo>> getServiceMetaDataMap4Consume();


	/**
	 * ���Ѷ˽���������Ϣע�ᵽzk��Ӧ�Ľڵ���
	 *
	 * @param consumer
	 */
	public void registerConsumer(final ConsumerInfo consumer);
}
