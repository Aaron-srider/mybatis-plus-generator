package fit.wenchao.db;

import fit.wenchao.db.generator.Generator;
import lombok.var;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static fit.wenchao.db.dbConnection.DatabaseConnection.closeConnection;
import static fit.wenchao.db.dbConnection.DatabaseConnection.getConnection;


public class DatabaseUtilTest {

    private static final String SQL = "SELECT * FROM ";// 数据库操作


    public static void main(String[] args) {

        var tables = getTables();

        Generator.generate(tables);

    }

    private static List<Table> getTables() {
        var tables = new ArrayList<Table>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(null, "simple-codebase", null, new String[]{"TABLE"});
            while (rs.next()) {
                if(!"simple-codebase".equals(rs.getString(1))) {
                    continue;
                }
                Table table = new Table();
                String tableName = rs.getString(3);
                table.setName(tableName);
                var attrs = TableAttr.fromTable(conn, table);
                table.setAttrs(attrs);
                tables.add(table);
            }

        }
        catch (Exception e) {
            System.out.println("getTableNames failure " + e.getMessage());
        }
        finally {
            try {
                rs.close();
                closeConnection(conn);
            }
            catch (Exception e) {
                System.out.println("close ResultSet failure " + e.getMessage() );
            }
        }
        return tables;

    }
}