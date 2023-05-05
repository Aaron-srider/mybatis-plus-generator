package fit.wenchao.db.generator;

// Treat every file as a generation unit. Every unit fetches information from global context first, then generates code and if necessary
// add some new information for later unit usage after generating finishes.
abstract class GeneratorUnit(var generatorContext: GeneratorContext) {

    // generate framework
    fun generate() {

        // get information from context
        var partialMap = getRequiredInfoMap()

        // a place to collect new info to be added to context
        var newInfoRegistor = NewInfoRegistor()

        // do the generation work
        doGenerate(partialMap, newInfoRegistor);

        // pour new info to global context
        generatorContext.pour(newInfoRegistor)

        // report content in global context 
        reportContext()
    }

    private fun reportContext() {
        // generatorContext.report();
    }

    // for child class to implement
    abstract fun doGenerate(partialMap: HashMap<String, Any>, newInfoRegistor: NewInfoRegistor)


    // for child class to implement
    open fun getRequiredInfoMap(): HashMap<String, Any> {
        return HashMap()
    }

}



