package com.zlframework.rpc.parallel;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.*;

/**
 * zlrpc com.zlframework.rpc.parallel
 *
 * @author Lichaojie
 * @version 2018/4/28 11:16
 */
public enum  RejectedPolicyType {
	ABORT_POLICY("AbortPolicy"),
	CALLER_RUNS_POLICY("CallerRunsPolicy"),
	DISCARD_POLICY("DiscardPolicy"),
	DISCARD_OLDEST_POLICY("DiscardOldestPolicy");

	private String value;

	private RejectedPolicyType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static RejectedPolicyType fromString(String value) {
		for (RejectedPolicyType type : RejectedPolicyType.values()) {
			if (type.getValue().equalsIgnoreCase(value.trim())) {
				return type;
			}
		}

		throw new IllegalArgumentException("Mismatched type with value=" + value);
	}

	public static RejectedExecutionHandler getPolicy(String policy){
		switch (fromString(System.getProperty(policy,"AbortPolicy"))) {
			case CALLER_RUNS_POLICY:
				return new CallerRunsPolicy();
			case DISCARD_POLICY:
				return new DiscardPolicy();
			case DISCARD_OLDEST_POLICY:
				return new DiscardOldestPolicy();
			case ABORT_POLICY:
			default:
				return new AbortPolicy();
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
