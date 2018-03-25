package com.zzzmode.server.tools.charles

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.File
import javax.annotation.PostConstruct

@Component
class CharlesConfig {

    companion object {
        const val SUB_DIR = "ckfiles"
    }


    @Value("\${ckConfig}")
    private lateinit var charlesFile :String

    val subDir: String = SUB_DIR



    val storeRootPath: String by lazy {

        return@lazy subDir.also {
            File(it).mkdirs()
        }
    }


    data class CharlesCKVer(@JsonProperty("version")val version:String,
                            @JsonProperty("origJar")val origJar:String,
                            @JsonProperty("cls")val cls: String,
                            @JsonProperty("m1")val m1:String,
                            @JsonProperty("m2")val m2:String)

    lateinit var  configMap : Map<String,CharlesConfig.CharlesCKVer>

    @PostConstruct
    fun init(){

        configMap = with(if(File(charlesFile).exists()){
            File(charlesFile).readText()
        }else{
            ClassPathResource(charlesFile).inputStream.bufferedReader().readText()
        },{
            jacksonObjectMapper().readValue<List<CharlesCKVer>>(this).associateBy { it.version }
        })

    }
}