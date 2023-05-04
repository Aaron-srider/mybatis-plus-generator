package fit.wenchao.db.generator;

// Treat every file as a generation unit. Every unit fetches information from global context first, then generates code and if necessary
// add some new information for later unit usage after generating finishes.
class GeneratorUnit {

    var generatorContext = GeneratorContext()

    // TODO
    fun getRequiredInfoMap(): HashMap<String, Any> {

    }

    // generate framework
    fun generate() {

        // get information from context
        var partialMap = getRequiredInfoMap()

        var newInfoRegistor= NewInfoRegistor()
        
        // do the generation work
        doGenerate(partialMap, newInfoRegistor);
        
        // pour new info to global context
        generatorContext.pour(newInfoRegistor)
        
        // report content in global context 
        reportContext();
    }

    // TODO
    private fun doGenerate(partialMap: HashMap<String, Any>, newInfoRegistor: NewInfoRegistor) {
        

    }


}
