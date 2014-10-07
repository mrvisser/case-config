package com.pellucid.caseconfig

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config

import scala.concurrent.duration.Duration
import scala.reflect.runtime.{universe => ru}

abstract class Types[T] {
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
    def isSub[T: ru.WeakTypeTag] = tpe <:< ru.weakTypeOf[T]
  }

  /**
   * Given an [[ru.Type]], get the config helper we can use to get its config
   * value.
   */
  def fromTpe(tpe: ru.Type) = {
    if (isCaseClass(tpe)) {
      CaseClassType(tpe)
    } else if (tpe.isSub[Option[Any]]) {
      OptionType(innerTpe(tpe, ru.newTermName("get")))
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
    } else {
      throw new IllegalArgumentException(s"Attempted to parse invalid type: $tpe")
    }
  }

  case class CaseClassType(tpe: ru.Type) extends Types[Any] {
    def get(config: Config, path: String) = {
      val args = List.newBuilder[Any]
      tpe.declarations.foreach {
        case m: ru.MethodSymbolApi if m.isCaseAccessor =>
          args += fromTpe(m.returnType).get(config.getConfig(path), m.name.decoded)
        case _ =>
      }

      invokeDynamic(tpe, ru.nme.CONSTRUCTOR, config.getClass.getClassLoader, args.result())
    }
  }

  case class OptionType(innerTpe: ru.Type) extends Types[Option[Any]] {
    def get(config: Config, path: String): Option[Any] = {
      config.hasPath(path) match {
        case true => Some(fromTpe(innerTpe).get(config, path))
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

  case object ByteType extends Types[Byte] {
    def get(config: Config, path: String) =
      config.getBytes(path).byteValue
  }

  case object NumberType extends Types[Number] {
    def get(config: Config, path: String) = config.getNumber(path)
  }

  private def isCaseClass(tpe: ru.Type): Boolean =
    tpe.typeSymbol.asClass.isCaseClass

  private def innerTpe(tpe: ru.Type, name: ru.Name) =
    tpe.member(ru.newTermName("get")).typeSignatureIn(tpe)
      .typeSymbol.asType.toType


  private def invokeDynamic(tpe: ru.Type, name: ru.Name, cl: ClassLoader, args: List[Any]): Any = {
    val cm = tpe.declaration(name).asMethod
    val rm = ru.runtimeMirror(cl)
    val rc = rm.reflectClass(tpe.typeSymbol.asClass)
    rc.reflectConstructor(cm)(args: _*)
  }
}
