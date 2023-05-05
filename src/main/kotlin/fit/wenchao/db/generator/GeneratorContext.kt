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

    fun report() {
        this.contextMap.forEach{ println(it)}
    }

    fun pour(newInfoRegistor: NewInfoRegistor) {
        val newInfoMap = newInfoRegistor.getNewInfoMap()
        this.contextMap.putAll(newInfoMap)
    }

}
