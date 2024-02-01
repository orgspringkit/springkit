/*
 * Copyright (c) 2011-2024, springkit (orgspringkit@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springkit.web.cors;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;

public class CorsConfiguration implements ServletContextInitializer {

	private void registerFilter(ServletContext servletContext) {
		String name = CorsResponseFilter.class.getSimpleName();

		FilterRegistration filter = servletContext.getFilterRegistration(name);
		if (filter == null) {
			FilterRegistration.Dynamic reg = null;
			try {
				reg = servletContext.addFilter(name, new CorsResponseFilter());
				reg.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
				System.out.println("CorsResponseFilter can not register");
			}

		}

	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		this.registerFilter(servletContext);
	}

}
