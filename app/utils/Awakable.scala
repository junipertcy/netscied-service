package utils

import org.slf4j.LoggerFactory


trait Awakable {

  /**
    * a logger instance of org.slf4j.Logger
    *
    */
  lazy val logger = LoggerFactory.getLogger(this.getClass)

  /**
    * log a tip to tell us '''I am started'''
    *
    * @param name who am i
    * @return the function is successfully executed
    */
  def awake(name: String = "") = {
    logger.info(s"[$name] is waking up ...")
    true
  }
}