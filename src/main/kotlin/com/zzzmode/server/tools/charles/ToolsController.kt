package com.zzzmode.server.tools.charles

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import java.io.File
import java.util.*

/**
 * Created by zl on 2017/7/7.
 */
@RestController()
@ComponentScan
class ToolsController {

    companion object {
        private val logger = LoggerFactory.getLogger(ToolsController::class.java)
    }

    @Autowired
    lateinit var charlesCK: CharlesCrackService

    @Autowired
    lateinit var charlesConfig: CharlesConfig

    private val downloadHttpHeaders by lazy {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_OCTET_STREAM
        httpHeaders.contentDisposition = ContentDisposition.parse("attachment; filename=charles.jar")
        HttpHeaders.readOnlyHttpHeaders(httpHeaders)
    }

    @RequestMapping(path = [CharlesConfig.SUB_DIR+"/{ckName}/charles.jar"], method = [(RequestMethod.GET)])
    fun download(@PathVariable("ckName") ckName:String): ResponseEntity<Resource>? {

        val file = File("${CharlesConfig.SUB_DIR}/$ckName/charles.jar")
        if(file.exists()){
            return ResponseEntity(FileSystemResource(file),downloadHttpHeaders, HttpStatus.OK)
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }


    @CrossOrigin
    @RequestMapping(value = ["/ck"],
            method = [RequestMethod.POST, RequestMethod.OPTIONS])
    fun ckCharles(@RequestHeader headers: HttpHeaders, @RequestParam(name = "name", defaultValue = "zzzmode") name: String, @RequestParam(name="version") version: String): CKResp {

        return handleCK(name, version)
    }

    @CrossOrigin
    @RequestMapping(value = ["/ck"],
            method = [RequestMethod.POST, RequestMethod.OPTIONS],
            consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun ckCharles(@RequestHeader headers: HttpHeaders, @RequestBody req: CKVerResp): CKResp {
        return handleCK(req.name, req.version)
    }


    private fun handleCK(name: String, version: String): CKResp {

        val resp = CKResp(name)

        charlesCK.process(name, version)?.apply {
            resp.version = version
            resp.file = "${charlesConfig.subDir}/${rid}/charles.jar"
            resp.size = file?.length()
            logger.debug("create file ${file?.absolutePath}")
        }

        return resp
    }

    @CrossOrigin
    @RequestMapping(value = ["/getCkVer"])
    fun getCkVers(): List<CKVerResp> {
        val ret = LinkedList<CKVerResp>()
        charlesConfig.configMap.forEach { t: String, _ ->
            ret.add((CKVerResp("Charles $t", t)))
        }
        return ret
    }

    data class CKResp(var name: String?, var version: String? = null, var file: String? = null, var size: Long? = 0)

    data class CKVerResp(var name: String, var version: String)
}