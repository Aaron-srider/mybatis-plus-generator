package fit.wenchao.db.generator

import fit.wenchao.db.Table
import fit.wenchao.db.fromTable
import fit.wenchao.db.generator.units.*
import fit.wenchao.db.sourceFile.JavaPackageImpl
import java.sql.*
import java.util.*

fun getTables(tableList: MutableList<String>): MutableList<Table> {
    if (tableList.isEmpty()) {
        return mutableListOf()
    }
    val tables = ArrayList<Table>()
    val conn: Connection = DbManager.getConnection()
    var rs: ResultSet? = null
    try {
        val db: DatabaseMetaData = conn.metaData
        rs = db.getTables(null, DbManager.dbname, null, arrayOf("TABLE"))
        while (rs.next()) {
            if (!DbManager.dbname.equals(rs.getString(1))) {
                continue
            }
            val table = Table()
            val tableName = rs.getString(3)
            if (!tableList.contains(tableName)) {
                continue
            }
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

object DbManager {
    var host: String
    var port: String
    var dbname: String
    var username: String
    var password: String
    var url: String

    init {
        val props = Properties()
        val inputStream = JavaPackageImpl::class.java.classLoader.getResourceAsStream("config.properties")
        inputStream?.let {
            props.load(inputStream)
        } ?: run {
            throw RuntimeException("config.properties not found")
        }

        // fetch connection props from config.properties and set default value
        var host: String = props.getProperty("host") ?: "127.0.0.1"
        var port: String = props.getProperty("port") ?: "3306"
        var dbname: String = props.getProperty("dbname") ?: "testdb"
        var username: String = props.getProperty("username") ?: "root"
        var password: String = props.getProperty("password") ?: "123456"

        this.host = host
        this.port = port
        this.dbname = dbname
        this.username = username
        this.password = password

        url = "jdbc:mysql://${host}:${port}/${dbname}?useUnicode=true&characterEncoding=UTF-8&useSSL=false"
    }

    fun getConnection(): Connection {
        var conn: Connection? = null
        conn = DriverManager.getConnection(
            url,
            username,
            password
        )
        conn ?: throw SQLException("get connection failure")
        return conn
    }

}

fun getCurrentPackage(clazz: Class<*>): String {
    return clazz.getPackage().name
}

fun getBackagePackagePath(clazz: Class<*>): String {
    val name = clazz.getPackage().name
    return name.replace(".", "/")
}

class Generator {

    fun start(mainClazz: Class<*>) {

        // global context for generation
        var globalContext = GeneratorContext()

        // read properties from config
        prepareProperties(mainClazz, globalContext)

        initContext(globalContext)

        // get all units for generation
        var generatorUnits = getAllGeneratorUnits(globalContext)

        // orchestration generation unit
        orchestrateUnits(generatorUnits)

        // trigger all units for generation
        generate(generatorUnits)

    }

    private fun orchestrateUnits(generatorUnits: MutableList<GeneratorUnit>) {
    }

    private fun prepareProperties(mainClazz: Class<*>, globalContext: GeneratorContext) {
        // read projectToSrc from config.properties
        val props = Properties()
        val inputStream = JavaPackageImpl::class.java.classLoader.getResourceAsStream("config.properties")
        inputStream?.let {
            props.load(inputStream)
        } ?: run {
            throw RuntimeException("config.properties not found")
        }

        val srcPath: String? = props.getProperty("srcPath")
        srcPath?.let {
            globalContext.put(GlobalContextKey.SRC_PATH.name, srcPath)
        } ?: run {
            throw RuntimeException("srcPath not found in config.properties")
        }


        val baseDir: String? = props.getProperty("basePackage")
        baseDir?.let {
            globalContext.put(GlobalContextKey.BASE_PACKAGE.name, baseDir)
        } ?: run {
            globalContext.put(GlobalContextKey.BASE_PACKAGE.name, getBackagePackagePath(mainClazz))
        }

        val modelPackage: String? = props.getProperty("modelPackage")
        modelPackage?.let {
            globalContext.put(GlobalContextKey.MODEL_PACKAGE.name, modelPackage)
        } ?: run {
            throw RuntimeException("modelPackage not found in config.properties")
        }

        val daoPackage: String? = props.getProperty("daoPackage")
        daoPackage?.let {
            globalContext.put(GlobalContextKey.REPO_PACKAGE.name, daoPackage)
        } ?: run {
            throw RuntimeException("daoPackage not found in config.properties")
        }

        val daoImplPackage: String? = props.getProperty("daoImplPackage")
        daoImplPackage?.let {
            globalContext.put(GlobalContextKey.REPOIMPL_PACKAGE.name, daoImplPackage)
        } ?: run {
            throw RuntimeException("daoImplPackage not found in config.properties")
        }

        val mapperPackage: String? = props.getProperty("mapperPackage")
        mapperPackage?.let {
            globalContext.put(GlobalContextKey.MAPPER_PACKAGE.name, mapperPackage)
        } ?: run {
            throw RuntimeException("mapperPackage not found in config.properties")
        }

        val servicePackage: String? = props.getProperty("servicePackage")
        servicePackage?.let {
            globalContext.put(GlobalContextKey.SERVICE_PACKAGE.name, servicePackage)
        } ?: run {
            throw RuntimeException("servicePackage not found in config.properties")
        }

        val serviceImplPackage: String? = props.getProperty("serviceImplPackage")
        serviceImplPackage?.let {
            globalContext.put(GlobalContextKey.SERVICEIMPL_PACKAGE.name, serviceImplPackage)
        } ?: run {
            throw RuntimeException("serviceImplPackage not found in config.properties")
        }


        val wantedTables: String? = props.getProperty("wantedTables")
        wantedTables?.let {
            var wantedTableList = it.split(",")
            globalContext.put(GlobalContextKey.WANTED_TABLES.name, wantedTableList)
        } ?: run {
            globalContext.put(GlobalContextKey.WANTED_TABLES.name, mutableListOf<String>())
        }

    }

    private fun initContext(globalContext: GeneratorContext) {
        var wantedTables: MutableList<String> =
            globalContext.get(GlobalContextKey.WANTED_TABLES.name) as MutableList<String>

        // prepare all tables to process
        val tables = getTables(wantedTables)
        globalContext.put(GlobalContextKey.TABLES.name, tables)
    }

    private fun generate(generatorUnits: MutableList<GeneratorUnit>) {
        generatorUnits.forEach { unit ->
            unit.generate()
        }
    }

    private fun getAllGeneratorUnits(globalContext: GeneratorContext): MutableList<GeneratorUnit> {
        return mutableListOf(
            ModelGeneratorUnit(globalContext),
            MapperGeneratorUnit(globalContext),
            RepoGeneratorUnit(globalContext),
            RepoImplGeneratorUnit(globalContext),
            ServiceGeneratorUnit(globalContext),
            ServiceImplGeneratorUnit(globalContext),
        )
    }
}