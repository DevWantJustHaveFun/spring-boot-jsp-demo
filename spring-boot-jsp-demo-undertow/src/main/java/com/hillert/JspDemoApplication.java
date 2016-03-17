/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hillert;

import java.util.HashMap;

import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;

import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.api.DeploymentInfo;

/**
 *
 * @author Gunnar Hillert
 *
 */
@SpringBootApplication
public class JspDemoApplication implements EmbeddedServletContainerCustomizer {

	public static void main(String[] args) {
		SpringApplication.run(JspDemoApplication.class, args);
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		if (container instanceof UndertowEmbeddedServletContainerFactory) {
			final UndertowEmbeddedServletContainerFactory undertow = (UndertowEmbeddedServletContainerFactory) container;

			final UndertowDeploymentInfoCustomizer customizer = new UndertowDeploymentInfoCustomizer() {

				@Override
				public void customize(DeploymentInfo deploymentInfo) {
					deploymentInfo.setClassLoader(JspDemoApplication.class.getClassLoader())
					.setContextPath("/")
					.setDeploymentName("servletContext.war")
					.setResourceManager(new DefaultResourceLoader(JspDemoApplication.class))
					.addServlet(JspServletBuilder.createServlet("Default Jsp Servlet", "*.jsp"));

					final HashMap<String, TagLibraryInfo> tagLibraryInfo = TldLocator.createTldInfos();

					JspServletBuilder.setupDeployment(deploymentInfo, new HashMap<String, JspPropertyGroup>(), tagLibraryInfo, new HackInstanceManager());

				}
			};

			undertow.addDeploymentInfoCustomizers(customizer);
		}
	}

	public static class DefaultResourceLoader extends ClassPathResourceManager {
		public DefaultResourceLoader(final Class<?> clazz) {
			super(clazz.getClassLoader(), "");
		}
	}
}
