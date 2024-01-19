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
package org.springkit.mybatisplus.wquery;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

abstract class WebQueryParamParser<T> implements DisposableBean {

	protected final org.apache.ibatis.logging.Log log = org.apache.ibatis.logging.LogFactory.getLog(this.getClass());

	protected final ConcurrentMap<Class<?>, Map<String, Field>> CLAZZ_CACHE = new ConcurrentHashMap<>();
	protected final ConcurrentMap<Class<? extends QueryValueResolver>, QueryValueResolver> QUERY_RESOLVER_CACHE = new ConcurrentHashMap<>();

	@Override
	public void destroy() throws Exception {
		CLAZZ_CACHE.clear();
		QUERY_RESOLVER_CACHE.clear();
	}

	public void convertQuery(T query, MethodParameter parameter, NativeWebRequest webRequest) {
		Class<?> entityClazz = null;
		Method pMethod = null;
		if (parameter.getExecutable() instanceof Method) {
			pMethod = (Method) parameter.getExecutable();

			List<Type> ptlist = Arrays.asList(pMethod.getGenericParameterTypes());

			Type queryType = ptlist.get(parameter.getParameterIndex());

			if (queryType instanceof ParameterizedType) {
				Type[] ts = ((ParameterizedType) queryType).getActualTypeArguments();
				entityClazz = (Class<?>) ts[0];

			}
		}

		if (entityClazz != null) {
			WebQuery ann = pMethod.getDeclaredAnnotation(WebQuery.class);
			Map<String, Field> fs = getFields(entityClazz);
			this.fillQuery(ann, fs, webRequest, query);
			this.fillLikeQuery(ann, fs, webRequest, query);
			this.fillSortAsc(ann, fs, webRequest, query);
			this.fillSortDesc(ann, fs, webRequest, query);

		}
	}

	private void fillQuery(WebQuery ann, Map<String, Field> fs, NativeWebRequest request, T query) {
		List<String> canQueryName;
		if (ann != null && ann.querys() != null && ann.querys().length > 0) {
			canQueryName = Arrays.asList(ann.querys()).stream().map(e -> {
				String a = e.toLowerCase();
				return a;
			}).collect(Collectors.toList());
		} else {
			canQueryName = Collections.emptyList();
		}
		for (Iterator<String> iter = request.getParameterNames(); iter.hasNext();) {
			String name = iter.next();
			Field f = fs.get(name.toLowerCase());
			if (f == null) {
				f = fs.get(resolveName(ann.value(), name).toLowerCase());
			}

			if (f != null && canQueryName.contains(name.toLowerCase())) {
				String value = request.getParameter(name);
				value = value == null ? null : value.trim();

				value = resolveValue(ann.value(), name, value);

				if (value == null || value.trim().length() == 0) {
					continue;
				}

				if (Number.class.isAssignableFrom(f.getType()) && value.matches("[\\-\\d\\+\\.]+")) {
					try {
						Constructor<?> con = f.getType().getDeclaredConstructor(String.class);
						if (con != null) {
							this.bindToEq(query, f, con.newInstance(value));
						} else {
							this.bindToEq(query, f, value);
						}

					} catch (ClassCastException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						log.error(e.getMessage(), e);
					}
				} else {
					this.bindToEq(query, f, value);
				}
			}
		}
	}

	private void fillLikeQuery(WebQuery ann, Map<String, Field> fs, NativeWebRequest request, T query) {
		List<String> canQueryName;
		if (ann != null && ann.likes() != null && ann.likes().length > 0) {
			canQueryName = Arrays.asList(ann.likes()).stream().map(e -> {
				String a = e.toLowerCase();
				return a;
			}).collect(Collectors.toList());
		} else {
			canQueryName = Collections.emptyList();
		}
		for (Iterator<String> iter = request.getParameterNames(); iter.hasNext();) {
			String name = iter.next();
			Field f = fs.get(name.toLowerCase());
			if (f == null) {
				String n = resolveName(ann.value(), name).toLowerCase();
				f = fs.get(n);
			}

			if (f != null && canQueryName.contains(name.toLowerCase())) {
				String value = request.getParameter(name);
				value = value == null ? null : value.trim();

				value = resolveValue(ann.value(), name, value);

				if (value == null || value.trim().length() == 0) {
					continue;
				}

				if (CharSequence.class.isAssignableFrom(f.getType())) {
					this.bindToLike(query, f, value);
				}
			}
		}

	}

