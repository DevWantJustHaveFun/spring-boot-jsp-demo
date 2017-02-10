/*
 * Copyright 2017 the original author or authors.
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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;

@SpringBootApplication
public class JspDemoApplication implements EmbeddedServletContainerCustomizer {

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {

		if (container instanceof JettyEmbeddedServletContainerFactory) {
			final JettyEmbeddedServletContainerFactory jetty = (JettyEmbeddedServletContainerFactory) container;

			final JettyServerCustomizer customizer = new JettyServerCustomizer() {
				@Override
				public void customize(Server server) {
					Handler handler = server.getHandler();
					WebAppContext webAppContext = (WebAppContext) handler;
					webAppContext.setBaseResource(Resource.newClassPathResource("webroot"));
				}
			};
			jetty.addServerCustomizers(customizer);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(JspDemoApplication.class, args);
	}
}
