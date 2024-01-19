package org.springkit.mybatisplus.wquery;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springkit.mybatisplus.wquery.WebQueryParamParser.LambdaQueryWrapperParser;
import org.springkit.mybatisplus.wquery.WebQueryParamParser.QueryWrapperParser;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

public class WebQueryResolver implements HandlerMethodArgumentResolver, DisposableBean {

	private final org.apache.ibatis.logging.Log log = org.apache.ibatis.logging.LogFactory.getLog(WebQueryResolver.class);

	final static String SPARATOR_COMMA = ",";

	final static String SORT_ASC_PARAM_NAME = "sort";
	final static String SORT_DESC_PARAM_NAME = "sort-desc";

	final static Class<?>[] SUPPORT_CLASS;

	final LambdaQueryWrapperParser lambdaParser = new LambdaQueryWrapperParser();
	final QueryWrapperParser parser = new QueryWrapperParser();

	static {
		SUPPORT_CLASS = new Class<?>[3];
		SUPPORT_CLASS[0] = QueryWrapper.class;
		SUPPORT_CLASS[1] = LambdaQueryWrapper.class;
		SUPPORT_CLASS[2] = IPage.class;
	}

	@Override
	public void destroy() throws Exception {
		lambdaParser.destroy();
		parser.destroy();
	}

	@Override
	protected void finalize() {
		try {
			this.destroy();
		} catch (Exception e) {

		}
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		boolean isGetMapping = false;

		if (parameter.getMethod().getAnnotation(GetMapping.class) == null) {
			RequestMapping ann2 = parameter.getMethod().getAnnotation(RequestMapping.class);
			if (ann2 != null && Arrays.asList(ann2.method()).contains(RequestMethod.GET)) {
				isGetMapping = true;
			}
		} else {
			isGetMapping = true;
		}

		if (isGetMapping) {
			for (Class<?> clz : SUPPORT_CLASS) {
				if (clz.isInterface() && clz.isAssignableFrom(parameter.getParameterType())) {
					return true;
				} else if (parameter.getParameterType() == clz) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}

	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		if (parameter.getParameterType() == LambdaQueryWrapper.class) {
			return convertQueryByLambda(parameter, webRequest);
		} else if (parameter.getParameterType() == QueryWrapper.class) {
			return convertQuery(parameter, webRequest);
		} else if (IPage.class.isAssignableFrom(parameter.getParameterType())) {
			return convertPagination(parameter, webRequest);
		} else {
			return null;
		}

	}

	private IPage<?> convertPagination(MethodParameter parameter, NativeWebRequest webRequest) {
		try {
			IPage<?> pageWrapper = (IPage<?>) parameter.getParameterType().newInstance();

			Integer page = Integer.parseInt(Optional.ofNullable(webRequest.getParameter("page")).orElse("1"));
			Integer size = Integer.parseInt(Optional.ofNullable(webRequest.getParameter("size")).orElse("20"));

			pageWrapper.setCurrent(page < 1 ? 1 : page);
			pageWrapper.setSize(size <= 0 ? 20 : size);

			return pageWrapper;
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private LambdaQueryWrapper<?> convertQueryByLambda(MethodParameter parameter, NativeWebRequest webRequest) {
		LambdaQueryWrapper<?> query = new LambdaQueryWrapper<>();

		this.lambdaParser.convertQuery(query, parameter, webRequest);
		return query;
	}

	private QueryWrapper<?> convertQuery(MethodParameter parameter, NativeWebRequest webRequest) {
		QueryWrapper<?> query = new QueryWrapper<>();
		
		this.parser.convertQuery(query, parameter, webRequest);
		return query;
	}

}
