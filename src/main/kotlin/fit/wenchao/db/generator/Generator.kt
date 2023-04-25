package fit.wenchao.db.generator

import fit.wenchao.db.JavaPackage
import fit.wenchao.db.Table

class Generator {
    companion object {


        @JvmStatic
        fun generate(tables: MutableList<Table>) {
            for (table in tables) {
                println("tablename: " + table.name)
                println(" === table attrs === :")
                for (tableAttr in table) {
                    println(tableAttr.name + " - " + tableAttr.type + " - " + tableAttr.isPri + " - " + tableAttr.isIncre + " - " + tableAttr.comment)
                }

                //System.out.println(new File(".").getAbsolutePath());
                val poSourceFile = table.generateJavaModel(JavaPackage.from("fit/wenchao/db/dao/po"))
                val mapperSourceFile = table.generateMybatisMapper(
                    JavaPackage.from("fit/wenchao/db/dao/mapper"),
                    poSourceFile
                )
                val daoSourceFile = table.generateDao(JavaPackage.from("fit/wenchao/db/dao/repo"), poSourceFile)
                table.generateDaoImpl(
                    JavaPackage.from("fit/wenchao/db/dao/repo/impl"),
                    mapperSourceFile,
                    poSourceFile,
                    daoSourceFile
                )
                val serviceSourceFile = table.generateService(JavaPackage.from("fit/wenchao/db/service"))
                table.generateServiceImpl(JavaPackage.from("fit/wenchao/db/service/impl"), serviceSourceFile)
            }
        }
    }


}

