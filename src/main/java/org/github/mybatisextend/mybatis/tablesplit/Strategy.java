package org.github.mybatisextend.mybatis.tablesplit;


import java.util.Map;

/**
 * 分表策略接口
 * @author zhouyh3 on 2019/3/8.
 */
public interface Strategy {

    /**
     * 分表逻辑实现
     *
     * @param tableName  原始表名(大写)
     * @param paramMap   参数列表(字段名大写)
     * @return  新表名
     */
    String splitTable(String tableName, Map<String, Object> paramMap);//{return tableName;}
}
