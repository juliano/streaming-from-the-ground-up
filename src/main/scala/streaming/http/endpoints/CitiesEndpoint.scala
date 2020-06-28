package streaming.http.endpoints

import io.circe.Encoder
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{ EntityEncoder, HttpRoutes }
import streaming.domain.City
import streaming.environment.repository._
import zio.RIO
import zio.interop.catz._

final class CitiesEndpoint[R <: CitiesRepository] {
  type CitiesTask[A] = RIO[R, A]
  type CitiesStream = fs2.Stream[CitiesTask, City]

  private val prefixPath = "/cities"

  implicit def cityEntityEncoder(implicit encoder: Encoder[City]): EntityEncoder[CitiesTask, City] =
    jsonEncoderOf[CitiesTask, City]

  val dsl = Http4sDsl[CitiesTask]
  import dsl._

  private val httpRoutes = HttpRoutes.of[CitiesTask] {
    case GET -> Root / "blocking" =>
      val pipeline: CitiesTask[CitiesStream] = allCities
      pipeline.flatMap(stream => Ok(stream.repeat.compile.toList.map(_.asJson)))

    case GET -> Root / "streaming" =>
      val pipeline: CitiesTask[CitiesStream] = allCities
      pipeline.flatMap(stream => Ok(stream.repeat.map(_.asJson)))
  }

  val routes: HttpRoutes[CitiesTask] = Router(
    prefixPath -> httpRoutes
  )
}
