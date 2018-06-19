package com.zlframework.rpc.spring;

import com.google.common.io.CharStreams;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * zlrpc com.zlframework.rpc.spring
 *
 * @author Lichaojie
 * @version 2018/4/23 17:27
 */
public class ZlrpcNamespaceHandler extends NamespaceHandlerSupport{

	static {
		Resource resource = new ClassPathResource("zlrpc-logo.txt");
		if (resource.exists()) {
			try {
				Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8");
				String text = CharStreams.toString(reader);
				System.out.println(text);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("      _\n" +
					"     | |\n" +
					" _ __| |   _ __ _ __   ___\n" +
					"|__ /| |  | '__| '_ \\ / __|\n" +
					"  //_| |_ | |  | |_) | (__\n" +
					" /_ _|\\__||_|  | .__/ \\___|\n" +
					"               | |\n" +
					"               |_|\n" +
					"\n" +
					"[zlrpc 1.0,Build 2018/4/23, Author:LiChaojie]");
		}
	}

	@Override
	public void init() {
		registerBeanDefinitionParser("service",new ZlrpcServiceParser());
		registerBeanDefinitionParser("registry",new ZlrpcRegistryParser());
		registerBeanDefinitionParser("reference",new ZlrpcReferenceParser());
	}
}
