package conf

import java.util

import utils.Awakable
import com.typesafe.config.{Config, ConfigFactory}
import utils.console.ColorForConsole._
import scala.util.Properties

trait DefaultSettings extends Awakable {
  lazy val mode: String = {
    val envMode = Properties.envOrElse("IO_CONF", "debug")
    logger.info(s"[io] conf. file reading : ${envMode.withColor(CYAN)}") //  + "$\t$" + sys.env.toList.mkString("|")
    envMode
  }
  lazy val conf: Config = ConfigFactory.load("").getConfig(mode)
  lazy val MONGO_ADDR: util.List[_ <: Config] = conf.getConfigList("mongo.addr")
  lazy val MONGO_HOST: String = MONGO_ADDR.get(0).getString("host")
  lazy val MONGO_PORT: Int = MONGO_ADDR.get(0).getInt("port")
  lazy val MONGO_ADDR_ARRAY: Array[(String, Int)] = MONGO_ADDR.toArray collect {
    case item: Config => (item.getString("host"), item.getInt("port"))
  }
  lazy val MONGO_DB: String = conf.getString("mongo.db")
  lazy val MONGO_USER: String = conf.getString("mongo.user")
  lazy val MONGO_PASS_STR: String = conf.getString("mongo.password")
  lazy val MONGO_PASS: Array[Char] = MONGO_PASS_STR.toCharArray


  lazy val MONGO_COL_CT_NODE: String = conf.getString("mongo.coll_name.ct_node")
  lazy val MONGO_COL_CT_EDGE: String = conf.getString("mongo.coll_name.ct_edge")
  lazy val MONGO_COL_CT_NETWORK: String = conf.getString("mongo.coll_name.ct_network")
  lazy val MONGO_COL_CT_SIMNET: String = conf.getString("mongo.coll_name.ct_simnet")

  /**
    * **THIS CONFIGURE OBJECT ONLY AFFECTED BEFORE THE MONGODB-CONNECTOR INITIALIZED**
    */
  object MONGO_CONN {
    // in ms
    var CONN_LIFE_TIMEOUT: Int = 1000 * 60 * 30
    // in ms
    var CONN_IDLE_TIMEOUT: Int = 1000 * 60 * 30
    // quick fail ..
    var CONN_MAX_WAIT_TIMEOUT: Int = 1000 * 2
    // quick fail ..
    var CONN_SOCKET_TIMEOUT: Int = 1000 * 5
    // in ms
    var MIN_CONN_PER_HOST_FACTOR: Double = 1.0
    var MIN_CONN_PER_HOST: Int = (Runtime.getRuntime.availableProcessors() * MIN_CONN_PER_HOST_FACTOR).toInt max 1
    var MAX_CONN_PER_HOST_FACTOR: Double = 3.0
    var MAX_CONN_PER_HOST: Int = (Runtime.getRuntime.availableProcessors() * MAX_CONN_PER_HOST_FACTOR).toInt max 1
    var THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER: Int = 1500

    /**
      * call before any mongo connection initialized
      */
    def configureAsLongWaiting(): Unit = {
      DefaultSettings.MONGO_CONN.CONN_MAX_WAIT_TIMEOUT = 1000 * 60
      DefaultSettings.MONGO_CONN.CONN_SOCKET_TIMEOUT = 1000 * 60
      DefaultSettings.MONGO_CONN.MAX_CONN_PER_HOST_FACTOR = 6.0
    }
  }

}

object DefaultSettings extends DefaultSettings