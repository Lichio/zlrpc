package com.zlframework.rpc.serialize.xml;

import com.zlframework.rpc.serialize.RpcEncoder;

/**
 * zlrpc com.zlframework.rpc.serialize.xml
 *
 * @author Lichaojie
 * @version 2018/5/4 14:59
 */
public class XmlEncoder extends RpcEncoder {
	public XmlEncoder(){
		super(new XmlSerializer());
	}
}
