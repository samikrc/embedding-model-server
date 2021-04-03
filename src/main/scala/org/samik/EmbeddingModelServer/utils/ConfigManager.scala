package org.samik.EmbeddingModelServer.utils

import java.io.{File, IOException}

import com.typesafe.config.{Config, ConfigFactory}
import org.samik.EmbeddingModelServer.EmbeddingModelServer

/**
 * @since 4/6/18
 */
object ConfigManager
{
    private val config: Config = readConfigFile

    private def readConfigFile: Config =
    {
        val configFile = EmbeddingModelServer.configFile
        val fileObj = new File(configFile)
        if(fileObj.exists())
            ConfigFactory.parseFile(fileObj)
        else
            throw new IOException(s"Config file doesn't exist: $configFile")
    }

    /**
      * Method to get a config from the config file.
      * @param key
      * @return
      */
    def get(key: String): String = config.getAnyRef(key).toString
}
