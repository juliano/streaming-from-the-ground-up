package streaming.environment.repository

import streaming.domain.City
import zio.{ Task, ZLayer }

object CitiesRepository {

  trait Service {
    def all: fs2.Stream[Task, City]
  }

  val live: ZLayer[DbTransactor, Nothing, CitiesRepository] =
    ZLayer.fromService { resource =>
      Database(resource.tx)
    }
}
