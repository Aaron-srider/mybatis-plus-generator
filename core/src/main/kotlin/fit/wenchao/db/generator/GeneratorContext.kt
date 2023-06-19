package fit.wenchao.db.generator

/**
 * each package contains three components, src path, base package name, and specific package name
 */
enum class GlobalContextKey {
    TABLES,

    /**
     * com.example.dao.po
     *
     * model package: dao.po
     */
    MODEL_PACKAGE,
    REPO_PACKAGE,
    REPOIMPL_PACKAGE,
    MAPPER_PACKAGE,
    SERVICE_PACKAGE,
    SERVICEIMPL_PACKAGE,
    WANTED_TABLES,


    LANGUAGE,

    /**
     * src/main/java or src/main/kotlin
     */
    SRC_PATH,

    /**
     *
     * com.example.moduleA
     * com.example.moduleB
     * com.example.moduleC
     *
     * base package: com.example
     *
     */
    BASE_PACKAGE,





    HOST,
    PORT,
    DBNAME,
    USERNAME,
    PASSWORD,
    URL,


    ModelSource,
    MapperSource,
    RepoSource,
    ServiceSource,
    ServiceImplSource,;


}

class GeneratorContext {

    // map for global context variables
    var contextMap = HashMap<String, Any>()


    // put information to context map
    fun put(key: String, value: Any) {
        contextMap[key] = value
    }

    // get information from context map
    fun get(key: String): Any? {
        return contextMap[key]
    }

    fun report() {
        this.contextMap.forEach{ println(it)}
    }

    fun pour(newInfoRegistor: NewInfoRegistor) {
        val newInfoMap = newInfoRegistor.getNewInfoMap()
        this.contextMap.putAll(newInfoMap)
    }

}