	private void fillSortAsc(WebQuery ann, Map<String, Field> fs, NativeWebRequest request, T query) {
		List<String> sortNames;
		if (ann != null && ann.asc() != null && ann.asc().length > 0) {
			sortNames = Arrays.asList(ann.asc()).stream().map(e -> e.toLowerCase()).collect(Collectors.toList());
		} else {
			sortNames = Collections.emptyList();
		}
		String reqSort = request.getParameter(WebQueryResolver.SORT_ASC_PARAM_NAME);
		reqSort = reqSort == null ? null : reqSort.trim();

		if (!sortNames.isEmpty() && reqSort != null && reqSort.trim().length() > 0) {
			List<String> reqAsc = Arrays.asList(reqSort.split(WebQueryResolver.SPARATOR_COMMA)).stream().filter(e -> sortNames.contains(e.toLowerCase())).collect(Collectors.toList());
			if (!reqAsc.isEmpty()) {
				List<Field> sortlist = new ArrayList<>();
				for (String asc : reqAsc) {
					Field f = fs.get(asc);
					if (f != null) {
						sortlist.add(f);
					}
				}
				if (!sortlist.isEmpty()) {
					this.bindToAsc(query, sortlist);
				}
			}
		}
	}

	private void fillSortDesc(WebQuery ann, Map<String, Field> fs, NativeWebRequest request, T query) {
		List<String> sortNames;
		if (ann != null && ann.desc() != null && ann.desc().length > 0) {
			sortNames = Arrays.asList(ann.desc()).stream().map(e -> e.toLowerCase()).collect(Collectors.toList());
		} else {
			sortNames = Collections.emptyList();
		}
		String reqSort = request.getParameter(WebQueryResolver.SORT_DESC_PARAM_NAME);
		reqSort = reqSort == null ? null : reqSort.trim();

		if (!sortNames.isEmpty() && reqSort != null && reqSort.trim().length() > 0) {
			List<String> reqDesc = Arrays.asList(reqSort.split(WebQueryResolver.SPARATOR_COMMA)).stream().filter(e -> sortNames.contains(e.toLowerCase())).collect(Collectors.toList());

			if (!reqDesc.isEmpty()) {
				List<Field> sortlist = new ArrayList<>();
				for (String desc : reqDesc) {
					Field f = fs.get(desc);
					if (f != null) {
						sortlist.add(f);
					}
				}
				if (!sortlist.isEmpty()) {
					this.bindToDesc(query, sortlist);
				}

			}
		}
	}

	private String resolveName(QueryResolver[] resolvers, String name) {

		if (resolvers == null || resolvers.length == 0) {
			return name;
		}

		for (QueryResolver r : resolvers) {
			if (name.equalsIgnoreCase(r.name()) && r.inEntity() != null && r.inEntity().trim().length() > 0) {
				return r.inEntity();
			}
		}

		return name;
	}

	private String resolveValue(QueryResolver[] queryResolvers, String name, String value) {

		if (queryResolvers == null || queryResolvers.length == 0) {
			return value;
		}

		QueryResolver qr = null;
		for (QueryResolver r : queryResolvers) {
			if (name.equalsIgnoreCase(r.name())) {
				qr = r;
				break;
			}
		}

		if (qr != null && qr.value() != null && qr.value().length > 0) {
			String qvalue = value;
			for (Class<? extends QueryValueResolver> qvrClz : qr.value()) {

				QueryValueResolver qvr = QUERY_RESOLVER_CACHE.get(qvrClz);
				if (qvr == null) {
					try {
						qvr = qvrClz.newInstance();
						QUERY_RESOLVER_CACHE.putIfAbsent(qvrClz, qvr);

						if (log.isDebugEnabled()) {
							log.debug(String.format("%s add to cache", qvrClz.getName()));
						}

					} catch (InstantiationException | IllegalAccessException e) {
						log.error(e.getMessage(), e);
						qvr = null;
					}

				}
				if (qvr != null) {
					qvalue = qvr.resolve(name, qvalue);
					if (qvalue != null) {
						value = qvalue;
						break;
					}
				}
			}

		}

		return value;
	}

