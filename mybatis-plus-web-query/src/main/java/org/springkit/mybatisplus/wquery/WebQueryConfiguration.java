package org.springkit.mybatisplus.wquery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

public class WebQueryConfiguration implements WebMvcConfigurer, ApplicationListener<ContextRefreshedEvent> {
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		if (resolvers.isEmpty()) {
			resolvers.add(0, new WebQueryResolver());
		} else {
			resolvers.set(0, new WebQueryResolver());
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		final ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) event.getApplicationContext().getAutowireCapableBeanFactory();

		InitFactory.getIniter(beanFactory).init();

	}

	private static class InitFactory {

		public static MpPluginInit getIniter(ConfigurableListableBeanFactory beanFactory) {

			boolean load = false;
			try {
				WebQueryConfiguration.class.getClassLoader().loadClass("com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor");
				load = true;
			} catch (ClassNotFoundException e) {
			}

			if (load) {
				return new MpPluginIniter(beanFactory);
			} else {
				return new EmptyInit();
			}
		}
	}

	private static interface MpPluginInit {
		public void init();
	}

	private static class EmptyInit implements MpPluginInit {

		@Override
		public void init() {
		}

	}

	private static class MpPluginIniter implements MpPluginInit {
		private final ConfigurableListableBeanFactory beanFactory;

		public MpPluginIniter(ConfigurableListableBeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		@Override
		public void init() {
			try {
				Map<String, SqlSessionFactory> beans = beanFactory.getBeansOfType(SqlSessionFactory.class);
				if (beans != null && !beans.isEmpty()) {
					for (SqlSessionFactory session : beans.values()) {
						loadPainationPlug(session);
					}
				}

			} catch (NoSuchBeanDefinitionException e) {

			}
		}

		private void loadPainationPlug(SqlSessionFactory session) {
			MybatisPlusInterceptor mpPluginWrapper = findMpPluginWrapper(session);
			if (mpPluginWrapper == null) {
				mpPluginWrapper = new MybatisPlusInterceptor();
				session.getConfiguration().addInterceptor(mpPluginWrapper);
			}
			boolean find = false;
			for (InnerInterceptor mpPlugin : mpPluginWrapper.getInterceptors()) {
				if (mpPlugin instanceof PaginationInnerInterceptor) {
					find = true;
					break;
				}
			}
			if (!find) {
				mpPluginWrapper.addInnerInterceptor(createPlugin(session));
			}
		}

		private PaginationInnerInterceptor createPlugin(SqlSessionFactory session) {

			try (SqlSession sess = session.openSession(); Connection conn = sess.getConnection()) {
				String dbDriver = conn.getMetaData().getDatabaseProductName();
				if ("mysql".equalsIgnoreCase(dbDriver)) {
					return new PaginationInnerInterceptor(DbType.MYSQL);
				} else if ("h2".equalsIgnoreCase(dbDriver)) {
					return new PaginationInnerInterceptor(DbType.H2);
				} else {
					return new PaginationInnerInterceptor(DbType.MYSQL);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		private MybatisPlusInterceptor findMpPluginWrapper(SqlSessionFactory session) {
			List<Interceptor> plugins = session.getConfiguration().getInterceptors();
			for (Interceptor plugin : plugins) {
				if (plugin instanceof MybatisPlusInterceptor) {
					return (MybatisPlusInterceptor) plugin;
				}
			}

			return null;
		}

	}

}
