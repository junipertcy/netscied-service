package clients.mongo

import conf.DefaultSettings
import utils.console.ColorForConsole._
import utils.Awakable
import utils.Utils.SafeExec
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClientOptions.Builder
import org.bson.types.ObjectId

trait MongoDBConnector extends Awakable {
  lazy val MONGO_LOGGER_LABEL: String = "MongoDBConnector".withColor(GREEN)
  lazy val MONGO_USER: String = DefaultSettings.MONGO_USER
  lazy val MONGO_DB: String = DefaultSettings.MONGO_DB
  lazy val MONGO_PASS: Array[Char] = DefaultSettings.MONGO_PASS_STR.toCharArray
  lazy val MONGO_ADDRS: Array[(String, Int)] = DefaultSettings.MONGO_ADDR_ARRAY

  lazy val CONN_LIFE_TIMEOUT: Int = DefaultSettings.MONGO_CONN.CONN_LIFE_TIMEOUT
  lazy val CONN_IDLE_TIMEOUT: Int = DefaultSettings.MONGO_CONN.CONN_IDLE_TIMEOUT

  // quick fail ..
  lazy val CONN_MAX_WAIT_TIMEOUT: Int = DefaultSettings.MONGO_CONN.CONN_MAX_WAIT_TIMEOUT
  // quick fail ..
  lazy val CONN_SOCKET_TIMEOUT: Int = DefaultSettings.MONGO_CONN.CONN_SOCKET_TIMEOUT

  lazy val MIN_CONN_PER_HOST_FACTOR: Double = DefaultSettings.MONGO_CONN.MIN_CONN_PER_HOST_FACTOR
  lazy val MIN_CONN_PER_HOST: Int = DefaultSettings.MONGO_CONN.MIN_CONN_PER_HOST

  lazy val MAX_CONN_PER_HOST_FACTOR: Double = DefaultSettings.MONGO_CONN.MAX_CONN_PER_HOST_FACTOR
  lazy val MAX_CONN_PER_HOST: Int = DefaultSettings.MONGO_CONN.MAX_CONN_PER_HOST

  lazy val THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER: Int = DefaultSettings.MONGO_CONN.THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER

  /**
    * database , use `lazy` so that you can override this variable
    * dbPrimary will read primary only which may helps us to get ''recent update''
    */
  lazy val db: MongoDB = mongoConn(MONGO_ADDRS, MONGO_USER, MONGO_DB, MONGO_PASS, primary = false)(MONGO_DB)
  lazy val dbPrimary: MongoDB = mongoConn(MONGO_ADDRS, MONGO_USER, MONGO_DB, MONGO_PASS, primary = true)(MONGO_DB)

  lazy val PRIMARY_ONLY_READ_PREFERENCE = ReadPreference.Primary
  lazy val NEAREST_READ_PREFERENCE = ReadPreference.Nearest
  lazy val DEFAULT_READ_PREFERENCE = NEAREST_READ_PREFERENCE

  //val db: MongoDB = mongoConn(MONGO_HOST, MONGO_PORT, MONGO_USER, MONGO_DB, MONGO_PASS, MONGO_ADDRS)(MONGO_DB)
  lazy val LENGTH_OF_OBJECT_ID = newObjectId().toString.length

  /**
    * create a new mongo db connection
    *
    * @param user   username
    * @param pass   password
    * @param addrs  addres
    * @param dbname db name
    */
  def mongoConn(addrs: Array[(String, Int)], user: String, dbname: String, pass: Array[Char], primary: Boolean = false): MongoClient = {
    //def mongoConn(host: String, port: Int, user: String, dbname: String, pass: Array[Char], addrs: Array[(String, Int)]): MongoClient = {
    //println(MONGO_HOST + "\t" + MONGO_PORT + "\t" + MONGO_USER + "\t" + MONGO_DB) // + "\t" + MONGO_PASS.toString)
    logger.info(s"[$MONGO_LOGGER_LABEL] auth... user:[$user] db:[$dbname] ")
    addrs.foreach(x => logger.info(s"[$MONGO_LOGGER_LABEL] server:" +
      s" ${x._1.toString.withColor(BLUE)}:${x._2.toString.withColor(YELLOW)}"))
    //val server = new ServerAddress(host, port)
    val credential: MongoCredential = MongoCredential.createCredential(user, dbname, pass)
    val optBuilder: Builder = new MongoClientOptions.Builder()
    optBuilder.maxConnectionLifeTime(CONN_LIFE_TIMEOUT)
    optBuilder.maxConnectionIdleTime(CONN_IDLE_TIMEOUT)
    optBuilder.threadsAllowedToBlockForConnectionMultiplier(THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER)
    optBuilder.minConnectionsPerHost(MIN_CONN_PER_HOST)
    optBuilder.connectionsPerHost(MAX_CONN_PER_HOST)
    optBuilder.socketTimeout(CONN_SOCKET_TIMEOUT) // 60 seconds of socket read
    optBuilder.maxWaitTime(CONN_MAX_WAIT_TIMEOUT)
    logger.info(s"[$MONGO_LOGGER_LABEL] timeouts:  " +
      s"life[$CONN_LIFE_TIMEOUT](s), " +
      s"idle[$CONN_IDLE_TIMEOUT](s), " +
      s"sock[$CONN_SOCKET_TIMEOUT](s), " +
      s"wait[$CONN_MAX_WAIT_TIMEOUT](s), " +
      s"min connection[$MIN_CONN_PER_HOST](fact. $MIN_CONN_PER_HOST_FACTOR), " +
      s"max connection[$MAX_CONN_PER_HOST](fact. $MAX_CONN_PER_HOST_FACTOR), " +
      s"threads allowed to block for connection multiplier[$THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER]")
    if (primary)
      optBuilder.readPreference(PRIMARY_ONLY_READ_PREFERENCE)
    else
      optBuilder.readPreference(DEFAULT_READ_PREFERENCE)

    val opt: MongoClientOptions = optBuilder.build()
    val conn: MongoClient = com.mongodb.casbah.Imports.MongoClient(
      replicaSetSeeds = addrs.toList collect {
        case addr: (String, Int) => new ServerAddress(addr._1, addr._2)
      },
      credentials = List(credential),
      options = opt)
    conn
  }


  /**
    * get collection from current db
    *
    * @param name collection name
    */
  def coll(name: String, db: MongoDB = db): MongoCollection = db(name)

  /**
    * create a new object id
    *
    * @return
    */
  def newObjectId(): ObjectId = new ObjectId()

  def isValidObjectId(oid: String) = SafeExec(ObjectId.isValid(oid)).getOrElse(false)
}

object MongoDBConnector extends MongoDBConnector {

}
