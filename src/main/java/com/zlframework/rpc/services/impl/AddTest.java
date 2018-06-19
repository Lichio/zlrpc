package com.zlframework.rpc.services.impl;

import com.zlframework.rpc.services.IAddTest;

/**
 * zlrpc com.zlframework.rpc.services.impl
 *
 * @author Lichaojie
 * @version 2018/4/26 21:58
 */
public class AddTest implements IAddTest {

	@Override
	public int add(int a, int b){
		return a + b;
	}
}
