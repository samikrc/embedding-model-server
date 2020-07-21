package org.samik.EmbeddingModelServer

import akka.actor.ActorSystem
import org.samik.EmbeddingModelServer.USEEmbeddingServer.USEEmbeddingResponse
import org.samik.EmbeddingModelServer.utils.ConfigManager
//import org.tensorflow.{SavedModelBundle, Shape, Tensors}
//import org.tensorflow.framework.{MetaGraphDef, TensorInfo}

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.concurrent.Future

object USEEmbeddingServer
{
    case class USEEmbeddingResponse(data: Array[_ <: Any])
}

/**
  * Class to extract the USE embedding of a sentence.
  * There is no state to manage here, only simple lookup. Using Futures directly.
  * Reference:
  * - https://stackoverflow.com/questions/23922530/when-to-use-actors-vs-futures
  * - https://www.chrisstucchio.com/blog/2013/actors_vs_futures.html
  */
class USEEmbeddingServer(system: ActorSystem)
{
    def getEmbedding(useEmbedRequest: USEEmbedRequest): Future[USEEmbeddingResponse] = ???
}
