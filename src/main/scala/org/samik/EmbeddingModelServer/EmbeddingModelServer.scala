package org.samik.EmbeddingModelServer

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive1, ExceptionHandler, Route}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.samik.EmbeddingModelServer.utils.{ConfigManager, Input, Json}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import akka.pattern.ask

import scala.collection.mutable
import scala.concurrent.Future

object EmbeddingModelServer extends App
{
    if(args.length == 0)
    {
        println("Usage: EmbeddingModelServer [local OR server] [/path/to/config/file]")
        println("Defaults:")
        println("  1st arg: local")
        println("  2nd arg: local-config.conf")
        System.exit(0)
    }

    private val execType = if(args.length > 0) args(0) else "local"
    val configFile = if(args.length > 1) args(1) else s"$execType-config.conf"

    implicit val system = ActorSystem("embedding-model-server")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    import scala.concurrent.duration._
    // Timeout for future request
    implicit def timeout: Timeout = 5 seconds

    private final val apiExecutorInstanceCount: Int = 5 //20
    private final val maxRequestDataSize = 20000L
    private val appServiceManagers = new mutable.HashMap[String, ActorRef]()

    // Start USEEmbeddingServer actors
    //val useEmbeddingServers = system.actorOf(Props(new APIExecutor()).withRouter(RoundRobinPool(apiExecutorInstanceCount)), name = "apiExecutors")
    val useEmbeddingServer = new USEEmbeddingServer(system)

    // Define helper directive to convert input JSON to an EmbedRequest
    implicit val webInputFromStringPayloadUM: FromEntityUnmarshaller[EmbedRequest] =
        PredefinedFromEntityUnmarshallers.stringUnmarshaller.map(v => new EmbedRequest(new Input(v)))

    // Define an exception handler for the possible exceptions that arise.
    val serverExceptionHandler = ExceptionHandler
    {
        case ex: Exception => complete(HttpResponse(StatusCodes.InternalServerError, entity = ex.getMessage))
    }

    val route = (handleExceptions(serverExceptionHandler)
            & decodeRequest
            & cors()
            & withSizeLimit(maxRequestDataSize)
            & post
            & entity(as[EmbedRequest]))
    {
        embedRequest =>
        {
            (path("USE") & pathEndOrSingleSlash)
            {
                val responseF: Future[USEEmbeddingServer.USEEmbeddingResponse] = useEmbeddingServer.getEmbedding(embedRequest.asInstanceOf[USEEmbedRequest])
                onSuccess(responseF)
                {
                    response => complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, Json.Value(response.data).write))
                }
            }
        }
    }


    val bindingFuture = Http().bindAndHandle(route, ConfigManager.get("http.interface"), ConfigManager.get("http.port").toInt)
    println(s"Server online at http://${ConfigManager.get("http.interface")}:${ConfigManager.get("http.port")}/")
}
