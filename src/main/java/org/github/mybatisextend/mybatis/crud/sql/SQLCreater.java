package org.github.mybatisextend.mybatis.crud.sql;


import com.asiainfo.springcloud.mybatis.crud.annotation.BaseMethod;
import com.asiainfo.springcloud.mybatis.tools.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouyh3 on 2019/3/25.
 */
public class SQLCreater {

    private static Constant.DBTYPE DBTYPE = Constant.DBTYPE.ORACLE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLCreater.class);

    private static final Map<Class,TableInfo> CachedTableInfo = new ConcurrentHashMap<>();

    public SQLCreater(){}

    public SQLCreater(Constant.DBTYPE dbtype){
        DBTYPE = dbtype;
    }

    public SQL createSQL(BaseMethod method,Map parameter)throws IllegalAccessException{
        Object entity = parameter.get("entity");
        int pageSize = Integer.MAX_VALUE ;
        int pageNum = 1;

        if(BaseMethod.PagedQuery == method){
            pageSize = (int)parameter.get("pageSize") ;
            pageNum = (int)parameter.get("pageNum");
        }

        switch (method){
            case Query:
                return Query(entity);
            case PagedQuery:
                return PagedQuery(entity,pageSize,pageNum);
            case Insert:
                return Insert(entity);
            case BatchInsert:
                return BatchInsert(entity);
            case Delete:
                return Delete(entity);
            case BatchDelete:
                return BatchDelete(entity);
        }

        return null;
    }

    public TableInfo analyzeTable(Object tableEntity,TableInfo tableInfo){
        Class<?> entityClass = tableEntity.getClass();
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if(null == tableAnnotation){
            //实体没有注解table
            throw new NullPointerException(tableEntity.getClass() + " should have annotation like @Table");
        }

        if(null == tableAnnotation.name() || "".equals(tableAnnotation.name())) {
            String entityTableName = entityClass.getSimpleName();
            //驼峰命名改下划线
            StringBuffer realTableName = new StringBuffer(entityTableName);
            char[] tableNameChars = entityTableName.toCharArray();
            if (1 < tableNameChars.length) {
                for (int i = tableNameChars.length - 1; i >= 1; i--) {
                    if (Character.isUpperCase(tableNameChars[i])) {
                        realTableName.insert(i, "_");
                    }
                }
            }
            tableInfo.setTableName(realTableName.toString());
        }else{
            tableInfo.setTableName(tableAnnotation.name());
        }

        return tableInfo;
    }

    private void analyzeTableColumns(Object tableEntity,TableInfo tableInfo){
        for(Field field : tableEntity.getClass().getDeclaredFields()){
            Column column = field.getDeclaredAnnotation(Column.class);
            Id id = field.getDeclaredAnnotation(Id.class);
            if(null != column || null != id){
                String columnName = field.getName();
                if(null != column.name() && !"".equals(column.name())){
                    columnName = column.name();
                }
                if(null != id){
                    tableInfo.setIdColumnName(columnName);
                }
                field.setAccessible(true);
                tableInfo.addColumn(columnName,field);
            }
        }
    }

    public SQL Query(Object entity) throws IllegalAccessException {
        SQL sqlObject = new SQL();
        StringBuilder builder = new StringBuilder("SELECT ");
        TableInfo tableInfo = CachedTableInfo.get(entity.getClass());
        if(null == tableInfo){
            //缓存未命中
            tableInfo = new TableInfo();
            analyzeTable(entity,tableInfo);
            analyzeTableColumns(entity,tableInfo);

            CachedTableInfo.put(entity.getClass(),tableInfo);
        }

        StringBuilder colBuilder = new StringBuilder();
        for(String columnName : tableInfo.getColumns()){
            colBuilder.append(columnName);
            colBuilder.append(",");
        }
        builder.append(colBuilder.substring(0, colBuilder.length() - 1));

        builder.append(" FROM ");
        builder.append(tableInfo.getTableName());
        builder.append(" WHERE 1=1 ");

        for(Map.Entry<String,Field> colnumField : tableInfo.getColumnFields().entrySet()){
            Object val = colnumField.getValue().get(entity);
            if(null != val) {
                builder.append(" AND ");
                builder.append(colnumField.getKey());
                builder.append(" = #{");
                builder.append(colnumField.getValue().getName());
                builder.append("} ");

                sqlObject.addParameter(colnumField.getValue().getName(),val);
            }
        }

        sqlObject.setSql(builder.toString());


        return sqlObject;
    }

    public SQL PagedQuery(Object entity,int pageSize, int pageNum ){
        return null;
    }

    public SQL Insert(Object entity){
        SQL sqlObject = new SQL();
        sqlObject.addParameter("name","yun");
        sqlObject.addParameter("id","aaaaa");
        sqlObject.setSql("insert into demo_Emplyee(id,name) values(#{id},#{name})");
        return sqlObject;
    }

    public SQL BatchInsert(Object entity){
        return null;
    }

    public SQL Delete(Object entity){
        return null;
    }

    public SQL BatchDelete(Object entities){
        return null;
    }

    public SQL Update(Object entity){
        return null;
    }


    public static class SQL{
        String sql;
        Map<String,Object> parametersMap = new HashMap<>();

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Map<String, Object> getParametersMap() {
            return parametersMap;
        }

        public void setParametersMap(Map<String, Object> parametersMap) {
            this.parametersMap = parametersMap;
        }

        public void addParameter(String name,Object val){
            this.parametersMap.put(name,val);
        }
    }


    public static class TableInfo{
        private String tableName;
        private String idColumnName;
        List<String> columns = new ArrayList<>();
        Map<String,Field> columnFields = new ConcurrentHashMap<>();

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getIdColumnName() {
            return idColumnName;
        }

        public void setIdColumnName(String idColumnName) {
            this.idColumnName = idColumnName;
        }

        public List<String> getColumns() {
            return columns;
        }


        public Map<String, Field> getColumnFields() {
            return columnFields;
        }


        synchronized public void addColumn(String columnName, Field columnField){
            this.columnFields.put(columnName,columnField);
            if(!this.columns.contains(columnName)) {
                this.columns.add(columnName);
            }
        }
    }


}
