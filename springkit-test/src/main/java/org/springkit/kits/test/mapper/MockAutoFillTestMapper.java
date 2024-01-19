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
package org.springkit.kits.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

@Mapper
public interface MockAutoFillTestMapper {

	@SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
	@Insert("insert into auto_fill_test (col1,col2) values (#{col1}, #{col2})")
	public int insert(AutoFillTestEntity entity);

	@Select("select * from auto_fill_test")
	@ResultType(AutoFillTestEntity.class)
	public List<AutoFillTestEntity> select();
	
	
	@Select("select * from auto_fill_test where id= #{id}")
	@ResultType(AutoFillTestEntity.class)
	public AutoFillTestEntity selectOne(Integer id);

}
