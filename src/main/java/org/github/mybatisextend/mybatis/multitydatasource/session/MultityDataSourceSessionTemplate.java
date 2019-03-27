package org.github.mybatisextend.mybatis.multitydatasource.session;

import com.alibaba.druid.pool.DruidDataSource;
import com.asiainfo.springcloud.mybatis.autoconfigura.MultitybatisConfiguration;
import com.asiainfo.springcloud.mybatis.multitydatasource.annotation.MultityDataSource;
import com.asiainfo.springcloud.mybatis.multitydatasource.datasource.MultityDataSourceConf;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.*;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * Mybatis自定义多数据源Session
 * @author zhouyh3 on 2019/3/6.
 */
public class MultityDataSourceSessionTemplate extends SqlSessionTemplate {

    private static final Log LOGGER = LogFactory.getLog(MultityDataSourceSessionTemplate.class);

    private SqlSessionFactory sqlSessionFactory;
    private final ExecutorType executorType;
    private final SqlSession sqlSessionProxy;
    private final PersistenceExceptionTranslator exceptionTranslator;

    private Map<String, SqlSessionFactory> targetSessionFactoryRoute = new ConcurrentHashMap<>();
    private MultityDataSourceConf dataSourceConf;


    public MultityDataSourceSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
    }

    public MultityDataSourceSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        this(sqlSessionFactory, executorType, new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration()
                .getEnvironment().getDataSource(), true));
    }

    public MultityDataSourceSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
                                            PersistenceExceptionTranslator exceptionTranslator) {

        super(sqlSessionFactory, executorType, exceptionTranslator);

        Assert.notNull(sqlSessionFactory, "Property \'sqlSessionFactory\' is required");
        Assert.notNull(executorType, "Property \'executorType\' is required");
        this.sqlSessionFactory = sqlSessionFactory;
        this.executorType = executorType;
        this.exceptionTranslator = exceptionTranslator;

        this.sqlSessionProxy = (SqlSession) newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor());
    }

    @Override
    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    @Override
    public ExecutorType getExecutorType() {
        return executorType;
    }

    public SqlSession getSqlSessionProxy() {
        return sqlSessionProxy;
    }

    public Map<String, SqlSessionFactory> getTargetSessionFactoryRoute() {
        return targetSessionFactoryRoute;
    }

    public void setTargetSessionFactoryRoute(Map<String, SqlSessionFactory> targetSessionFactoryRoute) {
        this.targetSessionFactoryRoute = targetSessionFactoryRoute;
    }

    public MultityDataSourceConf getDataSourceConf() {
        return dataSourceConf;
    }

    public void setDataSourceConf(MultityDataSourceConf dataSourceConf) throws Exception {
        this.dataSourceConf = dataSourceConf;

//        DruidDataSource defaultDataSource = dataSourceConf.getDefaultDatasource();
//        Assert.notNull(defaultDataSource, "Default Datasource can't be NULL");
//        SqlSessionFactoryBean defaultSqlSessionFatactory = new SqlSessionFactoryBean();
//        defaultSqlSessionFatactory.setDataSource(defaultDataSource);
//        defaultSqlSessionFatactory.setConfiguration(MultitybatisConfiguration.getMultityConfigurationInvocationHandler().setEnvironment("default", defaultDataSource));
//        targetSessionFactoryRoute.put("default", defaultSqlSessionFatactory.getObject());
//        this.sqlSessionFactory = defaultSqlSessionFatactory.getObject();



        if (null != dataSourceConf) {
            List<DruidDataSource> multityDataSources = dataSourceConf.getDatasourceList();
            if (null != multityDataSources && 0 != multityDataSources.size()) {
                for (DruidDataSource dataSource : multityDataSources) {

                    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
                    sqlSessionFactoryBean.setDataSource(dataSource);
                    sqlSessionFactoryBean.setConfiguration((MultitybatisConfiguration.getMultityConfigurationInvocationHandler()).setEnvironment(dataSource.getName(),dataSource));
                    targetSessionFactoryRoute.put(dataSource.getName(), sqlSessionFactoryBean.getObject());
                }
            }
        }

        return;
    }


    @Override
    public <T> T selectOne(String statement) {
        return this.sqlSessionProxy.selectOne(statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return this.sqlSessionProxy.selectOne(statement, parameter);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return this.sqlSessionProxy.selectMap(statement, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return this.sqlSessionProxy.selectMap(statement, parameter, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectMap(statement, parameter, mapKey, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement) {
        return this.sqlSessionProxy.selectCursor(statement);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return this.sqlSessionProxy.selectCursor(statement, parameter);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectCursor(statement, parameter, rowBounds);
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return this.sqlSessionProxy.selectList(statement);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return this.sqlSessionProxy.selectList(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectList(statement, parameter, rowBounds);
    }

    @Override
    public void select(String statement, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, handler);
    }

    @Override
    public void select(String statement, Object parameter, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, handler);
    }

    @Override
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, rowBounds, handler);
    }

    @Override
    public int insert(String statement) {
        return this.sqlSessionProxy.insert(statement);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return this.sqlSessionProxy.insert(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return this.sqlSessionProxy.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return this.sqlSessionProxy.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return this.sqlSessionProxy.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return this.sqlSessionProxy.delete(statement, parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return this.getConfiguration().getMapper(type, this);
    }


    @Override
    public void clearCache() {
        this.sqlSessionProxy.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return this.sqlSessionFactory.getConfiguration();
    }

    @Override
    public Connection getConnection() {
        return this.sqlSessionProxy.getConnection();
    }

    @Override
    public List<BatchResult> flushStatements() {
        return this.sqlSessionProxy.flushStatements();
    }

    @Override
    public void destroy() throws Exception {
    }


    private class SqlSessionInterceptor implements InvocationHandler {
        private SqlSessionInterceptor() {
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            //按注解查找对应数据源  默认default
            //dao层类需要添加注解@MultityDataSource，注明使用的数据源和基础表名
            String originMethonName = (String) args[0];
            String originClassName = originMethonName.substring(0, originMethonName.lastIndexOf("."));
            Class originClass = Class.forName(originClassName);
            MultityDataSource originAnnotations = (MultityDataSource) originClass.getAnnotation(MultityDataSource.class);
            String dataSourceName = null;
            if (null != originAnnotations) {
                dataSourceName = originAnnotations.dataSourceName();
            }
            SqlSessionFactory targetSeesionFactory;
            if (null == dataSourceName || "default".endsWith(dataSourceName)) {
                targetSeesionFactory = MultityDataSourceSessionTemplate.this.sqlSessionFactory;
            } else {
                targetSeesionFactory = MultityDataSourceSessionTemplate.this.getTargetSessionFactoryRoute().get(dataSourceName);
                if (null == targetSeesionFactory) {
                    targetSeesionFactory = MultityDataSourceSessionTemplate.this.sqlSessionFactory;
                }
            }

            SqlSession sqlSession = SqlSessionUtils.getSqlSession(targetSeesionFactory,MultityDataSourceSessionTemplate.this.executorType,MultityDataSourceSessionTemplate.this.exceptionTranslator);


            Object unwrapped;
            try {
                Object t = method.invoke(sqlSession, args);
                if (!SqlSessionUtils.isSqlSessionTransactional(sqlSession, targetSeesionFactory)) {
                    sqlSession.commit(true);
                }

                unwrapped = t;
            } catch (Throwable var11) {
                unwrapped = ExceptionUtil.unwrapThrowable(var11);
                if (MultityDataSourceSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
                    SqlSessionUtils.closeSqlSession(sqlSession, targetSeesionFactory);
                    sqlSession = null;
                    DataAccessException translated = MultityDataSourceSessionTemplate.this.exceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
                    if (translated != null) {
                        unwrapped = translated;
                    }
                }

                throw (Throwable) unwrapped;
            } finally {
                if (sqlSession != null) {
                    SqlSessionUtils.closeSqlSession(sqlSession, targetSeesionFactory);
                }

            }

            return unwrapped;
        }
    }
}
