package ca.mrvisser.caseconfig

import com.typesafe.config.Config

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
  stringList: Option[List[String]],
  int: Option[Int],
  intList: Option[List[Int]],
  boolean: Option[Boolean],
  booleanList: Option[List[Boolean]],
  duration: Option[Duration],
  durationList: Option[List[Duration]],
  number: Option[Number],
  numberList: Option[List[Number]],
  double: Option[Double],
  doubleList: Option[List[Double]],
  long: Option[Long],
  longList: Option[List[Long]],
  bytes: Option[Bytes],
  bytesList: Option[List[Bytes]]
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
  stringList: Option[List[String]],
  int: Option[Int],
  intList: Option[List[Int]],
  boolean: Option[Boolean],
  booleanList: Option[List[Boolean]],
  all: Option[AllSimpleTypesRequired],
  allList: Option[List[AllSimpleTypesRequired]],
  duration: Option[Duration],
  durationList: Option[List[Duration]],
  number: Option[Number],
  numberList: Option[List[Number]],
  double: Option[Double],
  doubleList: Option[List[Double]],
  long: Option[Long],
  longList: Option[List[Long]],
  bytes: Option[Bytes],
  bytesList: Option[List[Bytes]]
)

case class TestHyphenatedName(`test-hyphenated-name`: `Test-Hyphenated-Name`)
case class `Test-Hyphenated-Name`(`hyphenated-name-value`: Int)

case class TestConfigExtractorRequired(allWithCase0Config: Config,
  allConfigs: List[Config])

case class TestConfigExtractorOptional(allWithCase0Config: Option[Config],
  allConfigs: Option[List[Config]])
