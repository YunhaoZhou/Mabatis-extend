package org.github.mybatisextend.mybatis.configuration;


import org.github.mybatisextend.mybatis.crud.CrudInterceptor;
import org.github.mybatisextend.mybatis.tablesplit.Strategy;
import org.github.mybatisextend.mybatis.tablesplit.TableSplitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouyh3 on 2019/3/14.
 */
@Configuration
public class InterceptorConfiguration {
    /**
     * ORACLE
     * MYSQL
     * DB2
     * PGSQL
     */
    String dbType;

    /**
     * 分表拦截器
     * @return
     * @throws Exception
     */
    @Bean
    public TableSplitInterceptor getTabelSplitInterceptor()throws Exception{
        Strategy strategy = (Strategy)Class.forName(strategyClassName).newInstance();
        return new TableSplitInterceptor(dbType,strategy);
    }

    /**
     * 基础CRUD拦截器
     * @return
     * @throws Exception
     */
    @Bean
    public CrudInterceptor getCrudInterceptor()throws Exception{
        return new CrudInterceptor();
    }
}
