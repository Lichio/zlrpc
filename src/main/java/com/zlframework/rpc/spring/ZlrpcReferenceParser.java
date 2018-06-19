package com.zlframework.rpc.spring;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * zlrpc com.zlframework.rpc.spring
 *
 * @author Lichaojie
 * @version 2018/4/24 19:40
 */
public class ZlrpcReferenceParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String interfaceName = element.getAttribute("interface");
		String id = element.getAttribute("id");
		String serialize = element.getAttribute("serialize");
		String timeout = element.getAttribute("timeout");
		String loadbalance = element.getAttribute("loadbalance");

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(ZlrpcReference.class);
		beanDefinition.setLazyInit(false);

		beanDefinition.getPropertyValues().addPropertyValue("interfaceName", interfaceName);
		beanDefinition.getPropertyValues().addPropertyValue("serialize", serialize);
		if(NumberUtils.isNumber(timeout)){
			beanDefinition.getPropertyValues().addPropertyValue("timeout", Integer.parseInt(timeout));
		}

		if(StringUtils.isNotBlank(loadbalance)){
			beanDefinition.getPropertyValues().addPropertyValue("loadbalance", loadbalance);
		}

		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}
}