	private Map<String, Field> getFields(Class<?> clazz) {

		if (clazz == null) {
			return Collections.emptyMap();
		}

		Map<String, Field> fs = CLAZZ_CACHE.get(clazz);
		if (fs == null) {
			fs = new HashMap<>();
			for (Field f : clazz.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers())) {
					fs.put(f.getName().toLowerCase(), f);
				}
			}
			CLAZZ_CACHE.putIfAbsent(clazz, fs);

			if (log.isDebugEnabled()) {
				log.debug(String.format("%s add to reflect cache", clazz.getName()));
			}
		}

		return fs;
	}

	public abstract void bindToEq(T query, Field field, Object value);

	public abstract void bindToLike(T query, Field field, Object value);

	public abstract void bindToAsc(T query, List<Field> fields);

	public abstract void bindToDesc(T query, List<Field> fields);

	static class LambdaQueryWrapperParser extends WebQueryParamParser<LambdaQueryWrapper<?>> {

		final ConcurrentMap<String, SFunctionLambdaWrapper> LAMBDA_CACHE = new ConcurrentHashMap<>();

		@Override
		public void destroy() throws Exception {
			super.destroy();
			LAMBDA_CACHE.clear();
		}

		private SFunctionLambdaWrapper lambdaWrap(Field field) {
			String k = field.getDeclaringClass().getName() + "." + field.getName();

			SFunctionLambdaWrapper lambdaWrap;
			SFunctionLambdaWrapper older = null;

			if (!LAMBDA_CACHE.containsKey(k)) {
				lambdaWrap = new SFunctionLambdaWrapper(field);
				older = LAMBDA_CACHE.putIfAbsent(k, lambdaWrap);
			} else {
				lambdaWrap = LAMBDA_CACHE.get(k);
			}

			return older == null ? lambdaWrap : older;

		}

		@SuppressWarnings("unchecked")
		@Override
		public void bindToEq(LambdaQueryWrapper<?> query, Field field, Object value) {
			query.eq(lambdaWrap(field), value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void bindToLike(LambdaQueryWrapper<?> query, Field field, Object value) {
			query.like(lambdaWrap(field), value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void bindToAsc(LambdaQueryWrapper<?> query, List<Field> fields) {

			@SuppressWarnings("rawtypes")
			List sortlist = new ArrayList<>();
			for (Field f : fields) {
				sortlist.add(lambdaWrap(f));
			}

			if (!sortlist.isEmpty()) {
				query.orderBy(true, true, sortlist);
			}

		}

		@SuppressWarnings("unchecked")
		@Override
		public void bindToDesc(LambdaQueryWrapper<?> query, List<Field> fields) {

			@SuppressWarnings("rawtypes")
			List sortlist = new ArrayList<>();
			for (Field f : fields) {
				sortlist.add(lambdaWrap(f));
			}

			if (!sortlist.isEmpty()) {
				query.orderBy(true, false, sortlist);
			}

		}

	}

	static class QueryWrapperParser extends WebQueryParamParser<QueryWrapper<?>> {

		private String formatToTbColumn(Field f) {
			StringBuffer originalName = new StringBuffer(f.getName());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < originalName.length(); i++) {
				if (i == 0 || i == originalName.length() - 1) {
					sb.append(originalName.charAt(i));
				} else if (originalName.charAt(i) >= 'A' && originalName.charAt(i) <= 'Z') {
					sb.append("_").append(String.valueOf(originalName.charAt(i)).toLowerCase());
				} else {
					sb.append(originalName.charAt(i));
				}
			}

			return sb.toString();

		}

		@Override
		public void bindToEq(QueryWrapper<?> query, Field field, Object value) {
			query.eq(formatToTbColumn(field), value);
		}

		@Override
		public void bindToLike(QueryWrapper<?> query, Field field, Object value) {
			query.like(formatToTbColumn(field), value);
		}

		@Override
		public void bindToAsc(QueryWrapper<?> query, List<Field> fields) {

			List<String> sortlist = new ArrayList<>();
			for (Field f : fields) {
				sortlist.add(formatToTbColumn(f));
			}

			if (!sortlist.isEmpty()) {
				query.orderBy(true, true, sortlist);
			}

		}

		@Override
		public void bindToDesc(QueryWrapper<?> query, List<Field> fields) {

			List<String> sortlist = new ArrayList<>();
			for (Field f : fields) {
				sortlist.add(formatToTbColumn(f));
			}

			if (!sortlist.isEmpty()) {
				query.orderBy(true, false, sortlist);
			}

		}

	}
}
