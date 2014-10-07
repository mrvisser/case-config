package com.pellucid

import com.typesafe.config.Config
import scala.reflect.runtime.{universe => ru}

package object caseconfig {

  /**
   * Enhances the [[Config]] type to be able to get a configured object of some
   * provided generic type.
   *
   * Example:
   *
   * ```scala
   * case class MyConfig(host: String, port: Option[Int])
   *
   * val config = ConfigFactory.load()
   * config.get[MyConfig]("myConfig")
   * ```
   *
   * Acceptable types to parse from a config object are:
   *
   *  * Simple Types:
   *      * String
   *      * Int
   *      * Boolean
   *      * Duration
   *      * Number
   *  * Case Class, whose field types can be an acceptable type
   *  * Option of any of the above types
   */
  implicit class TypelevelConfig2CaseConfig(config: Config) {
    def get[T: ru.TypeTag](path: String): T =
      Types.fromTpe(ru.weakTypeOf[T]).get(config, path).asInstanceOf[T]
  }
}
