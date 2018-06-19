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

	//Key为服务提供者地址,value为Netty Channel阻塞队列
	private static final Map<InetSocketAddress, ArrayBlockingQueue<Channel>> channelPoolMap = Maps.newConcurrentMap();
	//初始化Netty Channel阻塞队列的长度,该值为可配置信息
	private static final int channelConnectSize = 10;

	private static String serialize;
	//服务提供者列表
	private List<ProviderInfo> serviceMetaDataList = Lists.newArrayList();

	private ChannelFactory(){}

	public void initChannelFactory(Map<String, List<ProviderInfo>> providerMap){
		//将服务提供者信息存入serviceMetaDataList列表
		Collection<List<ProviderInfo>> collectionServiceMetaDataList = providerMap.values();
		for (List<ProviderInfo> serviceMetaDataModels : collectionServiceMetaDataList) {
			if (CollectionUtils.isEmpty(serviceMetaDataModels)) {
				continue;
			}
			serviceMetaDataList.addAll(serviceMetaDataModels);
		}

		//获取服务提供者地址列表
		Set<InetSocketAddress> socketAddressSet = Sets.newHashSet();
		for (ProviderInfo serviceMetaData : serviceMetaDataList) {
			String serviceIp = serviceMetaData.getIp();
			int servicePort = serviceMetaData.getPort();

			InetSocketAddress socketAddress = new InetSocketAddress(serviceIp, servicePort);
			socketAddressSet.add(socketAddress);
		}

		//根据服务提供者地址列表初始化Channel阻塞队列,并以地址为Key,地址对应的Channel阻塞队列为value,存入channelPoolMap
		for (InetSocketAddress socketAddress : socketAddressSet) {
			try {
				int realChannelConnectSize = 0;
				while (realChannelConnectSize < channelConnectSize) {
					Channel channel = null;
					while (channel == null) {
						//若channel不存在,则注册新的Netty Channel
						channel = registerChannel(socketAddress);
					}
					//计数器,初始化的时候存入阻塞队列的Netty Channel个数不超过channelConnectSize
					realChannelConnectSize++;

					//将新注册的Netty Channel存入阻塞队列channelArrayBlockingQueue
					// 并将阻塞队列channelArrayBlockingQueue作为value存入channelPoolMap
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
	 * 为服务提供者地址socketAddress注册新的Channel
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
			//监听Channel是否建立成功
			channelFuture.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					//若Channel建立成功,保存建立成功的标记
					if (future.isSuccess()) {
						isSuccessHolder.add(Boolean.TRUE);
					} else {
						//若Channel建立失败,保存建立失败的标记
						future.cause().printStackTrace();
						isSuccessHolder.add(Boolean.FALSE);
					}
					connectedLatch.countDown();
				}
			});

			connectedLatch.await();
			//如果Channel建立成功,返回新建的Channel
			if (isSuccessHolder.get(0)) {
				return newChannel;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * 根据服务提供者地址获取对应的Netty Channel阻塞队列
	 *
	 * @param socketAddress
	 * @return
	 */
	public ArrayBlockingQueue<Channel> acquire(InetSocketAddress socketAddress) {
		return channelPoolMap.get(socketAddress);
	}


	/**
	 * Channel使用完毕之后,回收到阻塞队列arrayBlockingQueue
	 *
	 * @param arrayBlockingQueue
	 * @param channel
	 * @param inetSocketAddress
	 */
	public void release(ArrayBlockingQueue<Channel> arrayBlockingQueue, Channel channel, InetSocketAddress inetSocketAddress) {
		if (arrayBlockingQueue == null) {
			return;
		}

		//回收之前先检查channel是否可用,不可用的话,重新注册一个,放入阻塞队列
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
