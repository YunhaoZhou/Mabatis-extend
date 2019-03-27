package org.github.mybatisextend.mybatis.crud;

import org.github.mybatisextend.mybatis.crud.annotation.Crud;
import org.github.mybatisextend.mybatis.crud.sql.SQLCreater;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author zhouyh3 on 2019/3/22.
 */
@Intercepts({
        @Signature(type=Executor.class,method="query",args= {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class}),
        @Signature(type=Executor.class,method="query",args= {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type=Executor.class,method="update",args = {MappedStatement.class,Object.class})
})
public class CrudInterceptor implements Interceptor, Serializable {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        final MappedStatement mappedStatement = (MappedStatement)args[0];
        final Object parameters = args[1];

        String classMethodName = mappedStatement.getId();
        int lastOfPoint = classMethodName.lastIndexOf(".");
        String className = classMethodName.substring(0,lastOfPoint);
        String methodName = classMethodName.substring(lastOfPoint + 1);

        Class originClass = Class.forName(className);
        for(Method mehthod : originClass.getMethods()) {
            if (methodName.equals(mehthod.getName())) {
                Crud crudAnntation = mehthod.getAnnotation(Crud.class);
                if (null != crudAnntation) {
                    //拦截CRUD操作
                    Executor executor = (Executor) invocation.getTarget();

                    if (invocation.getMethod().getName().endsWith("query")) {
                        //query方法拦截
                        RowBounds rowBounds = (RowBounds) args[2];
                        ResultHandler resultHandler = (ResultHandler) args[3];
                        BoundSql boundSql;
                        CacheKey cacheKey;



                        if (args.length == 4) {
                            //4 个参数时
                            boundSql = mappedStatement.getBoundSql(parameters);
                            cacheKey = executor.createCacheKey(mappedStatement, parameters, rowBounds, boundSql);
                        } else {
                            //6 个参数时
                            cacheKey = (CacheKey) args[4];
                        }

                        //生成带参数的SQL
                        Map parameterMap;
                        if(parameters instanceof Map){
                            parameterMap = (Map)parameters;
                        }else{
                            parameterMap = new HashMap<>(1);
                            parameterMap.put("entity",parameters);
                        }
                        SQLCreater.SQL sqlObject = new SQLCreater().createSQL(crudAnntation.value(), parameterMap);

                        //Copy from "DynamicSqlSource.getBoundSql()";
                        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(mappedStatement.getConfiguration());
                        Map<String,Object> paramMap = new HashMap<>(2);
                        paramMap.put("_parameter",sqlObject.getParametersMap());
                        SqlSource sqlSource = sqlSourceParser.parse(sqlObject.getSql() , Map.class, paramMap);
                        BoundSql newBoundSql = sqlSource.getBoundSql(sqlObject.getParametersMap());
                        //Copy end

                        //继续调用query -- 6参数方法
                        return executor.query(mappedStatement, sqlObject.getParametersMap(), rowBounds, resultHandler, cacheKey, newBoundSql);
                    } else {
                        //update方法拦截
                        //生成带参数的SQL
                        Map parameterMap;
                        if(parameters instanceof Map){
                            parameterMap = (Map)parameters;
                        }else{
                            parameterMap = new HashMap<>(1);
                            parameterMap.put("entity",parameters);
                        }
                        SQLCreater.SQL sqlObject = new SQLCreater().createSQL(crudAnntation.value(), parameterMap);
                        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(mappedStatement.getConfiguration());
                        SqlSource sqlSource = sqlSourceParser.parse(sqlObject.getSql(), Map.class, sqlObject.getParametersMap());
                        MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(mappedStatement.getConfiguration(),mappedStatement.getId(),sqlSource,mappedStatement.getSqlCommandType());
                        MappedStatement newMappedStatement = mappedStatementBuilder.build();
                        return executor.update(newMappedStatement, sqlObject.getParametersMap());
                    }
                }
                break;
            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
