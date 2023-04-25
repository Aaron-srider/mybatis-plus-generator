package fit.wenchao.db;

import fit.wenchao.db.dbConnection.getConnection
import fit.wenchao.db.generator.generate
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet


class Main {

}

private fun getTables(): MutableList<Table> {
    val tables = ArrayList<Table>()
    val conn: Connection = getConnection()
    var rs: ResultSet? = null
    try {
        //获取数据库的元数据
        val db: DatabaseMetaData = conn.metaData
        //从元数据中获取到所有的表名
        rs = db.getTables(null, "simple-codebase", null, arrayOf("TABLE"))
        while (rs.next()) {
            if (!"simple-codebase".equals(rs.getString(1))) {
                continue
            }
            val table = Table()
            val tableName = rs.getString(3)
            table.name = tableName
            val attrs = fromTable(conn, table)
            table.attrs = (attrs)
            tables.add(table)
        }

    } finally {
        rs?.close()
        conn.close()
    }
    return tables
}

fun main() {
    val tables = getTables()
    generate(tables)
}