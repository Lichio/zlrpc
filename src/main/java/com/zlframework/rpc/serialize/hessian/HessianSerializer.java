package com.zlframework.rpc.serialize.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.zlframework.rpc.serialize.RpcSerialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * zlrpc com.zlframework.rpc.serialize.impl
 *
 * @author Lichaojie
 * @version 2018/5/4 11:56
 */
public class HessianSerializer implements RpcSerialize {

	@Override
	public byte[] serialize(Object obj) {
		if (obj == null) {
			throw new NullPointerException();
		}

		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			HessianOutput ho = new HessianOutput(os);
			ho.writeObject(obj);
			return os.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T deserialize(byte[] data,Class<T> clazz) {
		if (data == null) {
			throw new NullPointerException();
		}

		try {
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			HessianInput hi = new HessianInput(is);
			return (T) hi.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
