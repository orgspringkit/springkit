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
