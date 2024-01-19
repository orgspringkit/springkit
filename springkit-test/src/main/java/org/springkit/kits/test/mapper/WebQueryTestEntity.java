package org.springkit.kits.test.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("web_query_test")
public class WebQueryTestEntity {
	@TableId(type = IdType.AUTO)
	private Integer id;

	private String col1;

	private String col2;

	private String creator;

	private String updater;
}
