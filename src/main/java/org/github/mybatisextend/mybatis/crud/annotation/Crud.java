package org.github.mybatisextend.mybatis.crud.annotation;

import java.lang.annotation.*;

/**
 * @author zhouyh3 on 2019/3/24.
 * 需要让Mybatis可以扫描
 * 即需要在方法上同时加上 @Select 或 @Insert 。。。
 * SQL留空即可
 * SQL会在CrudInterceptor中拼装
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Crud {
    BaseMethod value() default BaseMethod.Query;
}
