package com.zzzmode.server.tools.charles

import javassist.ClassPool
import javassist.CtClass
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

/**
 * Created by zl on 2017/7/7.
 */
@Service
@ComponentScan
class CharlesCrackService constructor(@Autowired private val charlesConfig: CharlesConfig){

    private val rootPath = Paths.get(charlesConfig.storeRootPath)

    companion object {
        private val logger = LoggerFactory.getLogger(CharlesCrackService::class.java)
    }

    class CrackResult(var file: File?=null){
        val rid: String by lazy {
            UUID.randomUUID().toString().replace("-","").substring(0,16)
        }

    }

    @Throws(RuntimeException::class)
    fun process(name: String?,version: String?):CrackResult?{
        return version?.let {
            charlesConfig.configMap[it]?.let {
                return handleCrack(name ?: "zzzmode",it)
            }
        }
    }


    private fun handleCrack(name: String,config: CharlesConfig.CharlesCKVer):CrackResult?{
        val result = CrackResult()

        result.file=afterSave(config.origJar,config.cls,result.rid,modifyByte(config.origJar,config.cls,{
            var ctMethod = getDeclaredMethod(config.m1, null)
            ctMethod.setBody("{return true;}")
            ctMethod = getDeclaredMethod(config.m2, null)
            ctMethod.setBody("{return \"$name\";}")
        }))

        return result
    }




    private inline fun modifyByte(charlesFile : String,cls :String, block: CtClass.() -> Unit):ByteArray? {
        val classPool = ClassPool.getDefault()

        return with(classPool.getOrNull(cls),{

            this ?: kotlin.run {
                classPool.insertClassPath(charlesFile)
                classPool.get(cls)
            }

        })?.run {
            try {
                stopPruning(true)
                block(this)
                return@run toBytecode()
            }catch (e:Throwable){
                e.printStackTrace()
            }finally {
                defrost()
            }
            null
        }
    }


    private fun afterSave(charlesFile : String, cls :String,outDirName: String,toBytecode: ByteArray?):File?{
        val fn = cls.replace('.','/').plus(".class")

        val tempFile = replaceZipEntry(File(charlesFile), fn, newByteCode = toBytecode)

        logger.debug("tempFile ->size ${tempFile.length()}  ${tempFile.absolutePath}")

        val target = rootPath.resolve("$outDirName/charles.jar")
        val file = target.toFile()
        logger.debug("new save file path "+file.absolutePath)
        if(!file.exists()){
            file.mkdirs()
        }

        Files.move(tempFile.toPath().toAbsolutePath(), target, StandardCopyOption.REPLACE_EXISTING)

        logger.debug("ok file ->  ${file.absolutePath}")

        return file
    }
}