package com.pellucid.caseconfig.lists

import scala.reflect.runtime.{universe => ru}

/**
 * Utility class that dodges type erasure of Option[List[T]] when trying to have
 * a configuration object that is an optional list of some generic type.
 *
 * @param list  The {{{Option[List[T]]}}} that was parsed from the configuration
 * @tparam T    The generic type of the elements of the list, if it exists
 */
case class OptionalList[+T: ru.TypeTag](list: Option[List[T]]) {

  /**
   * Convenience method to support getting the runtime type of the inner List.
   * The OptionalList.head member's return type is inspected to get the concrete
   * type of T at runtime.
   */
  def head: T = {
    list match {
      case None => null.asInstanceOf[T]
      case Some(list) => list.head
    }
  }
}