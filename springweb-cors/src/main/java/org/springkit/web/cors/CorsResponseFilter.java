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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class CorsResponseFilter implements Filter {

	private final CorsConfig config;

	public CorsResponseFilter() {
		config = CorsConfig.CorsConfigBuilder.loadFromFile();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		try {
			if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
				this.doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response);
			}

		} finally {
			chain.doFilter(request, response);
		}
	}

	private boolean matchHost(String origin) {

		if (config.anyHost()) {
			return true;
		}

		if (config.hosts == null) {
			return false;
		}

		for (String h : config.hosts) {
			if (h.equalsIgnoreCase(origin)) {
				return true;
			}
		}

		return false;
	}

	private void doFilterInternal(HttpServletRequest request, HttpServletResponse response) {

		if (matchURI(request)) {
			String origin = request.getHeader("Origin");
			if (origin != null && origin.trim().length() > 0 && matchHost(origin)) {
				response.addHeader("Access-Control-Allow-Origin", origin);
				response.addHeader("Access-Control-Allow-Methods", ifNullDefault(request.getHeader("Access-Control-Request-Method"), "GET,POST,PUT,DELETE,OPTIONS", String.class));
				response.addHeader("Access-Control-Allow-Headers", ifNullDefault(request.getHeader("Access-Control-Request-Headers"), "Content-Type", String.class));

				response.addIntHeader("Access-Control-Max-Age", ifNullDefault(config.maxAge, 86400, Integer.class));

				if (config.allowCredentials != null) {
					response.addHeader("Access-Control-Allow-Credentials", String.valueOf(config.allowCredentials));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T ifNullDefault(T value, T def, Class<T> clazz) {
		if (String.class.isAssignableFrom(clazz)) {
			String v = (String) value;
			if (v == null || v.trim().length() == 0) {
				return def;
			}
			return (T) v.trim();
		} else {
			return value == null ? def : value;
		}
	}

	private String getFullMatchURL(String contextPath, String matchingURL) {

		contextPath = contextPath == null ? "" : contextPath.trim();

		String url = contextPath;

		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}

		matchingURL = matchingURL == null ? "" : matchingURL.trim();
		if (!matchingURL.startsWith("/")) {
			matchingURL = "/" + matchingURL;
		}

		return url.concat(matchingURL.trim()).toLowerCase();

	}

	private boolean matchURI(HttpServletRequest request) {
		if (config.url == null || config.url.length() == 0 || "/*".equals(config.url)) {
			return true;
		} else {

			// must get ContextPath
			// the getRequestURI() contain context path
			String ctxPath = request.getContextPath();

			if (config.url.endsWith("/*")) {
				String prefix = config.url.substring(config.url.length() - "/*".length());
				prefix = ctxPath.concat(prefix);
				return request.getRequestURI().toLowerCase().startsWith(getFullMatchURL(ctxPath, prefix));
			} else {
				return request.getRequestURI().equalsIgnoreCase(getFullMatchURL(ctxPath, config.url));
			}
		}

	}

	@Override
	public void destroy() {

	}

}
