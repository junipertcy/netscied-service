package models.Network

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject

case class Network(
                    network_id: String,
                    nodes: List[Node],
                    edges: List[Edge]) {

  def toMongoDBObject: MongoDBObject = {
    val obj: MongoDBObject = MongoDBObject(
      "network_id" -> network_id,
      "nodes" -> nodes.map(_.toMongoDBObject),
      "edges" -> edges.map(_.toMongoDBObject)
    )
    obj
  }
}


object Network {
  lazy val blank = Network(
    network_id = "",
    nodes = List[Node](),
    edges = List[Edge]()
  )
  def apply(item: Option[DBObject],
            withNodeMeta: Boolean = false,
            withWeight: Boolean = false,
            withGroupInfo: Boolean = false): Option[Network] = item match {
    case Some(data) =>
      val network_id = data.getAs[ObjectId]("network_id").getOrElse("").toString
      val nodes: List[Node] =
        Node.parseMongoList(data.getAs[MongoDBList]("nodes"))
      val edges: List[Edge] =
        Edge.parseMongoList(data.getAs[MongoDBList]("edges"))

      Some(Network.blank.copy(
        network_id = network_id,
        nodes = nodes,
        edges = edges
      ))
    case None =>
      None
  }

}