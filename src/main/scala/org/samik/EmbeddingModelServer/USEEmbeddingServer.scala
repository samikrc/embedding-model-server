package org.samik.EmbeddingModelServer

import akka.actor.ActorSystem
import org.samik.EmbeddingModelServer.USEEmbeddingServer.USEEmbeddingResponse
import org.samik.EmbeddingModelServer.utils.ConfigManager
import org.tensorflow.{SavedModelBundle, Shape, Tensors}
import org.tensorflow.framework.{MetaGraphDef, TensorInfo}

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
    import system.dispatcher

    // Load the USE model
    private val useModel = loadUSE
    private val metaData = MetaGraphDef.parseFrom(useModel.metaGraphDef())
    private val signatureDef = metaData.getSignatureDefMap().get("serving_default")
    private val firstInput = mapToShape(signatureDef.getInputsMap.asScala).keys.head
    private val firstOutput = mapToShape(signatureDef.getOutputsMap.asScala).keys.head

    /**
      * Method to load an USE model
      * @return
      */
    private def loadUSE: SavedModelBundle =
    {
        val USEModelPath = ConfigManager.get("USE.path")
        SavedModelBundle.load(USEModelPath)
    }

    /**
      * Method to convert the inputMap or outputMap to Shape objects
      * @param map
      * @return
      */
    private def mapToShape(map: mutable.Map[String, TensorInfo]): mutable.Map[String, Shape] =
    {
        map.foldLeft(mutable.HashMap[String, Shape]())
        { case(accum, (_, tensorInfo)) =>
            val dimList = tensorInfo.getTensorShape.getDimList.asScala.map(_.getSize)
            val shape = if(dimList.length == 0) Shape.unknown() else Shape.make(dimList(0), dimList.drop(1): _*)
            accum += (tensorInfo.getName -> shape)
        }
    }

    def getEmbedding(useEmbedRequest: USEEmbedRequest): Future[USEEmbeddingResponse] = Future
    {
        val sessionRunner = useModel.session().runner()
        // TODO Return future success or fail based on lengths!
        val input = useEmbedRequest.data
        val transformedInput = if(useEmbedRequest.splitTextByFS) input.split("\\.") else Array(input)
        val inputTensors = Tensors.create(transformedInput.map(sentence => sentence.getBytes()))
        val results = sessionRunner.feed(firstInput, inputTensors).fetch(firstOutput).run().asScala(0)
        // First copy over to an array
        val outputTensors = Array.ofDim[Float](results.shape()(0).toInt, results.shape()(1).toInt)
        results.copyTo(outputTensors)
        //tensorArray.map(embedding => println(s"[${embedding.mkString(", ")}]"))

        USEEmbeddingResponse(outputTensors)
    }
    // Load the USE Model

}
