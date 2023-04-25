package fit.wenchao.db.generator

import fit.wenchao.db.JavaPackage
import fit.wenchao.db.Table
import fit.wenchao.db.from

class Generator {


}

fun generate(tables: MutableList<Table>) {
    for (table in tables) {
        println("tablename: " + table.name)
        println(" === table attrs === :")
        for (tableAttr in table) {
            println(tableAttr.name + " - " + tableAttr.type + " - " + tableAttr.isPri + " - " + tableAttr.isIncre + " - " + tableAttr.comment)
        }

        //System.out.println(new File(".").getAbsolutePath());
        val poSourceFile = table.generateJavaModel(from("fit/wenchao/db/dao/po"))
        val mapperSourceFile = table.generateMybatisMapper(
            from("fit/wenchao/db/dao/mapper"),
            poSourceFile
        )
        val daoSourceFile = table.generateDao(from("fit/wenchao/db/dao/repo"), poSourceFile)
        table.generateDaoImpl(
            from("fit/wenchao/db/dao/repo/impl"),
            mapperSourceFile,
            poSourceFile,
            daoSourceFile
        )
        val serviceSourceFile = table.generateService(from("fit/wenchao/db/service"))
        table.generateServiceImpl(from("fit/wenchao/db/service/impl"), serviceSourceFile)
    }
}
