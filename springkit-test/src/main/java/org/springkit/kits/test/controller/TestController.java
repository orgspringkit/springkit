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
package org.springkit.kits.test.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springkit.kits.test.mapper.WebQueryTestEntity;
import org.springkit.kits.test.mapper.WebQueryTestMapper;
import org.springkit.mybatisplus.wquery.WebQuery;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RequestMapping("/test")
@RestController
public class TestController {

	@Resource
	private WebQueryTestMapper webQueryTestMapper;

	@GetMapping("/lambdaquery")
	@WebQuery(querys = { "id", "col1" })
	public String testWebQuery(LambdaQueryWrapper<WebQueryTestEntity> query, HttpServletRequest request) {

		List<WebQueryTestEntity> list = webQueryTestMapper.selectList(query);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		for (WebQueryTestEntity e : list) {
			pw.println(String.format("%s, %s, %s, %s, %s", e.getId(), e.getCol1(), e.getCol2(), e.getCreator(), e.getUpdater()));
			pw.flush();
		}

		return sw.toString();
	}

	@GetMapping("/query")
	@WebQuery(querys = { "id" })
	public String testWebQuery(QueryWrapper<WebQueryTestEntity> query, HttpServletRequest request) {

		List<WebQueryTestEntity> list = webQueryTestMapper.selectList(query);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		for (WebQueryTestEntity e : list) {
			pw.println(String.format("%s, %s, %s, %s, %s", e.getId(), e.getCol1(), e.getCol2(), e.getCreator(), e.getUpdater()));
			pw.flush();
		}

		return sw.toString();
	}

	@GetMapping("/query-paging")
	@WebQuery(querys = { "id" })
	public String testWebQueryPaging(Page<WebQueryTestEntity> paging, QueryWrapper<WebQueryTestEntity> query) {

		Page<WebQueryTestEntity> list = webQueryTestMapper.selectPage(paging, query);

		System.out.println("total=" + list.getTotal());

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		for (WebQueryTestEntity e : list.getRecords()) {
			pw.println(String.format("%s, %s, %s, %s, %s", e.getId(), e.getCol1(), e.getCol2(), e.getCreator(), e.getUpdater()));
			pw.flush();
		}

		return sw.toString();
	}

}
