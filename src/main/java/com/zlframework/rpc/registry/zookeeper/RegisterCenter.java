package com.zlframework.rpc.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zlframework.rpc.registry.zookeeper.config.ZkConfigHelper;
import com.zlframework.rpc.registry.zookeeper.model.ConsumerInfo;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;
import com.zlframework.rpc.utils.IpUtils;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;


import java.util.List;
import java.util.Map;

/**
 * zlrpc com.zlframework.rpc.registry.zookeeper
 *
 * @author Lichaojie
 * @version 2018/4/26 18:13
 */
public class RegisterCenter implements IRegister4Consumer,IRegister4Provider,IRegister4Governance {

	private static volatile RegisterCenter instance;

	//服务提供者列表,Key:服务提供者接口  value:服务提供者服务方法列表
	private static final Map<String, List<ProviderInfo>> providerServiceMap = Maps.newConcurrentMap();
	//服务端ZK服务元信息,选择服务(第一次直接从ZK拉取,后续由ZK的监听机制主动更新)
	private static final Map<String, List<ProviderInfo>> serviceMetaDataMap4Consume = com.google.common.collect.Maps.newConcurrentMap();

	private static String ZK_SERVICE = ZkConfigHelper.getZkAddress();
	private static int ZK_SESSION_TIME_OUT = ZkConfigHelper.getZkConnectionTimeout();
	private static int ZK_CONNECTION_TIME_OUT = ZkConfigHelper.getZkConnectionTimeout();
	private static String ROOT_PATH = "/config_register";
	public static String PROVIDER_TYPE = "provider";
	public static String CONSUMER_TYPE = "consumer";
	private static volatile ZkClient zkClient = null;


	private RegisterCenter(){

	}

	public static RegisterCenter getInstance(){
		if(instance == null){
			synchronized (RegisterCenter.class){
				if(instance == null){
					instance = new RegisterCenter();
				}
			}
		}

		return instance;
	}

