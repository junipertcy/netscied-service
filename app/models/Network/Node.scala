package models.Network

import com.mongodb.casbah.Imports._

case class Node(name: Int, group: Int) {
  def toMongoDBObject: MongoDBObject = {
    val obj: MongoDBObject = MongoDBObject(
      "name" -> name,
      "group" -> group
    )
    obj
  }
}

object Node {
  lazy val blank = Node(
    name = 0,
    group = 0
  )

  def parseMongoList(node: Option[MongoDBList]): List[Node] = node match {
    case Some(_node) =>
      _node.toList collect {
        case obj: DBObject => Node(obj)
      }
    case None =>
      List[Node]()
  }

  def apply(node: DBObject): Node = {
    val name = node.getAs[ObjectId]("meta.name").getOrElse(0).asInstanceOf[Int]
    val group = node.getAs[ObjectId]("group").getOrElse(0).asInstanceOf[Int]

    Node(
      name = name,
      group = group
    )
  }

  def withNodeName(data: Option[DBObject]): Option[Node] = data match {
    case Some(obj) =>
      val name = obj.getAs[Int]("name").getOrElse(0)
      val group = obj.getAs[Int]("group").getOrElse(0)

      Some(Node.blank.copy(
        name = name,
        group = group
      ))
    case None =>
      None
  }


}