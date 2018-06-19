package com.zlframework.rpc.netty.consumer;

import com.zlframework.rpc.cluster.ClusterStrategy;
import com.zlframework.rpc.cluster.ClusterStrategyFrame;
import com.zlframework.rpc.model.ReqMessage;
import com.zlframework.rpc.model.RespMessage;
import com.zlframework.rpc.netty.config.NettyConfig;
import com.zlframework.rpc.registry.zookeeper.IRegister4Consumer;
import com.zlframework.rpc.registry.zookeeper.RegisterCenter;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * zlrpc com.zlframework.rpc.netty.consumer
 *
 * @author Lichaojie
 * @version 2018/4/28 16:40
 */
public class ProxyFactory implements InvocationHandler{

	private ExecutorService fixedThreadPool = null;
	private static volatile ProxyFactory instance;

	//服务接口
	private String targetInterface;
	//超时时间
	private int consumeTimeout;
	//调用者线程数
	private static int threadWorkerNumber = 10;
	//负载均衡策略
	private String clusterStrategy;

	private ProxyFactory(String targetInterface,int consumeTimeout,String clusterStrategy){
		this.targetInterface = targetInterface;
		this.consumeTimeout = consumeTimeout;
		this.clusterStrategy = clusterStrategy;
	}

	public Object getProxy() throws ClassNotFoundException {
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{Class.forName(targetInterface)}, this);
	}

	public static ProxyFactory getInstance(String targetInterface,int consumeTimeout,String clusterStrategy){
		if(null == instance){
			synchronized (ProxyFactory.class){
				if(null == instance){
					instance = new ProxyFactory(targetInterface,consumeTimeout,clusterStrategy);
				}
			}
		}
		return instance;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//服务接口名称
		String serviceKey = targetInterface;
		//获取某个接口的服务提供者列表
		IRegister4Consumer register4Consumer = RegisterCenter.getInstance();
		List<ProviderInfo> providerServices = register4Consumer.getServiceMetaDataMap4Consume().get(serviceKey);
		//根据软负载策略,从服务提供者列表选取本次调用的服务提供者
		ClusterStrategy clusterStrategyService = ClusterStrategyFrame.select(clusterStrategy);
		ProviderInfo providerInfo = clusterStrategyService.select(providerServices);

		//声明调用AresRequest对象,AresRequest表示发起一次调用所包含的信息
		final ReqMessage request = new ReqMessage();
		//设置本次调用的唯一标识
		request.setMessageId(UUID.randomUUID().toString() + "-" + Thread.currentThread().getId());
		//设置本次调用的服务提供者信息
		request.setClassName(providerInfo.getService());
		//设置本次调用的方法名称
		request.setMethodName(method.getName());
		//设置本次调用的方法参数类型信息
		request.setParametersType(method.getParameterTypes());
		//设置本次调用方法的参数值
		request.setParametersVal(args);

		try {
			//构建用来发起调用的线程池
			if (fixedThreadPool == null) {
				synchronized (ProxyFactory.class) {
					if (null == fixedThreadPool) {
						fixedThreadPool = Executors.newFixedThreadPool(threadWorkerNumber);
					}
				}
			}
			//根据服务提供者的ip,port,构建InetSocketAddress对象,标识服务提供者地址
			String serverIp = providerInfo.getIp();
			int serverPort = providerInfo.getPort();
			System.out.println("ip1 : " + serverIp + "  port1 : " + serverPort);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIp, serverPort);
			//提交本次调用信息到线程池fixedThreadPool,发起调用
			Future<RespMessage> responseFuture = fixedThreadPool.submit(new ClientTask(inetSocketAddress, request));
			//获取调用的返回结果
			RespMessage response = responseFuture.get(NettyConfig.getTimeout(), TimeUnit.MILLISECONDS);
			if (response != null) {
				return response.getResult();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}
