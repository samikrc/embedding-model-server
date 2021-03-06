package org.samik.EmbeddingModelServer

import org.samik.EmbeddingModelServer.utils.Input

/**
  * Class representing a request for vector with Google's USE model.
  */
class USEEmbedRequest(input: Input) extends EmbedRequest(input)
{
    /**
      * Split the input text by fullstop/period character to arrive at multiple
      * sentences. Return the embeddings for all sentences in an array.
      */
    val splitTextByFS =  input.jsonVal("splitSentenceByFS").asBoolean
}
