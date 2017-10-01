package clients.mongo.network

/**
  * Created by Tzu-Chi Yen on 10/2/17.
  */

import com.mongodb.casbah.Imports._
import conf.DefaultSettings
import clients.mongo.MongoDBConnector
import models.Network.Network
import models.Network.Node
import models.Network.Edge

import org.bson.types.ObjectId

trait NetworkMongoClient extends MongoDBConnector {
  lazy val db_node: MongoCollection = db(DefaultSettings.MONGO_COL_CT_NODE)
  lazy val db_edge: MongoCollection = db(DefaultSettings.MONGO_COL_CT_EDGE)
  lazy val db_network: MongoCollection = db(DefaultSettings.MONGO_COL_CT_NETWORK)

  def getNameByNodeId(nid: String, nodeId: Int): Option[String] = {
    val nodeInfo = db_node.findOne(MongoDBObject(
      "network_id" -> new ObjectId(nid),
      "node_id" -> nodeId))

    nodeInfo match {
      case Some(n) => n.getAs[String]("meta.name")
      case _ => null
    }
  }

  def withNodesMeta(nid: String, nodeType: Int = 1): Option[Network] = {
    val query = MongoDBObject("network_id" -> new ObjectId(nid), "node_type" -> nodeType)
    val nodesMeta = db_node.find(query).limit(100000)
    //    val nodesMeta = findSliceByQuery(coll = ct_node, query = query, offset = 0, size = 100000)

    var nodesMetaDict = Map[Int, String]()
    for (i <- nodesMeta) {
      nodesMetaDict += (i.getAs[Int]("node_id").get -> i.getAs[String]("meta.name").get)
    }

    var nodes = List[Node]()
    var edges = List[Edge]()

    Some(Network.blank.copy(
      network_id = nid,
      nodes = nodes,
      edges = edges
    ))
  }

  def getNetworkById(nid: String, nodeType: Int = 1): Option[Network] = {
    val query = MongoDBObject("network_id" -> new ObjectId(nid), "node_type" -> nodeType)
    NetworkMongoClient.withNodesMeta(nid = nid, nodeType = nodeType)
  }
}

object NetworkMongoClient extends NetworkMongoClient
