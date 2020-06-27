package streaming.environment.config

object Configuration {

  final case class DbConfig(driver: String, url: String, user: String, password: String)
  final case class HttpServerConfig(host: String, port: Int, path: String)
  final case class AppConfig(database: DbConfig, httpServer: HttpServerConfig)

}
