package streaming

import zio._

object Main extends App {
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    val program = for {
      _ <- zio.console.putStrLn("IT'S ALIVE!")
    } yield ()

    program.exitCode
  }
}
