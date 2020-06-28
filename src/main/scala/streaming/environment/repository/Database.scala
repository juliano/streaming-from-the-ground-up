package streaming.environment.repository

import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import doobie.implicits._
import io.getquill._
import streaming.domain.City
import zio.Task
import zio.interop.catz._

final case class Database(tx: Transactor[Task]) extends CitiesRepository.Service {
  val ctx = new DoobieContext.H2(Literal)
  import ctx._

  def all: fs2.Stream[Task, City] =
    ctx.stream(queryCities).transact(tx)

  private val queryCities = quote {
    query[City]
  }
}
