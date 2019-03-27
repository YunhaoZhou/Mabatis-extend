package org.github.mybatisextend.mybatis.tools;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

/**
 * @author zhouyh3 on 2019/3/13.
 */
public class DruidSqlParser {

    private String dbType;

    public DruidSqlParser(String dbType){
        this.dbType = dbType;
    }
    /**
     * 调用Druid的sql解析器解析sql
     * @param sql
     * @return  MySqlSchemaStatVisitor对象
     * @throws Exception
     */
    public SchemaStatVisitor parser(String sql)throws Exception{
        SQLStatementParser parser;
        SchemaStatVisitor visitor;
        switch (dbType){
            case "MYSQL":
                parser = new MySqlStatementParser(sql);visitor = new MySqlSchemaStatVisitor();break;
            case "DB2":
                parser = new DB2StatementParser(sql);visitor = new DB2SchemaStatVisitor();break;
            case "ODPS":
                parser = new OdpsStatementParser(sql);visitor = new OdpsSchemaStatVisitor();break;
            case "PGSQL":
                parser = new PGSQLStatementParser(sql);visitor = new PGSchemaStatVisitor();break;
            default:
                parser = new OracleStatementParser(sql);visitor = new OracleSchemaStatVisitor();break;
        }


        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        statement.accept(visitor);

        return visitor;
    }
}
