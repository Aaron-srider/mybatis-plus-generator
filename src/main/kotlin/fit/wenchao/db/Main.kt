package fit.wenchao.db;

import fit.wenchao.db.JavaPackage.Companion.from
import fit.wenchao.db.dbConnection.getConnection
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import java.util.*



object Generator {

    lateinit var baseDir: String

    init {
        val props = Properties()
        val inputStream = javaClass.classLoader.getResourceAsStream("config.properties")
        inputStream?.let {
            props.load(inputStream)
        } ?: run {
            throw RuntimeException("config.properties not found")
        }

        val baseDir: String? = props.getProperty("baseDir")
        baseDir?.let {
            this.baseDir = baseDir
        } ?: run {
            throw RuntimeException("baseDir not found in config.properties")
        }
    }


    fun getTables(): MutableList<Table> {
        val tables = ArrayList<Table>()
        val conn: Connection = getConnection()
        var rs: ResultSet? = null
        try {
            // 获取数据库的元数据
            val db: DatabaseMetaData = conn.metaData
            // 从元数据中获取到所有的表名
            rs = db.getTables(null, null, null, arrayOf("TABLE"))
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

    fun generate() {

        val tables = getTables()

        for (table in tables) {
            println("tablename: " + table.name)
            println(" === table attrs === :")
            for (tableAttr in table) {
                println(tableAttr.name + " - " + tableAttr.type + " - " + tableAttr.isPri + " - " + tableAttr.isIncre + " - " + tableAttr.comment)
            }

            val poSourceFile = table.generateJavaModel(from("$baseDir/dao/po"))
            val mapperSourceFile = table.generateMybatisMapper(
                from("$baseDir/dao/mapper"),
                poSourceFile
            )
            val daoSourceFile = table.generateDao(from("$baseDir/dao/repo"), poSourceFile)
            table.generateDaoImpl(
                from("$baseDir/dao/repo/impl"),
                mapperSourceFile,
                poSourceFile,
                daoSourceFile
            )
            val serviceSourceFile = table.generateService(from("$baseDir/service"))
            table.generateServiceImpl(from("$baseDir/service/impl"), serviceSourceFile)
        }
    }
}


fun main() {
    Generator.generate()
}