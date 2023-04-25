package fit.wenchao.db.dbConnection

import fit.wenchao.db.log.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnection {

    companion object : Log() {
        
        private const val DRIVER = "com.mysql.jdbc.Driver"
        private const val DATABASE_URL =
            "jdbc:mysql://localhost:33061/simple-codebase?useUnicode=true&characterEncoding=UTF-8&useSSL=false"
        private const val USERNAME = "root"
        private const val PASSWORD = "wc123456"

        init {
            try {
                Class.forName(DRIVER)
            } catch (e: ClassNotFoundException) {
                log.error("can not load jdbc driver", e)
            }
        }

        @JvmStatic
        fun getConnection(): Connection? {
            var conn: Connection? = null
            try {
                conn = DriverManager.getConnection(
                    DATABASE_URL,
                    USERNAME,
                    PASSWORD
                )
            } catch (e: SQLException) {
                log.error("get connection failure", e)
            }
            return conn
        }
        @JvmStatic
        fun closeConnection(conn: Connection?) {
            if (conn != null) {
                try {
                    conn.close()
                } catch (e: SQLException) {
                    log.error("close connection failure", e)
                }
            }
        }
    }


}