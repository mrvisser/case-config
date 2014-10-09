package com.pellucid.caseconfig.types

import java.util.concurrent.TimeUnit

import com.pellucid.caseconfig.Bytes
import com.pellucid.caseconfig.lists.OptionalList
import com.typesafe.config.Config

import scala.collection.JavaConversions._
import scala.concurrent.duration.Duration
import scala.reflect.runtime.{universe => ru}

abstract class Types[T: ru.TypeTag] {
  def get(config: Config, path: String): T
}

object Types {

  /**
   * Convenience function for comparing a concrete [[ru.Type]] object with some
   * generic type paramter {{{T}}}
   *
   * @param tpe The reflection type with which to compare
   */
  implicit class TpeCanCompare(tpe: ru.Type) {

    /**
     * @return whether or not generic type [[T]] =:= the [[ru.Type]]
     */
    def is[T: ru.TypeTag] = tpe =:= ru.typeOf[T]

    /**
     * @return whether or not generic type [[T]] <:< the [[ru.Type]]
     */
    def isSub[T: ru.TypeTag] = tpe <:< ru.typeOf[T]
  }

  /**
   * Given an [[ru.Type]], get the config helper we can use to get its list
   * config value.
   */
  def listFromTpe(listInnerFnTpe: ru.Type): Types[_] = {
    val listInnerTpe = listInnerFnTpe.typeSymbol.asType.toType
    if (listInnerTpe.is[String]) {
      StringListType
    } else if (listInnerTpe.is[Int]) {
      IntListType
    } else if (listInnerTpe.is[Boolean]) {
      BooleanListType
    } else if (listInnerTpe.is[Duration]) {
      DurationListType
    } else if (listInnerTpe.is[Number]) {
      NumberListType
    } else if (listInnerTpe.is[Double]) {
      DoubleListType
    } else if (listInnerTpe.is[Long]) {
      // Due to https://issues.scala-lang.org/browse/SI-6474, we have to be
      // unfortunately devious and compare it with a string :(
      if (listInnerFnTpe.toString == s"=> ${ru.typeOf[Bytes].toString}") {
        BytesListType
      } else {
        LongListType
      }
    } else if (isCustomCaseClass(listInnerTpe)) {
      CaseClassListType(CaseClassType(listInnerTpe))
    } else {
      throw new IllegalArgumentException(s"Attempted to parse invalid inner list type: $listInnerTpe")
    }
  }

  /**
   * Given an [[ru.Type]], get the config helper we can use to get its config
   * value.
   */
  def fromTpe(tpe: ru.Type, fnTypeOpt: Option[ru.Type]): Types[_] = {
    if (isCustomCaseClass(tpe)) {
      CaseClassType(tpe)
    } else if (tpe.isSub[Option[Any]]) {
      val optionInnerFnTpe = innerFunctionTpe(tpe, ru.newTermName("get"))
      if (optionInnerFnTpe.typeSymbol.asType.toType.isSub[List[Any]])
        throw new IllegalArgumentException(s"""
          Attempted to get optional type Option[List[T]], which is not supported due to type erasure.\n\n
          Use com.pellucid.caseconfig.lists.OptionalList[T] instead.""")
      OptionType(optionInnerFnTpe)
    } else if (tpe.isSub[OptionalList[Any]]) {
      OptionalListType(innerFunctionTpe(tpe, ru.newTermName("head")))
    } else if (tpe.isSub[List[Any]]) {
      listFromTpe(innerFunctionTpe(tpe, ru.newTermName("head")))
    } else if (tpe.is[String]) {
      StringType
    } else if (tpe.is[Int]) {
      IntType
    } else if (tpe.is[Boolean]) {
      BooleanType
    } else if (tpe.is[Duration]) {
      DurationType
    } else if (tpe.is[Number]) {
      NumberType
    } else if (tpe.is[Double]) {
      DoubleType
    } else if (tpe.is[Long]) {
      // Due to https://issues.scala-lang.org/browse/SI-6474, we have to be
      // unfortunately devious and compare it with a string :(
      fnTypeOpt match {
        case None =>
          if (tpe.toString == ru.typeOf[Bytes].toString) {
            BytesType
          } else {
            LongType
          }
        case Some(fnTpe) =>
          if (fnTpe.toString == s"=> ${ru.typeOf[Bytes].toString}") {
            BytesType
          } else {
            LongType
          }
      }
    } else {
      throw new IllegalArgumentException(s"Attempted to parse invalid type: $tpe")
    }
  }

