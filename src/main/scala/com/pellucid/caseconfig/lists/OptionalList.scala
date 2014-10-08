package com.pellucid.caseconfig.lists

import scala.reflect.runtime.{universe => ru}

case class OptionalList[+T: ru.TypeTag](o: Option[List[T]]) {
  def head: T = {
    o match {
      case None => null.asInstanceOf[T]
      case Some(l) => l.head
    }
  }
}