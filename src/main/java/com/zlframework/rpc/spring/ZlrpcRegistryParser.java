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
 * @version 2018/4/24 19:35
 */
public class ZlrpcRegistryParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String id = element.getAttribute("id");
		String protocol = element.getAttribute("protocol");
		String address = element.getAttribute("address");

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(ZlrpcRegistry.class);
		beanDefinition.getPropertyValues().addPropertyValue("address", address);

		if(StringUtils.isNotBlank(protocol)){
			beanDefinition.getPropertyValues().addPropertyValue("protocol", protocol);
		}

		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);

		return beanDefinition;
	}
}
