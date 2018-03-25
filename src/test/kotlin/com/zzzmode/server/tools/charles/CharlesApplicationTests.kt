package com.zzzmode.server.tools.charles

import org.assertj.core.internal.bytebuddy.utility.RandomString
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class CharlesApplicationTests {

	companion object {
		private val logger = LoggerFactory.getLogger(CharlesApplicationTests::class.java)
	}

	@Autowired
	lateinit var charlesCK: CharlesCrackService

	@Autowired
	lateinit var charlesConfig: CharlesConfig

	@Test
	fun contextLoads() {
		logger.debug("---------TEST---------")
		charlesConfig.configMap.forEach { t, _ ->


			val name = RandomString.make()
			val process = charlesCK.process(name, t)

			logger.debug("TEST  $t  $name ---->> $process")
		}

	}

}
