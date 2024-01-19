package org.springkit.mybatisplus.wquery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springkit.kits.test.SpringKitTestStart;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { SpringKitTestStart.class })
@ActiveProfiles("mybatis")
public class WebQueryTest {

	@Resource
	private ApplicationContext ctx;

	@Resource
	private DataSource dataSource;

	@Autowired
	private ServletWebServerApplicationContext server;

	private static volatile boolean init = false;

	private void initDatabase() throws ScriptException, SQLException {
		org.springframework.core.io.Resource initSql = ctx.getResource("classpath:h2/mybatisplus-init.sql");

		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, initSql);
			conn.commit();
		}
	}

	@Before
	public void setUp() throws ScriptException, SQLException {
		if (!init) {
			this.initDatabase();
			init = true;
		}
	}

	@Test
	public void testQuery1() throws IOException, InterruptedException {

		int port = server.getWebServer().getPort();
		String path = server.getServletContext().getContextPath();

		URL url = new URL("http://127.0.0.1:" + port + path + "/test/webquery1?id=1&col1=%E4%B8%AD%E5%9B%BD");

		URLConnection conn = url.openConnection();

		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) conn;

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {

				httpConn.setDoInput(true);
				httpConn.setDoOutput(true);
				httpConn.connect();

				try (InputStream in = httpConn.getInputStream()) {
					if (in != null) {
						while (in.available() > 0) {
							byte[] buf = new byte[1024];
							int l = in.read(buf);

							out.write(buf, 0, l);
						}
					}
				}

			} finally {
				httpConn.disconnect();
			}

			String resp = out.toString(StandardCharsets.UTF_8.name());

			System.out.println(resp);

			Assert.assertEquals("1, 中国, test1, testholder1, null\n", resp);

		}
	}

	@Test
	public void testQuery2() throws IOException, InterruptedException {

		int port = server.getWebServer().getPort();
		String path = server.getServletContext().getContextPath();

		URL url = new URL("http://127.0.0.1:" + port + path + "/test/webquery1?col1=test2&id=2");

		URLConnection conn = url.openConnection();

		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) conn;

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {

				httpConn.setDoInput(true);
				httpConn.setDoOutput(true);
				httpConn.connect();

				try (InputStream in = httpConn.getInputStream()) {
					if (in != null) {
						while (in.available() > 0) {
							byte[] buf = new byte[1024];
							int l = in.read(buf);

							out.write(buf, 0, l);
						}
					}
				}

			} finally {
				httpConn.disconnect();
			}

			String resp = out.toString(StandardCharsets.UTF_8.name());

			System.out.println(resp);

			Assert.assertEquals("2, test2, test2, testholder2, null\n", resp);

		}

	}
}
