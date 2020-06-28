package streaming.environment

import streaming.environment.config.Configuration.{ DbConfig, HttpServerConfig }
import zio.Has

package object config {

  type Configuration = Has[HttpServerConfig] with Has[DbConfig]
}
