package com.pellucid.caseconfig

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
  numberList: List[Number],
  double: Double,
  doubleList: List[Double],
  long: Long,
  longList: List[Long],
  bytes: Bytes,
  bytesList: List[Bytes]
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
  numberList: OptionalList[Number],
  double: Option[Double],
  doubleList: OptionalList[Double],
  long: Option[Long],
  longList: OptionalList[Long],
  bytes: Option[Bytes],
  bytesList: OptionalList[Bytes]
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
  numberList: List[Number],
  double: Double,
  doubleList: List[Double],
  long: Long,
  longList: List[Long],
  bytes: Bytes,
  bytesList: List[Bytes]
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
  numberList: OptionalList[Number],
  double: Option[Double],
  doubleList: OptionalList[Double],
  long: Option[Long],
  longList: OptionalList[Long],
  bytes: Option[Bytes],
  bytesList: OptionalList[Bytes]
)

case class InvalidOptionalList(optionalList: Option[List[String]])

case class TestHyphenatedName(`test-hyphenated-name`: `Test-Hyphenated-Name`)
case class `Test-Hyphenated-Name`(`hyphenated-name-value`: Int)