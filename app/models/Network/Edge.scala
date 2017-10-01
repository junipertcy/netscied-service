package models.Network

import com.mongodb.casbah.Imports._
case class Edge(
                 source: Int,
                 target: Int,
                 value: Int) {

  def toMongoDBObject: MongoDBObject = {
    val obj: MongoDBObject = MongoDBObject(
      "source" -> source,
      "target" -> target,
      "value" -> value
    )
    obj
  }
}

object Edge {
  lazy val blank = Edge(
    source = 0,
    target = 0,
    value = 0
  )

  def parseMongoList(edge: Option[MongoDBList]): List[Edge] = edge match {
    case Some(_edge) =>
      _edge.toList collect {
        case obj: DBObject => Edge(obj)
      }
    case None =>
      List[Edge]()
  }

  def apply(edge: DBObject): Edge = {
    val source: Int = edge.getAs[Int]("source").getOrElse(0)
    val target: Int = edge.getAs[Int]("target").getOrElse(0)
    val value: Int = edge.getAs[Int]("value").getOrElse(0)

    Edge(
      source = source,
      target = target,
      value = value
    )
  }

  def fromDB(data: Option[DBObject]): Option[Edge] = data match {
    case Some(obj) =>
      val source = obj.getAs[Int]("source").getOrElse(0)
      val target = obj.getAs[Int]("target").getOrElse(0)
      val value = obj.getAs[Int]("value").getOrElse(0)

      Some(Edge.blank.copy(
        source = source,
        target = target,
        value = value
      ))
    case None =>
      None
  }
}