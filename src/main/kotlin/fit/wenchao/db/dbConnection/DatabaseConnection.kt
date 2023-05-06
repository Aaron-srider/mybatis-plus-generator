package fit.wenchao.db.dbConnection

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnection {
}


 const val DRIVER = "com.mysql.jdbc.Driver"
 const val DATABASE_URL =
    "jdbc:mysql://localhost:33061/shardingsphere-encryption-playground?useUnicode=true&characterEncoding=UTF-8&useSSL=false"
 const val USERNAME = "root"
 const val PASSWORD = "wc123456"


fun closeConnection(conn: Connection) {
    conn.close()
}

fun getConnection(): Connection {
    var conn: Connection? = null
    conn = DriverManager.getConnection(
        DATABASE_URL,
        USERNAME,
        PASSWORD
    )

    conn?: throw SQLException("get connection failure")
    return conn
}
