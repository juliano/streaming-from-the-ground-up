package streaming.environment

import streaming.environment.config.Configuration
import streaming.environment.repository._
import zio.ULayer
import zio.clock.Clock

object Environments {

  type HttpServerEnvironment = Clock with Configuration
  type AppEnvironment = HttpServerEnvironment with CitiesRepository

  val httpServerEnvironment = Clock.live ++ Configuration.live
  val dbTransactor: ULayer[DbTransactor] = Configuration.live >>> DbTransactor.h2
  val citiesRepository: ULayer[CitiesRepository] = dbTransactor >>> CitiesRepository.live
  val appEnvironment: ULayer[AppEnvironment] = httpServerEnvironment ++ citiesRepository
}
