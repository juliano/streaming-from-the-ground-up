package streaming.environment

import doobie.util.transactor.Transactor
import streaming.domain.City
import streaming.environment.config.Configuration.DbConfig
import zio.interop.catz._
import zio._

package object repository {
  type DbTransactor = Has[DbTransactor.Resource]
  type CitiesRepository = Has[CitiesRepository.Service]

  def allCities: RIO[CitiesRepository, fs2.Stream[Task, City]] =
    RIO.access(_.get.all)

  object DbTransactor {

    trait Resource {
      val tx: Transactor[Task]
    }

    val h2: URLayer[Has[DbConfig], DbTransactor] = ZLayer.fromService { db =>
      new Resource {
        val tx: Transactor[Task] =
          Transactor.fromDriverManager(db.driver, db.url, db.user, db.password)
      }
    }
  }
}
