package org.samik.EmbeddingModelServer

import org.samik.EmbeddingModelServer.utils.Input

/**
  * Class encapsulating an embedding request.
  * Note: has to be a case class in order for input to be available from the class object.
  * @param input
  */
case class EmbedRequest(input: Input)
{
    val data = input.stringVal("data")
}
