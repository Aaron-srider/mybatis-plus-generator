package fit.wenchao.db.generator

import fit.wenchao.db.JavaPackage
import fit.wenchao.db.Table
import fit.wenchao.db.dbConnection.getConnection
import fit.wenchao.db.fromTable
import fit.wenchao.db.generator.units.*
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import java.util.*

enum class GlobalContextKey {
    TABLES,

    MODEL_PACKAGE,
    REPO_PACKAGE,
    REPOIMPL_PACKAGE,
    MAPPER_PACKAGE,
    SERVICE_PACKAGE,
    SERVICEIMPL_PACKAGE,


    LANGUAGE,

    PROJECT_TO_SRC_PATH,

    BASE_DIR,


    ModelSource,
    MapperSource,
    RepoSource,
    ServiceSource,
    ServiceImplSource,
}


fun getTables(): MutableList<Table> {
    val tables = ArrayList<Table>()
    val conn: Connection = getConnection()
    var rs: ResultSet? = null
    try {
        // 获取数据库的元数据
        val db: DatabaseMetaData = conn.metaData
        // 从元数据中获取到所有的表名
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


class Generator {

    fun start() {

        // global context for generation
        var globalContext = GeneratorContext()

        // read properties from config
        prepareProperties(globalContext)

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

    private fun prepareProperties(globalContext: GeneratorContext) {
        // read projectToSrc from config.properties
        val props = Properties()
        val inputStream = JavaPackage::class.java.classLoader.getResourceAsStream("config.properties")
        inputStream?.let {
            props.load(inputStream)
        } ?: run {
            throw RuntimeException("config.properties not found")
        }

        val projectToSrc: String? = props.getProperty("projectToSrc")
        projectToSrc?.let {
            globalContext.put(GlobalContextKey.PROJECT_TO_SRC_PATH.name, projectToSrc)
        } ?: run {
            throw RuntimeException("projectToSrc not found in config.properties")
        }

        val baseDir: String? = props.getProperty("baseDir")
        baseDir?.let {
            globalContext.put(GlobalContextKey.BASE_DIR.name, baseDir)
        } ?: run {
            throw RuntimeException("baseDir not found in config.properties")
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

    }

    private fun initContext(globalContext: GeneratorContext) {

        // prepare all tables to process
        val tables = getTables()
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