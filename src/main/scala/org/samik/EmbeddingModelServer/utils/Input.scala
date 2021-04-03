package org.samik.EmbeddingModelServer.utils

import scala.collection.immutable

/**
  * Helper class for processing POST data.
  * @param input
  * @param requiredParamList
  */
class Input(input: Map[String, Any], requiredParamList: List[String] = List())
{
    // Validate the params, or throw an exception
    val missingParams = required(requiredParamList)
    if(missingParams.nonEmpty)
        throw new NoSuchElementException(s"Missing parameters: ${missingParams.mkString(", ")}\n")

    /**
      * Constructor accepting a JSON string and a list of required arguments. <br />
      * Note: we cannot have a default argument for the requiredParamList, since we run into this
      * problem: [https://stackoverflow.com/questions/4652095/why-does-the-scala-compiler-disallow-overloaded-methods-with-default-arguments].
      * We need to define an additional constructor with "body" as a workaround.
      *
      * @param body
      * @return
      */
    def this(body: String, requiredParamList: List[String]) = this(Json.parse(body).asMap, requiredParamList)

    /**
      * Constructor accepting a JSON string.
      * @param body
      */
    def this(body: String) = this(body, List())

    /**
      * Check for the required parameters.
      *
      * @param params
      * @return A sequence of parameters that are required but missing.
      */
    def required(params: List[String]): immutable.Seq[String] =
        for{ param <- params if (!input.contains(param)) } yield param

    /**
      * Extract string value for a key.
      *
      * @param key
      * @return
      */
    def stringVal(key: String) =
    {
        input.getOrElse(key, "") match
        {
            case value: Json.Value => if (value.isNumeric) value.asLong.toString else value.asString
            case value: Any => value.toString
        }
    }

    def jsonVal(key: String) =
    {
        input.getOrElse(key, "") match
        {
            case v: Json.Value => v
            case _ => throw new IllegalArgumentException
        }
    }

    def byteArray(key: String) =
    {
        input.getOrElse(key, "") match
        {
            case v: Json.Value => ???
            case _ => throw new IllegalArgumentException
        }
    }
}
