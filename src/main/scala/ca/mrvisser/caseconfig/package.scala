package ca.mrvisser

import com.typesafe.config.Config

package object caseconfig {

  /**
   * Enhances the [[Config]] type to be able to get a configured object of some
   * provided generic type.
   *
   * Example:
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
    import extractors._

    /**
     * Get the configuration object as the provided case class.
     *
     * @tparam T  The type of case class into which to parse the configuration
     * @return    The case class created with the configuration
     */
    def get[T: CTypeExtractor]: T =
      implicitly[CTypeExtractor[T]].apply(config, None)

    /**
     * Get the configuration value at the provided path as the provided type.
     *
     * @param path  The path in the config object to parse
     * @tparam T    The type as which to parse the config value
     * @return      The object parsed from the config value
     */
    def get[T: CTypeExtractor](path: String): T =
      implicitly[CTypeExtractor[T]].apply(config, Some(path))
  }
}
