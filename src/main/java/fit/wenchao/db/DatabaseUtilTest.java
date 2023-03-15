package fit.wenchao.db;

import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DatabaseUtilTest {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:33061/table-generate?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "wc123456";

    private static final String SQL = "SELECT * FROM ";// 数据库操作

    static {
        try {
            Class.forName(DRIVER);
        }
        catch (ClassNotFoundException e) {
            log.error("can not load jdbc driver", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        }
        catch (SQLException e) {
            log.error("get connection failure", e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                log.error("close connection failure", e);
            }
        }
    }

    public static void main(String[] args) {
        var tables = getTables();

        for (Table table : tables) {
            System.out.println("tablename: " + table.getName());
            System.out.println(" === table attrs === :");
            for (TableAttr tableAttr : table) {
                System.out.println(tableAttr.getName() + " - " + tableAttr.getType() + " - " + tableAttr.isPri() + " - " + tableAttr.isIncre() + " - " + tableAttr.getComment());
            }

            //System.out.println(new File(".").getAbsolutePath());
            JavaSourceFile poSourceFile = table.generateJavaModel(JavaPackage.from("fit/wenchao/db/dao/po"));
            JavaSourceFile mapperSourceFile = table.generateMybatisMapper(JavaPackage.from("fit/wenchao/db/dao/mapper"),
                    poSourceFile);
            JavaSourceFile daoSourceFile = table.generateDao(JavaPackage.from("fit/wenchao/db/dao/repo"), poSourceFile);
            table.generateDaoImpl(JavaPackage.from("fit/wenchao/db/dao/repo/impl"),
                    mapperSourceFile,
                    poSourceFile,
                    daoSourceFile);

            JavaSourceFile serviceSourceFile = table.generateService(JavaPackage.from("fit/wenchao/db/service"));
            table.generateServiceImpl(JavaPackage.from("fit/wenchao/db/service/impl"), serviceSourceFile);

        }
    }

    private static List<Table> getTables() {
        var tables = new ArrayList<Table>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                Table table = new Table();
                String tableName = rs.getString(3);
                table.setName(tableName);
                var attrs = TableAttr.fromTable(conn, table);
                table.setAttrs(attrs);
                tables.add(table);
            }

        }
        catch (SQLException e) {
            log.error("getTableNames failure", e);
        }
        finally {
            try {
                rs.close();
                closeConnection(conn);
            }
            catch (SQLException e) {
                log.error("close ResultSet failure", e);
            }
        }
        return tables;

    }
}