  case object StringListType extends Types[List[String]] {
    def get(config: Config, path: String) = config.getStringList(path).toList
  }

  case object IntListType extends Types[List[Int]] {
    def get(config: Config, path: String) = config.getIntList(path).map(_.toInt).toList
  }

  case object BooleanListType extends Types[List[Boolean]] {
    def get(config: Config, path: String) = config.getBooleanList(path).map(_.booleanValue()).toList
  }

  case object DurationListType extends Types[List[Duration]] {
    def get(config: Config, path: String) =
      config.getDurationList(path, TimeUnit.MILLISECONDS)
        .map(Duration(_, TimeUnit.MILLISECONDS)).toList
  }

  case object NumberListType extends Types[List[Number]] {
    def get(config: Config, path: String) = config.getNumberList(path).toList
  }

  case object DoubleListType extends Types[List[Double]] {
    def get(config: Config, path: String) = config.getDoubleList(path).map(_.toDouble).toList
  }

  case object LongListType extends Types[List[Long]] {
    def get(config: Config, path: String) = config.getLongList(path).map(_.toLong).toList
  }

  case object BytesListType extends Types[List[Bytes]] {
    def get(config: Config, path: String) = config.getBytesList(path).map(_.toLong).toList
  }

  case class CaseClassListType(caseClassType: CaseClassType) extends Types[List[Any]] {
    def get(config: Config, path: String) =
      config.getConfigList(path).map(caseClassType.get(_, "")).toList
  }

  case class OptionalListType(fnTpe: ru.Type) extends Types[OptionalList[Any]] {
    def get(config: Config, path: String) = {
      config.hasPath(path) match {
        case true => OptionalList(Some(listFromTpe(fnTpe).get(config, path).asInstanceOf[List[Any]]))
        case false => OptionalList(None)
      }
    }
  }

  case class CaseClassType(tpe: ru.Type) extends Types[Any] {
    def get(config: Config, path: String) = {
      val targetConfig = if (path.isEmpty) config else config.getConfig(path)
      val args = List.newBuilder[Any]
      tpe.declarations.foreach {
        case m: ru.MethodSymbolApi if m.isCaseAccessor =>
          args += fromTpe(m.returnType, None).get(targetConfig, m.name.decoded)
        case _ =>
      }

      invokeDynamic(tpe, ru.nme.CONSTRUCTOR, config.getClass.getClassLoader, args.result())
    }
  }

  case class OptionType(innerFnTpe: ru.Type) extends Types[Option[Any]] {
    def get(config: Config, path: String): Option[Any] = {
      config.hasPath(path) match {
        case true => Some(fromTpe(innerFnTpe.typeSymbol.asType.toType, Some(innerFnTpe)).get(config, path))
        case false => None
      }
    }
  }

  case object StringType extends Types[String] {
    def get(config: Config, path: String) = config.getString(path)
  }

  case object IntType extends Types[Int] {
    def get(config: Config, path: String) = config.getInt(path)
  }

  case object BooleanType extends Types[Boolean] {
    def get(config: Config, path: String) = config.getBoolean(path)
  }

  case object DurationType extends Types[Duration] {
    def get(config: Config, path: String) = Duration(
      config.getDuration(path, TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
  }

  case object NumberType extends Types[Number] {
    def get(config: Config, path: String) = config.getNumber(path)
  }

  case object DoubleType extends Types[Double] {
    def get(config: Config, path: String) = config.getDouble(path)
  }

  case object LongType extends Types[Long] {
    def get(config: Config, path: String) = config.getLong(path)
  }

  case object BytesType extends Types[Long] {
    def get(config: Config, path: String) = config.getBytes(path)
  }

  private def isCustomCaseClass(tpe: ru.Type): Boolean =
    tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isCaseClass && !tpe.isSub[OptionalList[Any]]

  private def innerTpe(tpe: ru.Type, name: ru.Name): ru.Type =
    innerFunctionTpe(tpe, name).typeSymbol.asType.toType

  private def innerFunctionTpe(tpe: ru.Type, name: ru.Name): ru.Type =
    tpe.member(name).typeSignatureIn(tpe)

  private def invokeDynamic(tpe: ru.Type, name: ru.Name, cl: ClassLoader, args: List[Any]): Any = {
    val cm = tpe.declaration(name).asMethod
    val rm = ru.runtimeMirror(cl)
    val rc = rm.reflectClass(tpe.typeSymbol.asClass)
    rc.reflectConstructor(cm)(args: _*)
  }
}
