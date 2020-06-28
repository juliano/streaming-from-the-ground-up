package streaming.http

import cats.data.Kleisli
import cats.effect.ExitCode
import cats.implicits._
import org.http4s.implicits._
import org.http4s.{ Request, Response }
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import streaming.environment.Environments.AppEnvironment
import streaming.environment.config.Configuration.HttpServerConfig
import streaming.http.endpoints.{ CitiesEndpoint, HealthEndpoint }
import zio.interop.catz._
import zio.{ RIO, ZIO }

object Server {
  type ServerRIO[A] = RIO[AppEnvironment, A]
  type ServerRoutes = Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]]

  def runServer: ZIO[AppEnvironment, Nothing, Unit] =
    ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
      val ec = rts.platform.executor.asEC
      val cfg = rts.environment.get[HttpServerConfig]

      BlazeServerBuilder[ServerRIO](ec)
        .bindHttp(cfg.port, cfg.host)
        .withHttpApp(createRoutes(cfg.path))
        .serve
        .compile[ServerRIO, ServerRIO, ExitCode]
        .drain
    }
      .orDie

  private def createRoutes(path: String): ServerRoutes = {
    val healthRoutes = new HealthEndpoint[AppEnvironment].routes
    val citiesRoutes = new CitiesEndpoint[AppEnvironment].routes
    val routes = healthRoutes <+> citiesRoutes

    Router[ServerRIO](path -> routes).orNotFound
  }
}
