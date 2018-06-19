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
 * �����ע�����Ľӿ�
 */
public interface IRegister4Provider {

	/**
	 * ����˽������ṩ����Ϣע�ᵽzk��Ӧ�Ľڵ���
	 *
	 * @param serviceMetaData
	 */
	public void registerProvider(final List<ProviderInfo> serviceMetaData);


	/**
	 * ����˻�ȡ�����ṩ����Ϣ
	 * <p/>
	 * ע:���ض���,Key:�����ṩ�߽ӿ�  value:�����ṩ�߷��񷽷��б�
	 *
	 * @return
	 */
	public Map<String, List<ProviderInfo>> getProviderServiceMap();
}
