package fit.wenchao.db.generator

class NewInfoRegistor {

    var map = HashMap<String, Any>()

    fun addInfo(key: String, value: Any) {
        this.map[key] = value
    }
    
    // get all new info
    fun getNewInfoMap(): HashMap<String, Any> {
        return map
    }


    
}