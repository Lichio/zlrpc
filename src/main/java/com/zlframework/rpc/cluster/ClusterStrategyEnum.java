package com.zlframework.rpc.cluster;

import org.apache.commons.lang3.StringUtils;

/**
 * zlrpc com.zlframework.rpc.cluster
 *
 * @author Lichaojie
 * @version 2018/4/28 16:47
 */
public enum ClusterStrategyEnum {

	//����㷨
	Random("Random"),
	//Ȩ������㷨
	WeightRandom("WeightRandom"),
	//��ѯ�㷨
	Polling("Polling"),
	//Ȩ����ѯ�㷨
	WeightPolling("WeightPolling"),
	//Դ��ַhash�㷨
	Hash("Hash");

	private ClusterStrategyEnum(String code) {
		this.code = code;
	}


	public static ClusterStrategyEnum query(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		for (ClusterStrategyEnum strategy : values()) {
			if (StringUtils.equals(code, strategy.getCode())) {
				return strategy;
			}
		}
		return null;
	}

	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
