package org.github.mybatisextend.mybatis.multitydatasource.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zhouyh3 on 2019/3/7.
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MultityDataSource {

    @AliasFor("dataSourceName")
    String value() default "default";

    //选择数据源名称   默认为default
    @AliasFor("value")
    String dataSourceName() default "default";
    
}
