package com.zlframework.rpc.serialize.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.zlframework.rpc.serialize.RpcSerialize;

/**
 * zlrpc com.zlframework.rpc.serialize.xml
 *
 * @author Lichaojie
 * @version 2018/5/4 14:57
 */
public class XmlSerializer implements RpcSerialize {
	private static final XStream xStream = new XStream(new DomDriver());

	@Override
	public <T> byte[] serialize(T obj) {
		return xStream.toXML(obj).getBytes();
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		String xml = new String(data);
		return (T) xStream.fromXML(xml);
	}
}
