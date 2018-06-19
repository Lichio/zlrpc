package com.zlframework.rpc.spring;

import com.google.common.collect.Lists;
import com.zlframework.rpc.netty.provider.ZlrpcServer;
import com.zlframework.rpc.registry.zookeeper.IRegister4Provider;
import com.zlframework.rpc.registry.zookeeper.RegisterCenter;
import com.zlframework.rpc.registry.zookeeper.config.ZkConfigHelper;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;
import com.zlframework.rpc.utils.IpUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.List;

/**
 * zlrpc com.zlframework.rpc.spring
 *
 * @author Lichaojie
 * @version 2018/4/24 19:29
 */
public class ZlrpcService implements InitializingBean,FactoryBean,ApplicationContextAware{
	private String interfaceName;
	private String ref;
	private String serialize;
	private int port;
	private int timeout;

	private ApplicationContext applicationContext;

	@Override
	public Object getObject() throws Exception {
		return getObjectType().getClass().newInstance();
	}

	@Override
	public Class<?> getObjectType() {
		try {
			return this.getClass().getClassLoader().loadClass(interfaceName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ZkConfigHelper.setZkConnectionTimeout(timeout == 0 ? 600 : timeout);

		ZlrpcServer server = ZlrpcServer.getInstance();
		server.setPort(port);
		server.setSerialize(serialize);
		server.start();

		//注册到zk,元数据注册中心
		List<ProviderInfo> providerServiceList = buildProviderServiceInfos();
		IRegister4Provider register4Provider = RegisterCenter.getInstance();
		register4Provider.registerProvider(providerServiceList);
	}

	private List<ProviderInfo> buildProviderServiceInfos() throws ClassNotFoundException {
		List<ProviderInfo> providerList = Lists.newArrayList();
		Object serviceImpl = applicationContext.getBean(ref);
		Method[] methods = serviceImpl.getClass().getDeclaredMethods();
		for (Method method : methods) {
			ProviderInfo providerInfo = new ProviderInfo();
			providerInfo.setService(interfaceName);
			providerInfo.setServiceImpl(serviceImpl);
			providerInfo.setIp(IpUtils.localIp());
			providerInfo.setPort(port);
			providerInfo.setTimeout(timeout);
			providerInfo.setMethod(method);
			providerList.add(providerInfo);
		}
		return providerList;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getSerialize() {
		return serialize;
	}

	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
