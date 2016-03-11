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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class JspDemoApplication  extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(JspDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JspDemoApplication.class, args);
	}

	private static File getScratchDir() {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");

		if (!scratchDir.exists())
		{
			if (!scratchDir.mkdirs())
			{
				throw new IllegalStateException("Unable to create scratch directory: " + scratchDir);
			}
		}
		return scratchDir;
	}

	@Bean
	public JettyServerCustomizer jettyServerCustomizer() {

		return new JettyServerCustomizer() {

			@Override
			public void customize(Server server) {
				WebAppContext webAppContext = (WebAppContext) server.getHandler();

				File tempDir = getScratchDir();
				LOGGER.info("Setting Jetty temp directory to {}", tempDir);
				webAppContext.setTempDirectory(tempDir);

				try {
					ClassPathResource classPathResource = new ClassPathResource("META-INF/resources");
					String externalResource = classPathResource.getURI().toString();
					String[] resources = new String[] { externalResource };
					webAppContext.setBaseResource(new ResourceCollection(resources));

					ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
					webAppContext.setClassLoader(jspClassLoader);
				}
				catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		};
	}
	public void customizeJetty(
			JettyEmbeddedServletContainerFactory containerFactory) {
		containerFactory.addServerCustomizers(jettyServerCustomizer());
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		if (container instanceof JettyEmbeddedServletContainerFactory) {
			customizeJetty((JettyEmbeddedServletContainerFactory) container);
		}
	}
}
