package org.samik.EmbeddingModelServer

import org.samik.EmbeddingModelServer.utils.Input

/**
  * Class encapsulating an embedding request
  * @param input
  */
class EmbedRequest(input: Input)
{
    val data = input.stringVal("data")
}
