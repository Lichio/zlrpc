package com.zlframework.rpc.spring;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * zlrpc com.zlframework.rpc.spring
 *
 * @author Lichaojie
 * @version 2018/4/24 19:28
 */
public class ZlrpcServiceParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String interfaceName = element.getAttribute("interface");
		String ref = element.getAttribute("ref");
		String serialize = element.getAttribute("serialize");
		String port = element.getAttribute("port");
		String timeout = element.getAttribute("timeout");

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(ZlrpcService.class);
		beanDefinition.setLazyInit(false);
		beanDefinition.getPropertyValues().addPropertyValue("interfaceName", interfaceName);
		beanDefinition.getPropertyValues().addPropertyValue("ref", ref);
		beanDefinition.getPropertyValues().addPropertyValue("port",Integer.parseInt(port));

		if(StringUtils.isNotBlank(serialize)){
			beanDefinition.getPropertyValues().addPropertyValue("serialize", serialize);
		}

		if(StringUtils.isNotBlank(timeout)){
			beanDefinition.getPropertyValues().addPropertyValue("timeout",Integer.parseInt(timeout));
		}

		parserContext.getRegistry().registerBeanDefinition(interfaceName, beanDefinition);

		return beanDefinition;
	}
}
