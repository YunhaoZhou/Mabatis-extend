package org.github.mybatisextend.mybatis.multitydatasource;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MethodResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zhouyh3 on 2019/3/7.
 */
public class Configuration extends org.apache.ibatis.session.Configuration {
    org.apache.ibatis.session.Configuration configuration;

    public Configuration(){
        this.configuration = new org.apache.ibatis.session.Configuration();
    }

    public Configuration(org.apache.ibatis.session.Configuration configuration){
        this.configuration = configuration;
    }

    public Configuration setEnvironment(String id,DataSource dataSource){
        super.setEnvironment(new Environment(id, configuration.getEnvironment().getTransactionFactory(), dataSource));
        return this;
    }

    @Override
    public void setEnvironment(Environment configenvironment) {
        //do nothing
    }

    public org.apache.ibatis.session.Configuration getConfiguration(){
        return this.configuration;
    }

    @Override
    public Environment getEnvironment(){
        if(null == super.getEnvironment()){
            return configuration.getEnvironment();
        }
        return super.getEnvironment();
    }

    public Cache getCache(String configstring){return configuration.getCache(configstring);}
    public void setAggressiveLazyLoading(boolean configboolean){configuration.setAggressiveLazyLoading(configboolean);}
    public boolean isAggressiveLazyLoading(){return configuration.isAggressiveLazyLoading();}
    public boolean isCallSettersOnNulls(){return configuration.isCallSettersOnNulls();}
    public void setSafeRowBoundsEnabled(boolean configboolean){configuration.setSafeRowBoundsEnabled(configboolean);}
    public boolean isMapUnderscoreToCamelCase(){return configuration.isMapUnderscoreToCamelCase();}
    public void setLazyLoadingEnabled(boolean configboolean){configuration.setLazyLoadingEnabled(configboolean);}
    public boolean isSafeResultHandlerEnabled(){return configuration.isSafeResultHandlerEnabled();}
    public void setConfigurationFactory(Class configclass){configuration.setConfigurationFactory(configclass);}
    public Class getConfigurationFactory(){return configuration.getConfigurationFactory();}
    public void setMapUnderscoreToCamelCase(boolean configboolean){configuration.setMapUnderscoreToCamelCase(configboolean);}
    public void setAutoMappingBehavior(AutoMappingBehavior configautomappingbehavior){configuration.setAutoMappingBehavior(configautomappingbehavior);}
    public void setUseActualParamName(boolean configboolean){configuration.setUseActualParamName(configboolean);}
    public void setCallSettersOnNulls(boolean configboolean){configuration.setCallSettersOnNulls(configboolean);}
    public boolean isSafeRowBoundsEnabled(){return configuration.isSafeRowBoundsEnabled();}
    public void setReturnInstanceForEmptyRow(boolean configboolean){configuration.setReturnInstanceForEmptyRow(configboolean);}
    public void setSafeResultHandlerEnabled(boolean configboolean){configuration.setSafeResultHandlerEnabled(configboolean);}
    public void addLoadedResource(String configstring){configuration.addLoadedResource(configstring);}
    public AutoMappingBehavior getAutoMappingBehavior(){return configuration.getAutoMappingBehavior();}
    public boolean isLazyLoadingEnabled(){return configuration.isLazyLoadingEnabled();}
    public boolean isUseActualParamName(){return configuration.isUseActualParamName();}
    public boolean isReturnInstanceForEmptyRow(){return configuration.isReturnInstanceForEmptyRow();}
    public boolean isUseColumnLabel(){return configuration.isUseColumnLabel();}
    public List getInterceptors(){return configuration.getInterceptors();}
    public void setDatabaseId(String configstring){configuration.setDatabaseId(configstring);}
    public boolean isResourceLoaded(String configstring){return configuration.isResourceLoaded(configstring);}
    public Properties getVariables(){return configuration.getVariables();}
    public void setCacheEnabled(boolean configboolean){configuration.setCacheEnabled(configboolean);}
    public void setLogPrefix(String configstring){configuration.setLogPrefix(configstring);}
    public void setVariables(Properties configproperties){configuration.setVariables(configproperties);}
    public ObjectFactory getObjectFactory(){return configuration.getObjectFactory();}
    public void setVfsImpl(Class configclass){configuration.setVfsImpl(configclass);}
    public ProxyFactory getProxyFactory(){return configuration.getProxyFactory();}
    public void setObjectFactory(ObjectFactory configobjectfactory){configuration.setObjectFactory(configobjectfactory);}
    public String getLogPrefix(){return configuration.getLogPrefix();}
    public Class getLogImpl(){return configuration.getLogImpl();}
    public void setLogImpl(Class configclass){configuration.setLogImpl(configclass);}
    public Class getVfsImpl(){return configuration.getVfsImpl();}
    public String getDatabaseId(){return configuration.getDatabaseId();}
    public void setProxyFactory(ProxyFactory configproxyfactory){configuration.setProxyFactory(configproxyfactory);}
    public boolean isCacheEnabled(){return configuration.isCacheEnabled();}
    public void addCacheRef(String configstring1,String configstring2){configuration.addCacheRef(configstring1,configstring2);}
    public void addInterceptor(Interceptor configinterceptor){configuration.addInterceptor(configinterceptor);}
    public boolean hasCache(String configstring){return configuration.hasCache(configstring);}
    public MetaObject newMetaObject(Object configobject){return configuration.newMetaObject(configobject);}
    public boolean hasResultMap(String configstring){return configuration.hasResultMap(configstring);}
    public boolean hasParameterMap(String configstring){return configuration.hasParameterMap(configstring);}
    public Object getMapper(Class configclass,SqlSession configsqlsession){return configuration.getMapper(configclass,configsqlsession);}
    public boolean hasMapper(Class configclass){return configuration.hasMapper(configclass);}
    public boolean hasKeyGenerator(String configstring){return configuration.hasKeyGenerator(configstring);}
    public void addParameterMap(ParameterMap configparametermap){configuration.addParameterMap(configparametermap);}
    public void addCache(Cache configcache){configuration.addCache(configcache);}
    public boolean hasStatement(String configstring){return configuration.hasStatement(configstring);}
    public boolean hasStatement(String configstring,boolean configboolean){return configuration.hasStatement(configstring,configboolean);}
    public void addResultMap(ResultMap configresultmap){configuration.addResultMap(configresultmap);}
    public ResultMap getResultMap(String configstring){return configuration.getResultMap(configstring);}
    public Executor newExecutor(Transaction configtransaction){return configuration.newExecutor(configtransaction);}
    public Executor newExecutor(Transaction configtransaction,ExecutorType configexecutortype){return configuration.newExecutor(configtransaction,configexecutortype);}
    public KeyGenerator getKeyGenerator(String configstring){return configuration.getKeyGenerator(configstring);}
    public Collection getResultMaps(){return configuration.getResultMaps();}
    public void addKeyGenerator(String configstring,KeyGenerator configkeygenerator){configuration.addKeyGenerator(configstring,configkeygenerator);}
    public Collection getParameterMaps(){return configuration.getParameterMaps();}
    public Collection getKeyGenerators(){return configuration.getKeyGenerators();}
    public Collection getCacheNames(){return configuration.getCacheNames();}
    public Collection getCaches(){return configuration.getCaches();}
    public ParameterMap getParameterMap(String configstring){return configuration.getParameterMap(configstring);}
    public Map getSqlFragments(){return configuration.getSqlFragments();}
    public void addMappers(String configstring,Class configclass){configuration.addMappers(configstring,configclass);}
    public void addMappers(String configstring){configuration.addMappers(configstring);}
    public void addMapper(Class configclass){configuration.addMapper(configclass);}
    public LanguageDriver getDefaultScriptingLanuageInstance(){return configuration.getDefaultScriptingLanuageInstance();}
    public LanguageDriver getDefaultScriptingLanguageInstance(){return configuration.getDefaultScriptingLanguageInstance();}
    public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior(){return configuration.getAutoMappingUnknownColumnBehavior();}
    public void setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior configautomappingunknowncolumnbehavior){configuration.setAutoMappingUnknownColumnBehavior(configautomappingunknowncolumnbehavior);}
    public void setDefaultFetchSize(Integer configinteger){configuration.setDefaultFetchSize(configinteger);}
    public void setDefaultScriptingLanguage(Class configclass){configuration.setDefaultScriptingLanguage(configclass);}
    public Integer getDefaultStatementTimeout(){return configuration.getDefaultStatementTimeout();}
    public JdbcType getJdbcTypeForNull(){return configuration.getJdbcTypeForNull();}
    public void setMultipleResultSetsEnabled(boolean configboolean){configuration.setMultipleResultSetsEnabled(configboolean);}
    public ObjectWrapperFactory getObjectWrapperFactory(){return configuration.getObjectWrapperFactory();}
    public void setDefaultStatementTimeout(Integer configinteger){configuration.setDefaultStatementTimeout(configinteger);}
    public LocalCacheScope getLocalCacheScope(){return configuration.getLocalCacheScope();}
    public void setObjectWrapperFactory(ObjectWrapperFactory configobjectwrapperfactory){configuration.setObjectWrapperFactory(configobjectwrapperfactory);}
    public ReflectorFactory getReflectorFactory(){return configuration.getReflectorFactory();}
    public ResultSetHandler newResultSetHandler(Executor configexecutor,MappedStatement configmappedstatement,RowBounds configrowbounds,ParameterHandler configparameterhandler,ResultHandler configresulthandler,BoundSql configboundsql){return configuration.newResultSetHandler(configexecutor,configmappedstatement,configrowbounds,configparameterhandler,configresulthandler,configboundsql);}
    public void setJdbcTypeForNull(JdbcType configjdbctype){configuration.setJdbcTypeForNull(configjdbctype);}
    public Collection getResultMapNames(){return configuration.getResultMapNames();}
    public Set getLazyLoadTriggerMethods(){return configuration.getLazyLoadTriggerMethods();}
    public void setUseGeneratedKeys(boolean configboolean){configuration.setUseGeneratedKeys(configboolean);}
    public void addIncompleteStatement(XMLStatementBuilder configxmlstatementbuilder){configuration.addIncompleteStatement(configxmlstatementbuilder);}
    public void addIncompleteCacheRef(CacheRefResolver configcacherefresolver){configuration.addIncompleteCacheRef(configcacherefresolver);}
    public boolean isUseGeneratedKeys(){return configuration.isUseGeneratedKeys();}
    public void setLazyLoadTriggerMethods(Set configset){configuration.setLazyLoadTriggerMethods(configset);}
    public Collection getIncompleteMethods(){return configuration.getIncompleteMethods();}
    public void addIncompleteMethod(MethodResolver configmethodresolver){configuration.addIncompleteMethod(configmethodresolver);}
    public void setLocalCacheScope(LocalCacheScope configlocalcachescope){configuration.setLocalCacheScope(configlocalcachescope);}
    public TypeHandlerRegistry getTypeHandlerRegistry(){return configuration.getTypeHandlerRegistry();}
    public TypeAliasRegistry getTypeAliasRegistry(){return configuration.getTypeAliasRegistry();}
    public Collection getParameterMapNames(){return configuration.getParameterMapNames();}
    public void addMappedStatement(MappedStatement configmappedstatement){configuration.addMappedStatement(configmappedstatement);}
    public StatementHandler newStatementHandler(Executor configexecutor,MappedStatement configmappedstatement,Object configobject,RowBounds configrowbounds,ResultHandler configresulthandler,BoundSql configboundsql){return configuration.newStatementHandler(configexecutor,configmappedstatement,configobject,configrowbounds,configresulthandler,configboundsql);}
    public Collection getKeyGeneratorNames(){return configuration.getKeyGeneratorNames();}
    public Collection getMappedStatementNames(){return configuration.getMappedStatementNames();}
    public Collection getMappedStatements(){return configuration.getMappedStatements();}
    public ExecutorType getDefaultExecutorType(){return configuration.getDefaultExecutorType();}
    public MappedStatement getMappedStatement(String configstring,boolean configboolean){return configuration.getMappedStatement(configstring,configboolean);}
    public MappedStatement getMappedStatement(String configstring){return configuration.getMappedStatement(configstring);}
    public LanguageDriverRegistry getLanguageRegistry(){return configuration.getLanguageRegistry();}
    public Integer getDefaultFetchSize(){return configuration.getDefaultFetchSize();}
    public boolean isMultipleResultSetsEnabled(){return configuration.isMultipleResultSetsEnabled();}
    public void setUseColumnLabel(boolean configboolean){configuration.setUseColumnLabel(configboolean);}
    public MapperRegistry getMapperRegistry(){return configuration.getMapperRegistry();}
    public ParameterHandler newParameterHandler(MappedStatement configmappedstatement,Object configobject,BoundSql configboundsql){return configuration.newParameterHandler(configmappedstatement,configobject,configboundsql);}
    public Collection getIncompleteCacheRefs(){return configuration.getIncompleteCacheRefs();}
    public Collection getIncompleteResultMaps(){return configuration.getIncompleteResultMaps();}
    public void addIncompleteResultMap(ResultMapResolver configresultmapresolver){configuration.addIncompleteResultMap(configresultmapresolver);}
    public void setReflectorFactory(ReflectorFactory configreflectorfactory){configuration.setReflectorFactory(configreflectorfactory);}
    public void setDefaultEnumTypeHandler(Class configclass){configuration.setDefaultEnumTypeHandler(configclass);}
    public Collection getIncompleteStatements(){return configuration.getIncompleteStatements();}
    public void setDefaultExecutorType(ExecutorType configexecutortype){configuration.setDefaultExecutorType(configexecutortype);}
    public boolean equals(Object configobject){return configuration.equals(configobject);}
    public String toString(){return configuration.toString();}
    public int hashCode(){return configuration.hashCode();}

    public static void main(String args[])throws Exception{
        Method[] methods = org.apache.ibatis.session.Configuration.class.getMethods();

        for(Method method : methods){
            System.out.print("public ");
            System.out.print(method.getReturnType().getSimpleName()+" ");
            System.out.print(method.getName()+"(");
            for(Class paramType : method.getParameterTypes()){
                System.out.print("," + paramType.getSimpleName() + " " + "config"+paramType.getSimpleName().toLowerCase());
            }
            System.out.print("){");
            if(!"void".equals(method.getReturnType().getSimpleName())){
                System.out.print("return ");
            }
            System.out.print("configuration."+method.getName()+"(");
            for(Class paramType : method.getParameterTypes()){
                System.out.print("," + "config"+paramType.getSimpleName().toLowerCase());
            }
            System.out.print(");}");
            System.out.println();
        }
    }
}
