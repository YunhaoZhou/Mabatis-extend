package com.asiainfo.springcloud.mybatis.dao;

import org.apache.ibatis.annotations.Update;
import org.github.mybatisextend.mybatis.crud.annotation.BaseMethod;
import org.github.mybatisextend.mybatis.crud.annotation.Crud;
import org.github.mybatisextend.mybatis.multitydatasource.annotation.MultityDataSource;
import org.apache.ibatis.annotations.Select;

@MultityDataSource(dataSourceName = "xxxx")
public interface ExampleDao {

    @Crud(BaseMethod.Query)
    @Select("")
    public xxx getXxx(@Param("entity")Entity entity);

    @Crud(BaseMethod.Update)
    @Update("")
    public xxx updateXxx(@Param("entity")Entity entity);
}
