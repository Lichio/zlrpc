package com.zlframework.rpc.netty.consumer;

import com.zlframework.rpc.model.ReqMessage;
import com.zlframework.rpc.model.RespMessage;
import com.zlframework.rpc.netty.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * zlrpc com.zlframework.rpc.netty.consumer
 *
 * @author Lichaojie
 * @version 2018/4/28 17:19
 */
public class ClientTask implements Callable<RespMessage> {
	private static final Logger logger = LoggerFactory.getLogger(ClientTask.class);

	private Channel channel;
	private InetSocketAddress inetSocketAddress;
	private ReqMessage request;

	public ClientTask(InetSocketAddress inetSocketAddress,ReqMessage request){
		this.inetSocketAddress = inetSocketAddress;
		this.request = request;
	}

	@Override
	public RespMessage call() throws Exception {
		//��ʼ�����ؽ������,�����ε��õ�Ψһ��ʶ��ΪKey���뷵�ؽ����Map
		ClientResultHolder.initResponseData(request.getMessageId());
		//���ݱ��ص��÷����ṩ�ߵ�ַ��ȡ��Ӧ��Nettyͨ��channel����
		ArrayBlockingQueue<Channel> blockingQueue = ChannelFactory.getInstance().acquire(inetSocketAddress);
		try {
			if (channel == null) {
				//�Ӷ����л�ȡ���ε��õ�Nettyͨ��channel
				channel = blockingQueue.poll(NettyConfig.getTimeout(), TimeUnit.MILLISECONDS);
			}

			//����ȡ��channelͨ���Ѿ�������,�����»�ȡһ��
			while (!channel.isOpen() || !channel.isActive() || !channel.isWritable()) {
				logger.warn("----------retry get new Channel------------");
				channel = blockingQueue.poll(NettyConfig.getTimeout(), TimeUnit.MILLISECONDS);
				if (channel == null) {
					//��������û�п��õ�Channel,������ע��һ��Channel
					channel = ChannelFactory.getInstance().registerChannel(inetSocketAddress);
				}
			}

			//�����ε��õ���Ϣд��Nettyͨ��,�����첽����
			ChannelFuture channelFuture = channel.writeAndFlush(request);
			channelFuture.syncUninterruptibly();

			//�ӷ��ؽ�������л�ȡ���ؽ��,ͬʱ���õȴ���ʱʱ��ΪinvokeTimeout
			long invokeTimeout = NettyConfig.getTimeout();
			return ClientResultHolder.getValue(request.getMessageId(), invokeTimeout);
		} catch (Exception e) {
			logger.error("service invoke error.", e);
		} finally {
			//���ε�����Ϻ�,��Netty��ͨ��channel�����ͷŵ�������,�Ա��´ε��ø���
			ChannelFactory.getInstance().release(blockingQueue, channel, inetSocketAddress);
		}
		return null;
	}
}
