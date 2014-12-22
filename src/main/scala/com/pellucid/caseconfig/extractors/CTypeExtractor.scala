package com.pellucid.caseconfig.extractors

import java.util.concurrent.TimeUnit

import com.pellucid.caseconfig.Bytes
import com.typesafe.config.Config

import scala.collection.JavaConverters._
import scala.concurrent.duration._

trait CTypeExtractor[T] extends ((Config, Option[String]) => T)

object CTypeExtractor extends LowPriorityExtractors1 {

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
   *  * Config, List[Config]
   */
  implicit val stringExtractor: CTypeExtractor[String] = simpleCTypeExtractor(_.getString(_))
  implicit val stringListExtractor: CTypeExtractor[List[String]] = simpleCTypeExtractor(_.getStringList(_).asScala.toList)
  implicit val intExtractor: CTypeExtractor[Int] = simpleCTypeExtractor(_.getInt(_))
  implicit val intListExtractor: CTypeExtractor[List[Int]] = simpleCTypeExtractor(_.getIntList(_).asScala.map(_.intValue).toList)
  implicit val booleanExtractor: CTypeExtractor[Boolean] = simpleCTypeExtractor(_.getBoolean(_))
  implicit val booleanListExtractor: CTypeExtractor[List[Boolean]] = simpleCTypeExtractor(_.getBooleanList(_).asScala.map(_.booleanValue).toList)
  implicit val doubleExtractor: CTypeExtractor[Double] = simpleCTypeExtractor(_.getDouble(_))
  implicit val doubleListExtractor: CTypeExtractor[List[Double]] = simpleCTypeExtractor(_.getDoubleList(_).asScala.map(_.doubleValue).toList)
  implicit val longExtractor: CTypeExtractor[Long] = simpleCTypeExtractor(_.getLong(_))
  implicit val longListExtractor: CTypeExtractor[List[Long]] = simpleCTypeExtractor(_.getLongList(_).asScala.map(_.longValue).toList)
  implicit val bytesExtractor: CTypeExtractor[Bytes] = simpleCTypeExtractor((config, path) => Bytes(config.getBytes(path).longValue))
  implicit val bytesListExtractor: CTypeExtractor[List[Bytes]] = simpleCTypeExtractor((config, path) => config.getBytesList(path).asScala.map(v => Bytes(v.longValue)).toList)
  implicit val numberExtractor: CTypeExtractor[Number] = simpleCTypeExtractor(_.getNumber(_))
  implicit val numberListExtractor: CTypeExtractor[List[Number]] = simpleCTypeExtractor(_.getNumberList(_).asScala.toList)
  implicit val durationExtractor: CTypeExtractor[Duration] = simpleCTypeExtractor(_.getDuration(_, TimeUnit.MILLISECONDS).milliseconds)
  implicit val durationListExtractor: CTypeExtractor[List[Duration]] = simpleCTypeExtractor(_.getDurationList(_, TimeUnit.MILLISECONDS).asScala.map(_.longValue.milliseconds).toList)
  implicit val configExtractor: CTypeExtractor[Config] = simpleCTypeExtractor(_.getConfig(_))
  implicit val configListExtractor: CTypeExtractor[List[Config]] = simpleCTypeExtractor(_.getConfigList(_).asScala.toList)

  /**
   * Extractor for an Option[T: CTypeExtractor]
   */
  implicit def optionExtractor[T: CTypeExtractor]: CTypeExtractor[Option[T]] =
    simpleCTypeExtractor { (config, path) =>
      if (config.hasPath(path)) {
        Some(implicitly[CTypeExtractor[T]].apply(config, Some(path)))
      } else {
        None
      }
    }
}

trait LowPriorityExtractors1 extends LowPriorityExtractors0 {

  /**
   * Extractor for a List[A], where A has no simple or option extractor, so it
   * must be a case class.
   */
  implicit def caseClassListExtractor[T: CTypeExtractor]: CTypeExtractor[List[T]] =
    simpleCTypeExtractor(_.getConfigList(_).asScala.map(implicitly[CTypeExtractor[T]].apply(_, None)).toList)
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
