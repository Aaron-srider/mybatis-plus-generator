package fit.wenchao.db

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class TableAttr {
    var name: String = ""
    var type: String = "int"
    var isPri = false
    var isIncre = false
    var comment: String? = ""

    fun determineIfAutoIncre(conn: Connection, tableName: String, colName: String) {
        var pStemt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            pStemt = conn.prepareStatement("show full columns from $tableName")
            rs = pStemt.executeQuery()
            while (rs.next()) {
                val fieldName = rs.getString("Field")
                if (colName == fieldName) {
                    val Extra = rs.getString("Extra")
                    isIncre = Extra != null && Extra.contains("auto_increment")
                    break
                }
            }
        } finally {
            rs?.close()
        }
    }

    fun determineIfPri(conn: Connection, tableName: String, colName: String) {
        var pStemt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            pStemt = conn.prepareStatement("show full columns from $tableName")
            rs = pStemt.executeQuery()
            while (rs.next()) {
                val fieldName = rs.getString("Field")
                if (colName == fieldName) {
                    val KEY = rs.getString("Key")
                    isPri = KEY != null && KEY.contains("PRI")
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            rs?.close()
        }
    }

    fun readComment(conn: Connection, colName: String) {
        var pStemt: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            pStemt = conn.prepareStatement("show full columns from $name")
            rs = pStemt.executeQuery()
            while (rs.next()) {
                val fieldName = rs.getString("Field")
                if (colName == fieldName) {
                    comment = rs.getString("Comment")
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            rs?.close()
        }
    }

}


const val SQL = "SELECT * FROM " // 数据库操作

fun fromTable(conn: Connection, table: Table): List<TableAttr> {
    val columnNames = ArrayList<String>()
    val tableAttrs = ArrayList<TableAttr>()

    var ps: PreparedStatement? = null
    val tableSql = SQL + table.name
    try {
        ps = conn.prepareStatement(tableSql)
        val rsmd = ps.metaData
        val tableColumnCount = rsmd.columnCount
        for (i in 0 until tableColumnCount) {
            val colName: String = rsmd.getColumnName(i + 1)
            val colType: String = rsmd.getColumnTypeName(i + 1)

            val tableAttr = TableAttr()
            tableAttr.name = colName
            tableAttr.type = colType
            tableAttr.readComment(conn, colName)
            tableAttr.determineIfPri(conn, table.name, colName)
            tableAttr.determineIfAutoIncre(conn, table.name, colName)

            tableAttrs.add(tableAttr)
            columnNames.add(colName)
        }
    } finally {
        ps?.close()
    }
    return tableAttrs
}