	@Override
	public void registerProvider(List<ProviderInfo> serviceMetaData) {
		if (CollectionUtils.isEmpty(serviceMetaData)) {
			return;
		}

		//连接zk,注册服务
		synchronized (RegisterCenter.class) {
			for (ProviderInfo provider : serviceMetaData) {
				String service = provider.getService();

				List<ProviderInfo> providers = providerServiceMap.get(service);
				if (providers == null) {
					providers = Lists.newArrayList();
				}
				providers.add(provider);
				providerServiceMap.put(service, providers);
			}

			if (zkClient == null) {
				zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
			}

			//创建 ZK命名空间/当前部署应用APP命名空间/
			String appKey = ZkConfigHelper.APP_KEY;
			String zkPath = ROOT_PATH + "/" + appKey;
			boolean exist = zkClient.exists(zkPath);
			if (!exist) {
				zkClient.createPersistent(zkPath, true);
			}

			for (Map.Entry<String, List<ProviderInfo>> entry : providerServiceMap.entrySet()) {
				//服务分组
				String groupName = ZkConfigHelper.GROUP;
				//创建服务提供者
				String serviceNode = entry.getKey();
				String servicePath = zkPath + "/" + groupName + "/" + serviceNode + "/" + PROVIDER_TYPE;
				exist = zkClient.exists(servicePath);
				if (!exist) {
					zkClient.createPersistent(servicePath, true);
				}

				//创建当前服务器节点
				int serverPort = entry.getValue().get(0).getPort();//服务端口
//				int weight = 1;//entry.getValue().get(0).getWeight();//服务权重
//				int workerThreads = 15;//entry.getValue().get(0).getWorkerThreads();//服务工作线程
				String localIp = IpUtils.localIp();
				String currentServiceIpNode = servicePath + "/" + localIp + "|" + serverPort;
				exist = zkClient.exists(currentServiceIpNode);
				if (!exist) {
					//注意,这里创建的是临时节点
					zkClient.createEphemeral(currentServiceIpNode);
				}


				//监听注册服务的变化,同时更新数据到本地缓存
				zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
					@Override
					public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
						if (currentChilds == null) {
							currentChilds = Lists.newArrayList();
						}

						//存活的服务IP列表
						List<String> activityServiceIpList = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
							@Override
							public String apply(String input) {
								return StringUtils.split(input, "|")[0];
							}
						}));
						refreshActivityService(activityServiceIpList);
					}
				});

			}
		}
	}

	@Override
	public Map<String, List<ProviderInfo>> getProviderServiceMap() {
		return providerServiceMap;
	}

	@Override
	public void initProviderMap(String appkey, String group) {
		if (MapUtils.isEmpty(serviceMetaDataMap4Consume)) {
			serviceMetaDataMap4Consume.putAll(fetchOrUpdateServiceMetaData(appkey, group));
		}
	}

	@Override
	public Map<String, List<ProviderInfo>> getServiceMetaDataMap4Consume() {
		return serviceMetaDataMap4Consume;
	}

	@Override
	public void registerConsumer(ConsumerInfo consumer) {
		if (consumer == null) {
			return;
		}

		//连接zk,注册服务
		synchronized (RegisterCenter.class) {

			if (zkClient == null) {
				zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
			}
			//创建 ZK命名空间/当前部署应用APP命名空间/
			boolean exist = zkClient.exists(ROOT_PATH);
			if (!exist) {
				zkClient.createPersistent(ROOT_PATH, true);
			}


			//创建服务消费者节点
			String remoteAppKey = ZkConfigHelper.APP_KEY;
			String groupName = ZkConfigHelper.GROUP;
			String serviceNode = consumer.getService();
			String servicePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName + "/" + serviceNode + "/" + CONSUMER_TYPE;
			exist = zkClient.exists(servicePath);
			if (!exist) {
				zkClient.createPersistent(servicePath, true);
			}

			//创建当前服务器节点
			String localIp = IpUtils.localIp();
			String currentServiceIpNode = servicePath + "/" + localIp;
			exist = zkClient.exists(currentServiceIpNode);
			if (!exist) {
				//注意,这里创建的是临时节点
				zkClient.createEphemeral(currentServiceIpNode);
			}
		}
	}

	@Override
	public Pair<List<ProviderInfo>, List<ConsumerInfo>> queryProvidersAndInvokers(String serviceName, String appkey) {
		//服务消费者列表
		List<ConsumerInfo> consumerInfos = Lists.newArrayList();
		//服务提供者列表
		List<ProviderInfo> providerInfos = Lists.newArrayList();

		//连接zk
		if (zkClient == null) {
			synchronized (RegisterCenter.class) {
				if (zkClient == null) {
					zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
				}
			}
		}

		String parentPath = ROOT_PATH + "/" + appkey;
		//获取 ROOT_PATH + APP_KEY注册中心子目录列表
		List<String> groupServiceList = zkClient.getChildren(parentPath);
		if (CollectionUtils.isEmpty(groupServiceList)) {
			return Pair.of(providerInfos, consumerInfos);
		}

		for (String group : groupServiceList) {
			String groupPath = parentPath + "/" + group;
			//获取ROOT_PATH + appKey + group 注册中心子目录列表
			List<String> serviceList = zkClient.getChildren(groupPath);
			if (CollectionUtils.isEmpty(serviceList)) {
				continue;
			}
			for (String service : serviceList) {
				//获取ROOT_PATH + appKey + group +service 注册中心子目录列表
				String servicePath = groupPath + "/" + service;
				List<String> serviceTypes = zkClient.getChildren(servicePath);
				if (CollectionUtils.isEmpty(serviceTypes)) {
					continue;
				}
				for (String serviceType : serviceTypes) {
					if (StringUtils.equals(serviceType, PROVIDER_TYPE)) {
						//获取ROOT_PATH + appKey + group +service+serviceType 注册中心子目录列表
						String providerPath = servicePath + "/" + serviceType;
						List<String> providers = zkClient.getChildren(providerPath);
						if (CollectionUtils.isEmpty(providers)) {
							continue;
						}

						//获取服务提供者信息
						for (String provider : providers) {
							String[] providerNodeArr = StringUtils.split(provider, "|");

							ProviderInfo providerInfo = new ProviderInfo();
							providerInfo.setIp(providerNodeArr[0]);
							providerInfo.setPort(Integer.parseInt(providerNodeArr[1]));
							providerInfos.add(providerInfo);
						}

					} else if (StringUtils.equals(serviceType, CONSUMER_TYPE)) {
						//获取ROOT_PATH + appKey + group +service+serviceType 注册中心子目录列表
						String invokerPath = servicePath + "/" + serviceType;
						List<String> invokers = zkClient.getChildren(invokerPath);
						if (CollectionUtils.isEmpty(invokers)) {
							continue;
						}

						//获取服务消费者信息
						for (String invoker : invokers) {
							ConsumerInfo consumerInfo = new ConsumerInfo();
							consumerInfo.setIp(invoker);
							consumerInfos.add(consumerInfo);
						}
					}
				}
			}

		}
		return Pair.of(providerInfos, consumerInfos);
	}


	//利用ZK自动刷新当前存活的服务提供者列表数据
	private void refreshActivityService(List<String> serviceIpList) {
		if (serviceIpList == null) {
			serviceIpList = Lists.newArrayList();
		}

		Map<String, List<ProviderInfo>> currentServiceMetaDataMap = Maps.newHashMap();
		for (Map.Entry<String, List<ProviderInfo>> entry : providerServiceMap.entrySet()) {
			String key = entry.getKey();
			List<ProviderInfo> providerServices = entry.getValue();

			List<ProviderInfo> serviceMetaDataModelList = currentServiceMetaDataMap.get(key);
			if (serviceMetaDataModelList == null) {
				serviceMetaDataModelList = Lists.newArrayList();
			}

			for (ProviderInfo serviceMetaData : providerServices) {
				if (serviceIpList.contains(serviceMetaData.getIp())) {
					serviceMetaDataModelList.add(serviceMetaData);
				}
			}
			currentServiceMetaDataMap.put(key, serviceMetaDataModelList);
		}
		providerServiceMap.clear();
		System.out.println("currentServiceMetaDataMap,"+ JSON.toJSONString(currentServiceMetaDataMap));
		providerServiceMap.putAll(currentServiceMetaDataMap);
	}

	private Map<String, List<ProviderInfo>> fetchOrUpdateServiceMetaData(String remoteAppKey, String groupName) {
		final Map<String, List<ProviderInfo>> providerServiceMap = Maps.newConcurrentMap();
		//连接zk
		synchronized (RegisterCenter.class) {
			if (zkClient == null) {
				zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
			}
		}

		//从ZK获取服务提供者列表
		String providePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName;
		List<String> providerServices = zkClient.getChildren(providePath);

		for (String serviceName : providerServices) {
			String servicePath = providePath + "/" + serviceName + "/" + PROVIDER_TYPE;
			List<String> ipPathList = zkClient.getChildren(servicePath);
			for (String ipPath : ipPathList) {
				String serverIp = StringUtils.split(ipPath, "|")[0];
				String serverPort = StringUtils.split(ipPath, "|")[1];

				List<ProviderInfo> providerServiceList = providerServiceMap.get(serviceName);
				if (providerServiceList == null) {
					providerServiceList = Lists.newArrayList();
				}
				ProviderInfo providerInfo = new ProviderInfo();

				try {
					providerInfo.setService(ClassUtils.getClass(serviceName).getName());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}

				providerInfo.setIp(serverIp);
				providerInfo.setPort(Integer.parseInt(serverPort));
				providerServiceList.add(providerInfo);

				providerServiceMap.put(serviceName, providerServiceList);
			}

			//监听注册服务的变化,同时更新数据到本地缓存
			zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
				@Override
				public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
					if (currentChilds == null) {
						currentChilds = Lists.newArrayList();
					}
					currentChilds = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
						@Override
						public String apply(String input) {
							return StringUtils.split(input, "|")[0];
						}
					}));
					refreshServiceMetaDataMap(currentChilds);
				}
			});
		}
		return providerServiceMap;
	}

	private void refreshServiceMetaDataMap(List<String> serviceIpList) {
		if (serviceIpList == null) {
			serviceIpList = Lists.newArrayList();
		}

		Map<String, List<ProviderInfo>> currentServiceMetaDataMap = Maps.newHashMap();
		for (Map.Entry<String, List<ProviderInfo>> entry : serviceMetaDataMap4Consume.entrySet()) {
			String serviceItfKey = entry.getKey();
			List<ProviderInfo> serviceList = entry.getValue();

			List<ProviderInfo> providerServiceList = currentServiceMetaDataMap.get(serviceItfKey);
			if (providerServiceList == null) {
				providerServiceList = Lists.newArrayList();
			}

			for (ProviderInfo serviceMetaData : serviceList) {
				if (serviceIpList.contains(serviceMetaData.getIp())) {
					providerServiceList.add(serviceMetaData);
				}
			}
			currentServiceMetaDataMap.put(serviceItfKey, providerServiceList);
		}

		serviceMetaDataMap4Consume.clear();
		serviceMetaDataMap4Consume.putAll(currentServiceMetaDataMap);
	}

}
