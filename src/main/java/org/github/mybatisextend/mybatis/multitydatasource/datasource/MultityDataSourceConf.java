package org.github.mybatisextend.mybatis.multitydatasource.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhouyh3 on 2019/3/11.
 */
@ConfigurationProperties("mybatis")
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@Component
public class MultityDataSourceConf {
    public List<DruidDataSource> datasourcelist = new LinkedList<>();

    public List<DruidDataSource> getDatasourceList() {
        return datasourcelist;
    }

    public void setDatasourcelist(List<DruidDataSource> datasourceList) {
        this.datasourcelist = datasourceList;
    }

}


