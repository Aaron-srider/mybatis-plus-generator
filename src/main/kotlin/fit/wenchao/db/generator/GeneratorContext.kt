package fit.wenchao.db.generator

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

}
