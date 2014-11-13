package com.pellucid

import com.typesafe.config.Config
import java.util.concurrent.TimeUnit
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.reflect.macros.blackbox

package object caseconfig {

  case class Bytes(bytes: Long)

  object extractors {

    trait CTypeExtractor[T] extends ((Config, Option[String]) => T)

    object CTypeExtractor extends LowPriorityExtractors1 {

      def extract[T: CTypeExtractor](config: Config, pathOpt: Option[String]) =
        implicitly[CTypeExtractor[T]].apply(config, pathOpt)

      /**
       * Extractors for simple types:
       *
       *  * String, List[String]
       *  * Int, List[Int]
       *  * Boolean, List[Boolean]
       *  * Double, List[Double]
       *  * Long, List[Long]
       *  * Bytes, List[Bytes]
       *  * Number, List[Number]
       *  * Duration, List[Duration]
       */
      implicit val stringExtractor: CTypeExtractor[String] = simpleCTypeExtractor(_.getString(_))
      implicit val stringListExtractor: CTypeExtractor[List[String]] = simpleCTypeExtractor(_.getStringList(_).toList)
      implicit val intExtractor: CTypeExtractor[Int] = simpleCTypeExtractor(_.getInt(_))
      implicit val intListExtractor: CTypeExtractor[List[Int]] = simpleCTypeExtractor(_.getIntList(_).map(_.intValue).toList)
      implicit val booleanExtractor: CTypeExtractor[Boolean] = simpleCTypeExtractor(_.getBoolean(_))
      implicit val booleanListExtractor: CTypeExtractor[List[Boolean]] = simpleCTypeExtractor(_.getBooleanList(_).map(_.booleanValue).toList)
      implicit val doubleExtractor: CTypeExtractor[Double] = simpleCTypeExtractor(_.getDouble(_))
      implicit val doubleListExtractor: CTypeExtractor[List[Double]] = simpleCTypeExtractor(_.getDoubleList(_).map(_.doubleValue).toList)
      implicit val longExtractor: CTypeExtractor[Long] = simpleCTypeExtractor(_.getLong(_))
      implicit val longListExtractor: CTypeExtractor[List[Long]] = simpleCTypeExtractor(_.getLongList(_).map(_.longValue).toList)
      implicit val bytesExtractor: CTypeExtractor[Bytes] = simpleCTypeExtractor((config, path) => Bytes(config.getBytes(path).longValue))
      implicit val bytesListExtractor: CTypeExtractor[List[Bytes]] = simpleCTypeExtractor((config, path) => config.getBytesList(path).map(v => Bytes(v.longValue)).toList)
      implicit val numberExtractor: CTypeExtractor[Number] = simpleCTypeExtractor(_.getNumber(_))
      implicit val numberListExtractor: CTypeExtractor[List[Number]] = simpleCTypeExtractor(_.getNumberList(_).toList)
      implicit val durationExtractor: CTypeExtractor[Duration] = simpleCTypeExtractor(_.getDuration(_, TimeUnit.MILLISECONDS).milliseconds)
      implicit val durationListExtractor: CTypeExtractor[List[Duration]] = simpleCTypeExtractor(_.getDurationList(_, TimeUnit.MILLISECONDS).map(_.longValue.milliseconds).toList)

      /**
       * Extractor for an Option[T: CTypeExtractor]
       */
      implicit def optionExtractor[T: CTypeExtractor]: CTypeExtractor[Option[T]] =
        simpleCTypeExtractor { (config, path) =>
          if (config.hasPath(path)) {
            Some(config.get[T](path))
          } else {
            None
          }
        }
    }

    trait LowPriorityExtractors1 extends LowPriorityExtractors0 {

      /**
       * Extractor for a List[A], where A has no simple extractor, so it must be
       * a case class.
       */
      implicit def caseClassListExtractor[T: CTypeExtractor]: CTypeExtractor[List[T]] =
        simpleCTypeExtractor(_.getConfigList(_).map(_.get[T]).toList)
    }

    trait LowPriorityExtractors0 {

      /**
       * Convenience method to transform the `pathOpt: Option[String]` to a
       * `path: String`, throwing a compilation error if `pathOpt` is `None`. For
       * all simple types, the path is actually required so that is when you want
       * to throw an error. For CaseClass types, you can extract a `Config` object
       * directly with no path.
       */
      protected def simpleCTypeExtractor[T](f: (Config, String) => T): CTypeExtractor[T] = {
        new CTypeExtractor[T] {
          def apply(config: Config, pathOpt: Option[String]) = {
            pathOpt match {
              case None => throw new IllegalArgumentException("must provide path for simple type")
              case Some(path) => f(config, path)
            }
          }
        }
      }

      /**
       * Extractor for some non-simple type (i.e., a case class). If `T` turns
       * out to be something other than a case class, compilation will fail.
       */
      implicit def caseClassExtractor[T]: CTypeExtractor[T] = macro ExtractorMacros.caseClassExtractorImpl[T]
    }

    object ExtractorMacros {

      /**
       * An implicit macro that extracts any generic case class `T` from a config
       * object. If `T` is not a case class, compilation is aborted.
       */
      def caseClassExtractorImpl[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[CTypeExtractor[T]] = {
        import c.universe._
        val ccTpe = weakTypeOf[T].typeSymbol

        // Generic type that is not a simple type can only be a case class
        if (!ccTpe.isClass || !ccTpe.asClass.isCaseClass)
          c.abort(c.enclosingPosition, s"$ccTpe is not a simple type or a case class")

        val companion = ccTpe.companion

        // Extract the constructor arguments from the case class
        val ccArgs = ccTpe
          .typeSignature
          .declarations
          .toList
          .collect {
            case term: TermSymbol if term.isVal && term.isCaseAccessor => term
          }
          .map { termSymbol =>
            val path = c.literal(termSymbol.name.decoded.toString)
            // For each case accessor, we want to recursively extract their
            // inner type using an implicit extractor. Note that the `def dummy`
            // portion is in place to jump through some hoops to get the scala
            // compiler to properly perform implicit recursion and not wind up
            // with divergent implicit errors
            q"""
              def dummy(implicit ev: _root_.com.pellucid.caseconfig.extractors.CTypeExtractor[${termSymbol.typeSignature}]) = ev

              _root_.com.pellucid.caseconfig.extractors.CTypeExtractor
                .extract[${termSymbol.typeSignature}](
                  targetConfig,
                  Some($path)
                )(dummy)
            """
          }

        // Create our implicit extractor for the case class type
        val tree = q"""
          new _root_.com.pellucid.caseconfig.extractors.CTypeExtractor[$ccTpe] {
            def apply(config: _root_.com.typesafe.config.Config, pathOpt: Option[String]): $ccTpe = {
              val targetConfig = pathOpt match {
                case None => config
                case Some(path) => config.getConfig(path)
              }

              $companion(..$ccArgs)
            }
          }
        """

        c.Expr[CTypeExtractor[T]](tree)
      }
    }
  }

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
