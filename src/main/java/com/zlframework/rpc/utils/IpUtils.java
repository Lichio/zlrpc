package com.zlframework.rpc.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.List;

/**
 * zlrpc com.zlframework.rpc.utils
 *
 * @author Lichaojie
 * @version 2018/4/26 19:47
 */
public class IpUtils {

	private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

	private static String hostIp = StringUtils.EMPTY;


	/**
	 * ��ȡ����Ip
	 * <p/>
	 * ͨ�� ��ȡϵͳ���е�networkInterface����ӿ� Ȼ����� ÿ�������µ�InterfaceAddress�顣
	 * ��÷��� <code>InetAddress instanceof Inet4Address</code> ������һ��IpV4��ַ
	 *
	 * @return
	 */
	public static String localIp() {
		return hostIp;
	}


	public static String getRealIp() {
		String localip = null;// ����IP�����û����������IP�򷵻���
		String netip = null;// ����IP

		try {
			Enumeration<NetworkInterface> netInterfaces =
					NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// �Ƿ��ҵ�����IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress()
							&& !ip.isLoopbackAddress()
							&& !ip.getHostAddress().contains(":")) {// ����IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress()
							&& !ip.isLoopbackAddress()
							&& !ip.getHostAddress().contains(":")) {// ����IP
						localip = ip.getHostAddress();
					}
				}
			}

			if (netip != null && !"".equals(netip)) {
				return netip;
			} else {
				return localip;
			}
		} catch (SocketException e) {
			logger.warn("��ȡ����Ipʧ��:�쳣��Ϣ:" + e.getMessage());
			throw new RuntimeException(e);
		}
	}


	static {

		String ip = null;
		Enumeration allNetInterfaces;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
				for (InterfaceAddress add : InterfaceAddress) {
					InetAddress Ip = add.getAddress();
					if (Ip != null && Ip instanceof Inet4Address) {
						if (StringUtils.equals(Ip.getHostAddress(), "127.0.0.1")) {
							continue;
						}
						ip = Ip.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			logger.warn("��ȡ����Ipʧ��:�쳣��Ϣ:" + e.getMessage());
			throw new RuntimeException(e);
		}
		hostIp = ip;
	}


	/**
	 * ��ȡ������һ����Чip<br/>
	 * ���û��Чip�����ؿմ�
	 *
	 * @return
	 */
	public static String getHostFirstIp() {
		return hostIp;
	}


	public static void main(String[] args) throws Exception {
		//System.out.println(localIp());
		System.out.println(getRealIp());
		System.out.println(getHostFirstIp());
		//System.out.println(Integer.valueOf("123 "));
	}
}
