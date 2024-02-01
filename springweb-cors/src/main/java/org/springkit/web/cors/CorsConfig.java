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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

class CorsConfig {

	public Boolean allowCredentials;
	public String[] hosts;
	public String url;
	public Integer maxAge;

	/**
	 * if set hosts, it will return false
	 * 
	 * @return
	 */
	public boolean anyHost() {
		return hosts == null || hosts.length == 0;
	}

	public static CorsConfig merge(CorsConfig c1, CorsConfig c2) {

		CorsConfig c;
		if (c1 != null) {
			c = c1;
		} else {
			c = new CorsConfig();
		}

		if (c2 != null) {
			if (c2.allowCredentials != null) {
				c.allowCredentials = c2.allowCredentials;
			}
			if (c2.hosts != null && c.hosts.length > 0) {
				c.hosts = c2.hosts;
			}
			if (c2.url != null) {
				c.url = c2.url;
			}
			if (c2.maxAge != null) {
				c.maxAge = c2.maxAge;
			}
		}
		return c;
	}

	public static class CorsConfigBuilder {

		private final static String filename = "cors.properties";

		private final static URL innerResource;

		static {
			String jarInnerPath;
			{
				URL u = CorsConfig.class.getResource("");
				String clzPath = CorsConfig.class.getName().replaceAll("\\.", "/").replace("/CorsConfig", "");
				jarInnerPath = u.toString().replace(clzPath + "/", "");
			}

			{
				URL inner = null;
				try {
					Enumeration<URL> resList = CorsConfig.class.getClassLoader().getResources(filename);
					while (resList.hasMoreElements()) {
						URL r = resList.nextElement();
						if (r.toString().startsWith(jarInnerPath)) {
							inner = r;
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (inner != null) {
					innerResource = inner;
				} else {
					innerResource = null;
				}
			}
		}

		private static CorsConfig loadFromURL(URL resource) {
			Properties props = new Properties();

			if (resource != null) {
				try {
					File f = new File(resource.toURI());
					try (FileInputStream fin = new FileInputStream(f)) {

						if (fin != null) {
							props.load(fin);
						}
					}
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}

			CorsConfig cf = new CorsConfig();
			cf.allowCredentials = get(props.getProperty("cors.allow-credentials"), Boolean.class);
			cf.url = get(props.getProperty("cors.url"), String.class);
			cf.maxAge = get(props.getProperty("cors.max-age"), Integer.class);

			String hosts = get(props.getProperty("cors.hosts"), String.class);
			if (hosts != null) {
				List<String> hs = new ArrayList<>();
				for (String h : hosts.split(",")) {
					if (h.trim().length() > 0) {
						hs.add(h.trim());
					}
				}
				if (!hs.isEmpty()) {
					cf.hosts = hs.toArray(new String[hs.size()]);
				}
			}

			return cf;
		}

		public static CorsConfig loadFromFile() {

			CorsConfig c = null;
			try {
				Enumeration<URL> resList = CorsConfigBuilder.class.getClassLoader().getResources(filename);
				while (resList.hasMoreElements()) {
					URL r = resList.nextElement();
					if (innerResource == null || !r.equals(innerResource)) {
						c = CorsConfig.merge(c, loadFromURL(r));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return CorsConfig.merge(loadFromURL(innerResource), c);
		}

		@SuppressWarnings("unchecked")
		private static <T> T get(String value, Class<T> clz) {
			if (value != null && value.trim().length() > 0) {
				try {
					Constructor<?> constr = clz.getConstructor(String.class);
					return (T) constr.newInstance(value);
				} catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				return null;
			}
		}
	}
}
