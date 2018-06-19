package main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * zlrpc PACKAGE_NAME
 *
 * @author Lichaojie
 * @version 2018/4/26 21:27
 */
public class ServerStarter {
	public static void main(String[] args){
 		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("zlrpc-provider.xml");
 		System.out.println(" 服务发布完成");
	}
}
