package com.zzzmode.server.tools.charles

import org.apache.tomcat.util.http.fileupload.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Created by zl on 2017/7/8.
 */
@Component
class ScheduledTasks{

    companion object {
        private val logger = LoggerFactory.getLogger(ScheduledTasks::class.java)

        const val EXPIRED_TIME:Long=1000*60*10
    }


    @Autowired
    lateinit var charlesConfig: CharlesConfig


    @Async
    @Scheduled(fixedRate = EXPIRED_TIME, initialDelay = 1000*60)
    fun reportCurrentTime(){
        logger.info("--->>>  ${Date()}")

        Files.list(Paths.get(charlesConfig.storeRootPath))?.forEach {

            val creationTime = it?.toFile()?.createTime()?:0

            //此处比较暴力的删除了，所有务必设置子目录，如果在webserver根目录可以会误删文件
            if (System.currentTimeMillis().minus(creationTime) > EXPIRED_TIME){
                FileUtils.forceDelete(it.toFile())
                logger.warn("delete old file -->: $it")
            }

        }

    }
}