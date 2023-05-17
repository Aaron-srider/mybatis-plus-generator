package fit.wenchao.db.generator
enum class GlobalContextKey {
    TABLES,

    MODEL_PACKAGE,
    REPO_PACKAGE,
    REPOIMPL_PACKAGE,
    MAPPER_PACKAGE,
    SERVICE_PACKAGE,
    SERVICEIMPL_PACKAGE,
    WANTED_TABLES,


    LANGUAGE,

    PROJECT_TO_SRC_PATH,

    BASE_DIR,



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
