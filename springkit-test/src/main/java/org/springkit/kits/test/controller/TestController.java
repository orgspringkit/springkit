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
