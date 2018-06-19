package com.zlframework.rpc.netty.consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zlframework.rpc.registry.zookeeper.config.ZkConfigHelper;
import com.zlframework.rpc.registry.zookeeper.model.ProviderInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * zlrpc com.zlframework.rpc.netty.consumer
 *
 * @author Lichaojie
 * @version 2018/4/28 15:59
 */
public class ChannelFactory {

	private static final Logger logger = LoggerFactory.getLogger(ChannelFactory.class);

	private static volatile ChannelFactory instance;

	//KeyΪ�����ṩ�ߵ�ַ,valueΪNetty Channel��������
	private static final Map<InetSocketAddress, ArrayBlockingQueue<Channel>> channelPoolMap = Maps.newConcurrentMap();
	//��ʼ��Netty Channel�������еĳ���,��ֵΪ��������Ϣ
	private static final int channelConnectSize = 10;

	private static String serialize;
	//�����ṩ���б�
	private List<ProviderInfo> serviceMetaDataList = Lists.newArrayList();

	private ChannelFactory(){}

	public void initChannelFactory(Map<String, List<ProviderInfo>> providerMap){
		//�������ṩ����Ϣ����serviceMetaDataList�б�
		Collection<List<ProviderInfo>> collectionServiceMetaDataList = providerMap.values();
		for (List<ProviderInfo> serviceMetaDataModels : collectionServiceMetaDataList) {
			if (CollectionUtils.isEmpty(serviceMetaDataModels)) {
				continue;
			}
			serviceMetaDataList.addAll(serviceMetaDataModels);
		}

		//��ȡ�����ṩ�ߵ�ַ�б�
		Set<InetSocketAddress> socketAddressSet = Sets.newHashSet();
		for (ProviderInfo serviceMetaData : serviceMetaDataList) {
			String serviceIp = serviceMetaData.getIp();
			int servicePort = serviceMetaData.getPort();

			InetSocketAddress socketAddress = new InetSocketAddress(serviceIp, servicePort);
			socketAddressSet.add(socketAddress);
		}

		//���ݷ����ṩ�ߵ�ַ�б��ʼ��Channel��������,���Ե�ַΪKey,��ַ��Ӧ��Channel��������Ϊvalue,����channelPoolMap
		for (InetSocketAddress socketAddress : socketAddressSet) {
			try {
				int realChannelConnectSize = 0;
				while (realChannelConnectSize < channelConnectSize) {
					Channel channel = null;
					while (channel == null) {
						//��channel������,��ע���µ�Netty Channel
						channel = registerChannel(socketAddress);
					}
					//������,��ʼ����ʱ������������е�Netty Channel����������channelConnectSize
					realChannelConnectSize++;

					//����ע���Netty Channel������������channelArrayBlockingQueue
					// ������������channelArrayBlockingQueue��Ϊvalue����channelPoolMap
					ArrayBlockingQueue<Channel> channelArrayBlockingQueue = channelPoolMap.get(socketAddress);
					if (channelArrayBlockingQueue == null) {
						channelArrayBlockingQueue = new ArrayBlockingQueue<Channel>(channelConnectSize);
						channelPoolMap.put(socketAddress, channelArrayBlockingQueue);
					}
					channelArrayBlockingQueue.offer(channel);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Ϊ�����ṩ�ߵ�ַsocketAddressע���µ�Channel
	 *
	 * @param socketAddress
	 * @return
	 */
	public Channel registerChannel(InetSocketAddress socketAddress) {
		try {
			EventLoopGroup group = new NioEventLoopGroup(10);
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.remoteAddress(socketAddress);

			bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new SendChannelInitializer(serialize));

			ChannelFuture channelFuture = bootstrap.connect().sync();
			final Channel newChannel = channelFuture.channel();
			final CountDownLatch connectedLatch = new CountDownLatch(1);

			final List<Boolean> isSuccessHolder = Lists.newArrayListWithCapacity(1);
			//����Channel�Ƿ����ɹ�
			channelFuture.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					//��Channel�����ɹ�,���潨���ɹ��ı��
					if (future.isSuccess()) {
						isSuccessHolder.add(Boolean.TRUE);
					} else {
						//��Channel����ʧ��,���潨��ʧ�ܵı��
						future.cause().printStackTrace();
						isSuccessHolder.add(Boolean.FALSE);
					}
					connectedLatch.countDown();
				}
			});

			connectedLatch.await();
			//���Channel�����ɹ�,�����½���Channel
			if (isSuccessHolder.get(0)) {
				return newChannel;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * ���ݷ����ṩ�ߵ�ַ��ȡ��Ӧ��Netty Channel��������
	 *
	 * @param socketAddress
	 * @return
	 */
	public ArrayBlockingQueue<Channel> acquire(InetSocketAddress socketAddress) {
		return channelPoolMap.get(socketAddress);
	}


	/**
	 * Channelʹ�����֮��,���յ���������arrayBlockingQueue
	 *
	 * @param arrayBlockingQueue
	 * @param channel
	 * @param inetSocketAddress
	 */
	public void release(ArrayBlockingQueue<Channel> arrayBlockingQueue, Channel channel, InetSocketAddress inetSocketAddress) {
		if (arrayBlockingQueue == null) {
			return;
		}

		//����֮ǰ�ȼ��channel�Ƿ����,�����õĻ�,����ע��һ��,������������
		if (channel == null || !channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
			if (channel != null) {
				channel.deregister().syncUninterruptibly().awaitUninterruptibly();
				channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
			}
			Channel newChannel = null;
			while (newChannel == null) {
				logger.debug("---------register new Channel-------------");
				newChannel = registerChannel(inetSocketAddress);
			}
			arrayBlockingQueue.offer(newChannel);
			return;
		}
		arrayBlockingQueue.offer(channel);
	}


	public static ChannelFactory getInstance(){
		if(instance == null){
			synchronized (ChannelFactory.class){
				if(instance == null){
					instance = new ChannelFactory();
				}
			}
		}
		return instance;
	}

	public static String getSerialize() {
		return serialize;
	}

	public static void setSerialize(String serialize) {
		ChannelFactory.serialize = serialize;
	}
}
