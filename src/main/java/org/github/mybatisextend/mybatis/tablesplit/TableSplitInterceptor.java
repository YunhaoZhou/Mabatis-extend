package org.github.mybatisextend.mybatis.tablesplit;

import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.asiainfo.springcloud.mybatis.tools.DruidSqlParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.*;

/**
 * @author zhouyh3 on 2019/3/12.
 */
@Intercepts({
        @Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class,Integer.class}),
})
public class TableSplitInterceptor implements Interceptor {

    private DruidSqlParser sqlParser;

    private Strategy strategy;

    private static final Log LOGGER = LogFactory.getLog(TableSplitInterceptor.class);

    public TableSplitInterceptor(String dbType,Strategy strategy){
        this.sqlParser = new DruidSqlParser(dbType);
        this.strategy = strategy;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, reflectorFactory);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = statementHandler.getBoundSql();

        /*@TODO:
        //根据类调用类的注解判断是否分表和使用那种分表策略
        String id = mappedStatement.getId();
        String className = id.substring(0,id.lastIndexOf("."));
        String className = id.substring(id.lastIndexOf(".")+1,clasName.length());
        Class cls = Class.forName(className);
        */


        String sql = boundSql.getSql();

        if(null == strategy){
            throw new NullPointerException("分表策略未实现");
        }


        // 使用visitor来访问AST
        SchemaStatVisitor visitor = sqlParser.parser(sql);
        // 从visitor中拿出你所关注的信息
        Map<TableStat.Name, TableStat> tableStatMap = visitor.getTables();
        List<TableStat.Condition> conditions = visitor.getConditions();
        //表名：列：value
        Map<String,Map<String,Object>> tableConditions = new LinkedHashMap<>(tableStatMap.size());


        if(null != tableStatMap && !tableStatMap.isEmpty() && null != conditions && !conditions.isEmpty()){
            for(Map.Entry<TableStat.Name,TableStat> tables : tableStatMap.entrySet()){
                tableConditions.put(tables.getKey().getName(),new HashMap<>(16));
            }

            //加入条件字段和值
            for(TableStat.Condition condition : conditions){
                List<Object> values = condition.getValues();
                if(values.size() > 0){
                    if("=".equals(condition.getOperator())) {
                        TableStat.Column column = condition.getColumn();
                        //values数量大于1时，取第一个
                        Map<String, Object> c = tableConditions.get(column.getTable());
                        if (null != c) {
                            c.put(column.getName().toUpperCase(), values.get(0));
                        }
                    }
                }
            }
        }

        //每张表做一次分表计算
        for(Map.Entry<String,Map<String,Object>> tableSplitCondition : tableConditions.entrySet()){
            String newTableName = strategy.splitTable(tableSplitCondition.getKey().toUpperCase(),tableSplitCondition.getValue());
            sql = sql.replaceAll(tableSplitCondition.getKey(),newTableName);
        }

        //替换原始SQL
        metaObject.setValue("delegate.boundSql.sql", sql);

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("分表后SQL:" + sql);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
