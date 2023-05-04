package fit.wenchao.db.generator

class Generator {

    fun start() {

        // global context for generation
        var globalContext = GeneratorContext()

        // get all units for generation
        var generatorUnits = getAllGeneratorUnits(globalContext)

        // orchestration generation unit
        orchestrateUnits(generatorUnits)

        // trigger all units for generation
        generate()

    }

    private fun generate() {
        TODO("Not yet implemented")
    }

    private fun getAllGeneratorUnits(): MutableList<GeneratorUnit> {
        TODO("Not yet implemented")
    }
}