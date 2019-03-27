package org.github.mybatisextend.mybatis.tablesplit;

/**
 * @TODO
 * 注解了分表注解则按注解方式分表，
 * 可以指定不分表
 * 可以指定分表策略实现
 * @author zhouyh3 on 2019/3/13.
 */
public @interface TableSplitStrategy {
    boolean isTableSplit() default true;
    Class<? extends Strategy> splitStrategy();
}
