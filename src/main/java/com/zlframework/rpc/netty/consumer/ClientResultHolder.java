package com.zlframework.rpc.netty.consumer;

import com.google.common.collect.Maps;
import com.zlframework.rpc.model.RespMessage;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * zlrpc com.zlframework.rpc.netty.consumer
 *
 * @author Lichaojie
 * @version 2018/4/28 17:26
 */
public class ClientResultHolder {
	//���񷵻ؽ��Map
	private static final Map<String, RespMessageWrapper> responseMap = Maps.newConcurrentMap();
	//������ڵķ��ؽ��
	private static final ExecutorService removeExpireKeyExecutor = Executors.newSingleThreadExecutor();

	static {
		//ɾ����ʱδ��ȡ�������key,��ֹ�ڴ�й¶
		removeExpireKeyExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						for (Map.Entry<String, RespMessageWrapper> entry : responseMap.entrySet()) {
							boolean isExpire = entry.getValue().isExpire();
							if (isExpire) {
								responseMap.remove(entry.getKey());
							}
							Thread.sleep(10);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}

				}
			}
		});
	}

	/**
	 * ��ʼ�����ؽ������,requestUniqueKeyΨһ��ʶ���ε���
	 *
	 * @param requestUniqueKey
	 */
	public static void initResponseData(String requestUniqueKey) {
		responseMap.put(requestUniqueKey, RespMessageWrapper.of());
	}


	/**
	 * ��Netty�����첽���ؽ��������������
	 *
	 * @param response
	 */
	public static void putResultValue(RespMessage response) {
		long currentTime = System.currentTimeMillis();
		RespMessageWrapper responseWrapper = responseMap.get(response.getMessageId());
		responseWrapper.setResponseTime(currentTime);
		responseWrapper.getResponseQueue().add(response);
		responseMap.put(response.getMessageId(), responseWrapper);
	}


	/**
	 * �����������л�ȡNetty�첽���صĽ��ֵ
	 *
	 * @param requestUniqueKey
	 * @param timeout
	 * @return
	 */
	public static RespMessage getValue(String requestUniqueKey, long timeout) {
		RespMessageWrapper responseWrapper = responseMap.get(requestUniqueKey);
		try {
			return responseWrapper.getResponseQueue().poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			responseMap.remove(requestUniqueKey);
		}
	}
}
