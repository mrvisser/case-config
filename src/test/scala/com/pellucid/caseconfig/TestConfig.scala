package com.pellucid.caseconfig

import com.pellucid.caseconfig.lists.OptionalList

import scala.concurrent.duration.Duration

case class AllSimpleTypesRequired(
  string: String,
  stringList: List[String],
  int: Int,
  intList: List[Int],
  boolean: Boolean,
  booleanList: List[Boolean],
  duration: Duration,
  durationList: List[Duration],
  number: Number,
  numberList: List[Number]
)

case class AllSimpleTypesOptional(
  string: Option[String],
  stringList: OptionalList[String],
  int: Option[Int],
  intList: OptionalList[Int],
  boolean: Option[Boolean],
  booleanList: OptionalList[Boolean],
  duration: Option[Duration],
  durationList: OptionalList[Duration],
  number: Option[Number],
  numberList: OptionalList[Number]
)

case class AllSimpleTypesWithCaseRequired(
  string: String,
  stringList: List[String],
  int: Int,
  intList: List[Int],
  boolean: Boolean,
  booleanList: List[Boolean],
  all: AllSimpleTypesRequired,
  allList: List[AllSimpleTypesRequired],
  duration: Duration,
  durationList: List[Duration],
  number: Number,
  numberList: List[Number]
)

case class AllSimpleTypesWithCaseOptional(
  string: Option[String],
  stringList: OptionalList[String],
  int: Option[Int],
  intList: OptionalList[Int],
  boolean: Option[Boolean],
  booleanList: OptionalList[Boolean],
  all: Option[AllSimpleTypesRequired],
  allList: OptionalList[AllSimpleTypesRequired],
  duration: Option[Duration],
  durationList: OptionalList[Duration],
  number: Option[Number],
  numberList: OptionalList[Number]
)
