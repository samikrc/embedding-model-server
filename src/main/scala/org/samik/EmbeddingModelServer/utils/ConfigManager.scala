package org.samik.EmbeddingModelServer.utils

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import org.samik.EmbeddingModelServer.EmbeddingModelServer

/**
 * @since 4/6/18
 */
object ConfigManager
{
    private val configFile = EmbeddingModelServer.configFile
    private val config: Config = ConfigFactory.parseFile(new File(configFile))

    def get(key: String): String = config.getAnyRef(key).toString
}
