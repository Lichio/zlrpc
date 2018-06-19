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

	//����ӿ�
	private String targetInterface;
	//��ʱʱ��
	private int consumeTimeout;
	//�������߳���
	private static int threadWorkerNumber = 10;
	//���ؾ������
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
		//����ӿ�����
		String serviceKey = targetInterface;
		//��ȡĳ���ӿڵķ����ṩ���б�
		IRegister4Consumer register4Consumer = RegisterCenter.getInstance();
		List<ProviderInfo> providerServices = register4Consumer.getServiceMetaDataMap4Consume().get(serviceKey);
		//�������ز���,�ӷ����ṩ���б�ѡȡ���ε��õķ����ṩ��
		ClusterStrategy clusterStrategyService = ClusterStrategyFrame.select(clusterStrategy);
		ProviderInfo providerInfo = clusterStrategyService.select(providerServices);

		//��������AresRequest����,AresRequest��ʾ����һ�ε�������������Ϣ
		final ReqMessage request = new ReqMessage();
		//���ñ��ε��õ�Ψһ��ʶ
		request.setMessageId(UUID.randomUUID().toString() + "-" + Thread.currentThread().getId());
		//���ñ��ε��õķ����ṩ����Ϣ
		request.setClassName(providerInfo.getService());
		//���ñ��ε��õķ�������
		request.setMethodName(method.getName());
		//���ñ��ε��õķ�������������Ϣ
		request.setParametersType(method.getParameterTypes());
		//���ñ��ε��÷����Ĳ���ֵ
		request.setParametersVal(args);

		try {
			//��������������õ��̳߳�
			if (fixedThreadPool == null) {
				synchronized (ProxyFactory.class) {
					if (null == fixedThreadPool) {
						fixedThreadPool = Executors.newFixedThreadPool(threadWorkerNumber);
					}
				}
			}
			//���ݷ����ṩ�ߵ�ip,port,����InetSocketAddress����,��ʶ�����ṩ�ߵ�ַ
			String serverIp = providerInfo.getIp();
			int serverPort = providerInfo.getPort();
			System.out.println("ip1 : " + serverIp + "  port1 : " + serverPort);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIp, serverPort);
			//�ύ���ε�����Ϣ���̳߳�fixedThreadPool,�������
			Future<RespMessage> responseFuture = fixedThreadPool.submit(new ClientTask(inetSocketAddress, request));
			//��ȡ���õķ��ؽ��
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
