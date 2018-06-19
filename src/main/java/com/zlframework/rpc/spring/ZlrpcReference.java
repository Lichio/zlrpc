package com.zlframework.rpc.spring;

import com.google.common.reflect.Reflection;
import com.zlframework.rpc.netty.config.NettyConfig;
import com.zlframework.rpc.netty.consumer.ChannelFactory;
import com.zlframework.rpc.netty.consumer.ProxyFactory;
import com.zlframework.rpc.registry.zookeeper.IRegister4Consumer;
import com.zlframework.rpc.registry.zookeeper.RegisterCenter;
import com.zlframework.rpc.registry.zookeeper.config.ZkConfigHelper;
import com.zlframework.rpc.registry.zookeeper.model.ConsumerInfo;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;
import com.zlframework.rpc.utils.IpUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;


/**
 * zlrpc com.zlframework.rpc.spring
 *
 * @author Lichaojie
 * @version 2018/4/24 19:41
 */
public class ZlrpcReference implements FactoryBean, InitializingBean {
	private String interfaceName;
	private String serialize;
	private int timeout;
	private String loadbalance;

	private Object interfaceImpl;


	/**
	 * afterPropertiesSet方法，初始化bean的时候执行，可以针对某个具体的bean进行配置。
	 * 重写 afterPropertiesSet方法必须实现 InitializingBean接口。
	 * 实现 InitializingBean接口必须重写 afterPropertiesSet方法。
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		NettyConfig.setTimeout(timeout);

		//获取服务注册中心
		IRegister4Consumer register4Consumer = RegisterCenter.getInstance();
		//初始化服务提供者列表到本地缓存
		register4Consumer.initProviderMap(ZkConfigHelper.APP_KEY, ZkConfigHelper.GROUP);

		//初始化Netty Channel
		Map<String, List<ProviderInfo>> providerMap = register4Consumer.getServiceMetaDataMap4Consume();
		if (MapUtils.isEmpty(providerMap)) {
			throw new RuntimeException("service provider list is empty.");
		}
		ChannelFactory.setSerialize(serialize);
		ChannelFactory.getInstance().initChannelFactory(providerMap);

		//获取服务提供者代理对象
		ProxyFactory proxyFactory = ProxyFactory.getInstance(interfaceName, timeout, loadbalance);
		this.interfaceImpl = proxyFactory.getProxy();

		//将消费者信息注册到注册中心
		ConsumerInfo consumer = new ConsumerInfo();
		consumer.setService(interfaceName);
		register4Consumer.registerConsumer(consumer);
	}

	@Override
	public Object getObject() throws Exception {
		return interfaceImpl;
	}

	@Override
	public Class<?> getObjectType() {
		try {
			return this.getClass().getClassLoader().loadClass(interfaceName);
		} catch (ClassNotFoundException e) {
			System.err.println("spring analyze fail!");
		}
		return null;
	}


	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getSerialize() {
		return serialize;
	}

	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getLoadbalance() {
		return loadbalance;
	}

	public void setLoadbalance(String loadbalance) {
		this.loadbalance = loadbalance;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
