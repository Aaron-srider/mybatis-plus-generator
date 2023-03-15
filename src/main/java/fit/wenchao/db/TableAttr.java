package fit.wenchao.db;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Attr;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static fit.wenchao.db.DatabaseUtilTest.closeConnection;

@Data
@Slf4j
public class TableAttr {

    String name;
    private String type;
    private boolean isPri;
    private boolean isIncre;

    String comment;

    private static final String SQL = "SELECT * FROM ";// 数据库操作

    public static List<TableAttr> fromTable(Connection conn, Table table) {
        List<String> columnNames = new ArrayList<>();
        List<TableAttr> tableAttrs = new ArrayList<>();
        //与数据库的连接
        PreparedStatement ps = null;
        String tableSql = SQL + table.getName();
        try {
            ps = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = ps.getMetaData();
            //表列数
            int tableColumnCount = rsmd.getColumnCount();
            for (int i = 0; i < tableColumnCount; i++) {
                String colName = rsmd.getColumnName(i + 1);
                String colType = rsmd.getColumnTypeName(i + 1);
                TableAttr tableAttr = new TableAttr();
                tableAttr.setName(colName);
                tableAttr.setType(colType);
                tableAttr.readComment(conn, table.getName(), colName);
                tableAttr.determineIfPri(conn, table.getName(), colName);
                tableAttr.determineIfAutoIncre(conn, table.getName(), colName);

                tableAttrs.add(tableAttr);
                columnNames.add(colName);
            }
        }
        catch (SQLException e) {
            log.error("getColumnNames failure", e);
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (SQLException e) {
                    log.error("getColumnNames close pstem and connection failure", e);
                }
            }
        }
        return tableAttrs;
    }

    private void determineIfAutoIncre(Connection conn, String tableName, String colName) {
        PreparedStatement pStemt = null;
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement("show full columns from " + tableName);
            rs = pStemt.executeQuery();
            while (rs.next()) {
                String fieldName = rs.getString("Field");
                if (colName.equals(fieldName)) {
                    String Extra = rs.getString("Extra");
                    if (Extra != null && Extra.contains("auto_increment")) {
                        isIncre = true;
                    }
                    else {
                        isIncre = false;
                    }
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    log.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
    }

    private boolean primaryKey;

    private void determineIfPri(Connection conn, String tableName, String colName) {
        PreparedStatement pStemt = null;
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement("show full columns from " + tableName);
            rs = pStemt.executeQuery();
            while (rs.next()) {
                String fieldName = rs.getString("Field");
                if (colName.equals(fieldName)) {
                    String KEY = rs.getString("Key");
                    if (KEY != null && KEY.contains("PRI")) {
                        isPri = true;
                    }
                    else {
                        isPri = false;
                    }
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    log.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
    }

    private void readComment(Connection conn, String tableName, String colName) {
        PreparedStatement pStemt = null;
        List<String> columnComments = new ArrayList<>();//列名注释集合
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement("show full columns from " + tableName);
            rs = pStemt.executeQuery();
            while (rs.next()) {
                String fieldName = rs.getString("Field");
                if (colName.equals(fieldName)) {
                    comment = rs.getString("Comment");
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    log.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isPri() {
        return isPri;
    }

    public boolean isIncre() {
        return isIncre;
    }
}
