package com.zlframework.rpc.serialize.xml;

import com.zlframework.rpc.serialize.RpcDecoder;

/**
 * zlrpc com.zlframework.rpc.serialize.xml
 *
 * @author Lichaojie
 * @version 2018/5/4 14:58
 */
public class XmlDecoder extends RpcDecoder {
	public XmlDecoder(Class<?> clazz){
		super(clazz,new XmlSerializer());
	}
}
