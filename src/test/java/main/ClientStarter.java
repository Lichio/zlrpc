package main;

import com.zlframework.rpc.core.SystemConfig;
import com.zlframework.rpc.services.IAddTest;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * zlrpc main
 *
 * @author Lichaojie
 * @version 2018/4/28 18:03
 */
public class ClientStarter {
	public static void main(String[] args){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("zlrpc-consumer.xml");
		IAddTest addTest = (IAddTest)context.getBean("addService");

		int result = addTest.add(2,3);

		System.out.println("client result : " + result);
	}
}
