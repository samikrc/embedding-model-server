package org.samik.EmbeddingModelServer

import org.scalatest.flatspec.AnyFlatSpec
import org.tensorflow.framework.{MetaGraphDef, SignatureDef, TensorInfo}
import org.tensorflow.{SavedModelBundle, Shape, Tensor, Tensors}
//import org.tensorflow.proto.framework.{MetaGraphDef, SignatureDef, TensorInfo}
//import org.tensorflow.{SavedModelBundle, Tensor}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Test class for serving USE embedding
  */
class USEEmbeddingServerTest extends AnyFlatSpec
{
    val useModel = SavedModelBundle.load("/home/samik/git/embedding-model-server/tfhub/use_4", "serve")

    ///*
    val metaData = MetaGraphDef.parseFrom(useModel.metaGraphDef())
    val signatureDef = metaData.getSignatureDefMap().get("serving_default")
    val firstInput = mapToShape(signatureDef.getInputsMap.asScala).keys.head
    val firstOutput = mapToShape(signatureDef.getOutputsMap.asScala).keys.head
    val sessionRunner = useModel.session().runner()

    val input = Array("Hello there!", "How are you?")
    val inputTensors = Tensors.create(input.map(sentence => sentence.getBytes()))
    val results = sessionRunner.feed(firstInput, inputTensors).fetch(firstOutput).run().asScala(0)
    // First copy over to an array
    val array = Array.ofDim[Float](results.shape()(0).toInt, results.shape()(1).toInt)
    results.copyTo(array)
    array.foreach(embedding => println(s"[${embedding.mkString(", ")}]"))

    private def mapToShape(map: mutable.Map[String, TensorInfo]): mutable.Map[String, Shape] =
    {
        map.foldLeft(mutable.HashMap[String, Shape]())
        { case(accum, (_, tensorInfo)) =>
            val dimList = tensorInfo.getTensorShape.getDimList.asScala.map(_.getSize)
            val shape = if(dimList.length == 0) Shape.unknown() else Shape.make(dimList(0), dimList.drop(1): _*)
            accum += (tensorInfo.getName -> shape)
        }
    }
    //*/

    /*

    import org.tensorflow.ndarray.Shape
    import org.tensorflow.ndarray.buffer.DataBuffers
    import org.tensorflow.types.{TString, TUint8}
    import java.nio.ByteBuffer
    import java.nio.charset.StandardCharsets


    val metaData = useModel.metaGraphDef()
    val signatureDef = metaData.getSignatureDefMap().get("serving_default")
    val firstInput = mapToShape(signatureDef.getInputsMap.asScala).keys.head
    val firstOutput = mapToShape(signatureDef.getOutputsMap.asScala).keys.head


    val input = "Hello"
    val dataBuffer = DataBuffers.of(ByteBuffer.wrap(input.getBytes(StandardCharsets.UTF_8)))
    val tensor = Tensor.of(TString.DTYPE, Shape.of(1L), dataBuffer)
    println(s"Tensor: $tensor")
    val sessionRunner = useModel.session().runner()
    val result = sessionRunner
            .feed(firstInput, tensor)
            .fetch(firstOutput)
            .run()
            .asScala
    println(result)

    private def mapToShape(map: mutable.Map[String, TensorInfo]): mutable.Map[String, Shape] =
    {
        map.foldLeft(mutable.HashMap[String, Shape]())
        { case(accum, (_, tensorInfo)) =>
            val dimList = tensorInfo.getTensorShape.getDimList.asScala.map(_.getSize)
            val shape = if(dimList.length == 0) Shape.unknown() else Shape.of(dimList: _*)
            accum += (tensorInfo.getName -> shape)
        }
    }
    */
}
