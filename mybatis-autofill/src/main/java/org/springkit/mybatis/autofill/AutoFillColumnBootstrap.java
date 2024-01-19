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
package org.springkit.mybatis.autofill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.LOWEST_PRECEDENCE)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class AutoFillColumnBootstrap implements ApplicationListener<ContextRefreshedEvent> {

	static final ConcurrentMap<SqlCommandType, List<AutoFillColumnValueResolve>> COL_RESOLVE_LIST = new ConcurrentHashMap<>();

	@Bean
	@ConditionalOnMissingBean
	public AutoFillUpdateInterceptor register() {
		final AutoFillUpdateInterceptor plugin = new AutoFillUpdateInterceptor();
		return plugin;
	}

	public AutoFillColumnValueResolve creatorAutoColumnBean() {
		return new AutoFillColumnValueResolve() {
			@Override
			public SqlCommandType[] supporSqlType() {
				return new SqlCommandType[] { SqlCommandType.INSERT };
			}

			@Override
			public String columnName() {
				return "creator";
			}

			@Override
			public Object resolve(final SqlCommandType sct, final String table) {
				return CurrentContextHolder.get();
			}

			@Override
			public boolean support(final SqlCommandType sct, final String table) {
				return true;
			}

		};
	}

	public AutoFillColumnValueResolve updaterAutoColumnBean() {
		return new AutoFillColumnValueResolve() {
			@Override
			public SqlCommandType[] supporSqlType() {
				return new SqlCommandType[] { SqlCommandType.UPDATE };
			}

			@Override
			public String columnName() {
				return "updater";
			}

			@Override
			public Object resolve(final SqlCommandType sct, final String table) {
				return CurrentContextHolder.get();
			}

			@Override
			public boolean support(final SqlCommandType sct, final String table) {
				return true;
			}

		};
	}

	private void registerValueResolveBean(final ConfigurableListableBeanFactory beanFactory) {

		String[] beans;
		try {
			beans = beanFactory.getBeanNamesForType(AutoFillColumnValueResolve.class);
		} catch (NoSuchBeanDefinitionException e) {
			beans = null;
		}

		if (beans == null || beans.length == 0) {
			{
				final AutoFillColumnValueResolve resolveBean = this.creatorAutoColumnBean();
				beanFactory.registerSingleton("mybatis.autofill." + resolveBean.columnName(), resolveBean);

				System.out.println("register inner default ValueResolve[creator]");
			}

			{
				final AutoFillColumnValueResolve resolveBean = this.updaterAutoColumnBean();
				beanFactory.registerSingleton("mybatis.autofill." + resolveBean.columnName(), resolveBean);

				System.out.println("register inner default ValueResolve[updater]");
			}
		}
	}

	private void registerMybatisInterceptorBean(final ConfigurableListableBeanFactory beanFactory) {
		AutoFillUpdateInterceptor bean;
		try {
			bean = beanFactory.getBean(AutoFillUpdateInterceptor.class);
		} catch (NoSuchBeanDefinitionException e) {
			bean = null;
		}
		if (bean == null) {
			final AutoFillUpdateInterceptor resolveBean = this.register();
			beanFactory.registerSingleton(resolveBean.getClass().getSimpleName(), resolveBean);
		}

		try {
			Map<String, SqlSessionFactory> beans = beanFactory.getBeansOfType(SqlSessionFactory.class);
			if (beans != null && !beans.isEmpty()) {
				for (SqlSessionFactory session : beans.values()) {
					List<Interceptor> plugins = session.getConfiguration().getInterceptors();

					boolean find = false;
					for (Interceptor plugin : plugins) {
						if (plugin instanceof AutoFillUpdateInterceptor) {
							find = true;
							break;
						}
					}
					if (!find) {
						session.getConfiguration().addInterceptor(bean);
					}
				}
			}

		} catch (NoSuchBeanDefinitionException e) {

		}
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {

		AutowireCapableBeanFactory bean = event.getApplicationContext().getAutowireCapableBeanFactory();
		if (!ConfigurableListableBeanFactory.class.isAssignableFrom(bean.getClass())) {
			System.err.println("mybatis-autofill can't boot!!");
			return;
		}

		final ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) event.getApplicationContext().getAutowireCapableBeanFactory();

		this.registerMybatisInterceptorBean(beanFactory);
		this.registerValueResolveBean(beanFactory);

		final Map<String, AutoFillColumnValueResolve> map = event.getApplicationContext().getBeansOfType(AutoFillColumnValueResolve.class);

		for (final AutoFillColumnValueResolve r : map.values()) {
			final SqlCommandType[] sctList = r.supporSqlType();
			if (sctList != null) {
				for (final SqlCommandType sct : sctList) {
					List<AutoFillColumnValueResolve> list;
					if (sct != null) {
						if (!COL_RESOLVE_LIST.containsKey(sct)) {
							list = new ArrayList<AutoFillColumnValueResolve>();
							COL_RESOLVE_LIST.put(sct, list);
						} else {
							list = COL_RESOLVE_LIST.get(sct);
						}
						list.add(r);
					}
				}
			}
		}
	}

}
