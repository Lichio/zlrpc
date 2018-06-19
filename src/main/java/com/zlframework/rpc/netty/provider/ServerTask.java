package com.zlframework.rpc.netty.provider;

import com.zlframework.rpc.model.ReqMessage;
import com.zlframework.rpc.model.RespMessage;
import com.zlframework.rpc.registry.zookeeper.RegisterCenter;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * zlrpc com.zlframework.rpc.netty
 *
 * @author Lichaojie
 * @version 2018/4/28 9:53
 */
public class ServerTask implements Callable<RespMessage> ,ApplicationContextAware{

	private ApplicationContext applicationContext;
	private ReqMessage request;

	public ServerTask(ReqMessage request){
		this.request = request;
	}

	@Override
	public RespMessage call() throws Exception {
		RespMessage response = new RespMessage();
		response.setMessageId(request.getMessageId());
		Object result;

		try{
			//��ע�����Ļ�ȡ�����ṩ����Ϣ
			List<ProviderInfo> providerInfos = RegisterCenter.getInstance().getProviderServiceMap().get(request.getClassName());

			//�ӷ����ṩ���б��л�ȡ��Ӧ�ķ����ṩ��
			ProviderInfo provider = providerInfos.stream().filter(providerInfo -> providerInfo.getMethod().getName().equalsIgnoreCase(request.getMethodName()))
					.collect(Collectors.toList()).iterator().next();
			Object object = provider.getServiceImpl();

			//�������
			Method method = object.getClass().getDeclaredMethod(request.getMethodName(),request.getParametersType());
			result = method.invoke(object,request.getParametersVal());
		}catch (Exception e){
			result = e;
		}
		response.setResult(result);
		return response;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
