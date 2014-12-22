package com.pellucid.caseconfig

import com.typesafe.config.{Config, ConfigException, ConfigFactory}
import org.scalatest.{Matchers, FunSpec}

import scala.concurrent.duration._
import scala.language.postfixOps

class TypelevelConfig2CaseConfigSpec extends FunSpec with Matchers {

  implicit class double2Bytes(num: Double) {
    def kb = Bytes((num * 1000).toLong)
    def mb = Bytes((num * 1000 * 1000).toLong)
    def gb = Bytes((num * 1000 * 1000 * 1000).toLong)
  }

  val allSimpleTypes0Required = AllSimpleTypesRequired(
    "hello",
    List("a", "b", "c", "d"),
    0,
    List(0, 1, 2, 3),
    false,
    List(true, false),
    5 minutes,
    List[Duration](1 second, 2 seconds, 5 minutes),
    2.5,
    List[Number](0, 1.5, 2.4),
    2.5,
    List[Double](0, 1.5, 2.4),
    25,
    List[Long](0, 15, 24),
    25 mb,
    List[Bytes](0 gb, 15 gb, 24 gb)
  )

  val allSimpleTypes0Optional = AllSimpleTypesOptional(
    Some("hello"),
    Some(List("a", "b", "c", "d")),
    Some(0),
    Some(List(0, 1, 2, 3)),
    Some(false),
    Some(List(true, false)),
    Some(5 minutes),
    Some(List[Duration](1 second, 2 seconds, 5 minutes)),
    Some(2.5),
    Some(List[Number](0, 1.5, 2.4)),
    Some(2.5),
    Some(List[Double](0, 1.5, 2.4)),
    Some(25),
    Some(List[Long](0, 15, 24)),
    Some(25 mb),
    Some(List[Bytes](0 gb, 15 gb, 24 gb))
  )

  val allSimpleTypes0OptionalEmpty = AllSimpleTypesOptional(
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None
  )

  val allSimpleTypes1Required = AllSimpleTypesRequired(
    "world",
    List("1", "2", "3", "4"),
    1,
    List(3, 2, 1, 0),
    true,
    List(false, true),
    10 minutes,
    List[Duration](5 minutes, 2 seconds, 1 second),
    8,
    List[Number](2.4, 8, 1),
    8.8,
    List[Double](2.4, 8.8, 1.1),
    88,
    List[Long](24, 88, 11),
    88 mb,
    List[Bytes](2.4 gb, 8.8 gb, 1.1 gb)
  )

  val allSimpleTypes1Optional = AllSimpleTypesOptional(
    Some("world"),
    Some(List("1", "2", "3", "4")),
    Some(1),
    Some(List(3, 2, 1, 0)),
    Some(true),
    Some(List(false, true)),
    Some(10 minutes),
    Some(List[Duration](5 minutes, 2 seconds, 1 second)),
    Some(8),
    Some(List[Number](2.4, 8, 1)),
    Some(8.8),
    Some(List[Double](2.4, 8.8, 1.1)),
    Some(88),
    Some(List[Long](24, 88, 11)),
    Some(88 mb),
    Some(List[Bytes](2.5 gb, 8.8 gb, 1.1 gb))
  )

  val allWithCase0Required = AllSimpleTypesWithCaseRequired(
    "hello",
    List("a", "b", "c", "d"),
    0,
    List(0, 1, 2, 3),
    false,
    List(true, false),
    allSimpleTypes0Required,
    List(allSimpleTypes0Required, allSimpleTypes1Required),
    5 minutes,
    List[Duration](1 second, 2 seconds, 5 minutes),
    2.5,
    List[Number](0, 1.5, 2.4),
    2.5,
    List[Double](0, 1.5, 2.4),
    25,
    List[Long](0, 15, 24),
    25 mb,
    List[Bytes](0 gb, 15 gb, 24 gb)
  )

  val allWithCase0Optional = AllSimpleTypesWithCaseOptional(
    Some("hello"),
    Some(List("a", "b", "c", "d")),
    Some(0),
    Some(List(0, 1, 2, 3)),
    Some(false),
    Some(List(true, false)),
    Some(allSimpleTypes0Required),
    Some(List(allSimpleTypes0Required, allSimpleTypes1Required)),
    Some(5 minutes),
    Some(List[Duration](1 second, 2 seconds, 5 minutes)),
    Some(2.5),
    Some(List[Number](0, 1.5, 2.4)),
    Some(2.5),
    Some(List[Double](0, 1.5, 2.4)),
    Some(25),
    Some(List[Long](0, 15, 24)),
    Some(25 mb),
    Some(List[Bytes](0 gb, 15 gb, 24 gb))
  )

  val allWithCase0OptionalEmpty = AllSimpleTypesWithCaseOptional(
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None
  )

  describe("get()") {

    it("should not work on non-case class types") {
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[String])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Int])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Boolean])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Duration])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Number])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Double])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Long])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Bytes])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Config])

      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[String]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[Int]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[Boolean]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[Duration]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[Number]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[Double]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[Long]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[Bytes]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[Config]])

      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[String]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[Int]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[Boolean]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[Duration]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[Number]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[Double]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[Long]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[Bytes]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[List[Config]])

      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[String]]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[Int]]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[Boolean]]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[Duration]]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[Number]]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[Double]]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[Long]]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[Bytes]]])
      intercept[IllegalArgumentException](config.getConfig("allSimpleTypes0").get[Option[List[Config]]])
    }

    it("should parse a case class from config recursively with all known simple types") {
      config.getConfig("allSimpleTypes0").get[AllSimpleTypesRequired] should be(allSimpleTypes0Required)
      config.getConfig("allSimpleTypes1").get[AllSimpleTypesRequired] should be(allSimpleTypes1Required)
      config.getConfig("allWithCase0").get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)
      config.getConfig("allWithCase0").get[AllSimpleTypesWithCaseOptional] should be(allWithCase0Optional)

      // Ensure raw Configs are extracted properly
      val TestConfigExtractorRequired(allWithCaseRequired0Config, allConfigs) = config.getConfig("config").get[TestConfigExtractorRequired]
      allWithCaseRequired0Config.get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)
      allConfigs.size should be(3)
      allConfigs(0).get[AllSimpleTypesRequired] should be(allSimpleTypes0Required)
      allConfigs(1).get[AllSimpleTypesRequired] should be(allSimpleTypes1Required)
      allConfigs(2).get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)

      // Ensure options of raw Configs are extracted properly
      val TestConfigExtractorOptional(allWithCaseOptional0ConfigOpt, allConfigsOpt) = config.getConfig("config").get[TestConfigExtractorOptional]
      allWithCaseOptional0ConfigOpt.get.get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)
      allConfigsOpt.get.size should be(3)
      allConfigsOpt.get(0).get[AllSimpleTypesRequired] should be(allSimpleTypes0Required)
      allConfigsOpt.get(1).get[AllSimpleTypesRequired] should be(allSimpleTypes1Required)
      allConfigsOpt.get(2).get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)
    }

    it("should parse an empty config into all optional values if allowed by config object") {
      config.getConfig("empty").get[AllSimpleTypesOptional] should be(allSimpleTypes0OptionalEmpty)
      config.getConfig("empty").get[AllSimpleTypesWithCaseOptional] should be(allWithCase0OptionalEmpty)
      config.getConfig("empty").get[TestConfigExtractorOptional] should be(TestConfigExtractorOptional(None, None))
    }

    it("should throw a 'missing' config exception if required fields aren't specified") {
      intercept[ConfigException.Missing](config.getConfig("empty").get[AllSimpleTypesRequired])
      intercept[ConfigException.Missing](config.getConfig("empty").get[TestConfigExtractorRequired])
    }

    it("should throw a proper config exception if a field type is invalid") {
      intercept[ConfigException.WrongType](config.getConfig("invalidStringList").get[AllSimpleTypesOptional])
      intercept[ConfigException.WrongType](config.getConfig("invalidInt").get[AllSimpleTypesOptional])
      intercept[ConfigException.WrongType](config.getConfig("invalidIntList").get[AllSimpleTypesOptional])
      intercept[ConfigException.WrongType](config.getConfig("invalidBoolean").get[AllSimpleTypesOptional])
      intercept[ConfigException.WrongType](config.getConfig("invalidBooleanList").get[AllSimpleTypesOptional])
      intercept[ConfigException.BadValue](config.getConfig("invalidDuration").get[AllSimpleTypesOptional])
      intercept[ConfigException.BadValue](config.getConfig("invalidDurationList").get[AllSimpleTypesOptional])
      intercept[ConfigException.WrongType](config.getConfig("invalidNumber").get[AllSimpleTypesOptional])
      intercept[ConfigException.WrongType](config.getConfig("invalidNumberList").get[AllSimpleTypesOptional])
      intercept[ConfigException.WrongType](config.getConfig("invalidAll").get[AllSimpleTypesWithCaseOptional])
      intercept[ConfigException.WrongType](config.getConfig("invalidAllList").get[AllSimpleTypesWithCaseOptional])

      // Expected object but received string
      intercept[ConfigException.WrongType](config.getConfig("invalidConfig0").get[TestConfigExtractorOptional])

      // Expected object but received array
      intercept[ConfigException.WrongType](config.getConfig("invalidConfig1").get[TestConfigExtractorOptional])

      // Expected array of objects but received string
      intercept[ConfigException.WrongType](config.getConfig("invalidConfigList0").get[TestConfigExtractorOptional])

      // Expected array of objects but received array containing a string
      intercept[ConfigException.WrongType](config.getConfig("invalidConfigList1").get[TestConfigExtractorOptional])

      // Expected array of objects but received array containing an array
      intercept[ConfigException.WrongType](config.getConfig("invalidConfigList2").get[TestConfigExtractorOptional])
    }
  }

  describe("get(String)") {

    it("should parse an optional string") {
      config.get[Option[String]]("allSimpleTypes0.string") should be(allSimpleTypes0Optional.string)
    }

    it("should parse a config path into simple types directly") {
      config.get[String]("allSimpleTypes0.string") should be(allSimpleTypes0Required.string)
      config.get[Int]("allSimpleTypes0.int") should be(allSimpleTypes0Required.int)
      config.get[Boolean]("allSimpleTypes0.boolean") should be(allSimpleTypes0Required.boolean)
      config.get[Duration]("allSimpleTypes0.duration") should be(allSimpleTypes0Required.duration)
      config.get[Number]("allSimpleTypes0.number") should be(allSimpleTypes0Required.number)
      config.get[Config]("config.allWithCase0Config").get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)

      config.get[Option[String]]("allSimpleTypes0.string") should be(allSimpleTypes0Optional.string)
      config.get[Option[Int]]("allSimpleTypes0.int") should be(allSimpleTypes0Optional.int)
      config.get[Option[Boolean]]("allSimpleTypes0.boolean") should be(allSimpleTypes0Optional.boolean)
      config.get[Option[Duration]]("allSimpleTypes0.duration") should be(allSimpleTypes0Optional.duration)
      config.get[Option[Number]]("allSimpleTypes0.number") should be(allSimpleTypes0Optional.number)
      config.get[Option[Config]]("config.allWithCase0Config").get.get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)
      config.get[Option[Config]]("config.non-existing") should be(None)

      config.get[List[String]]("allSimpleTypes0.stringList") should be(allSimpleTypes0Required.stringList)
      config.get[List[Int]]("allSimpleTypes0.intList") should be(allSimpleTypes0Required.intList)
      config.get[List[Boolean]]("allSimpleTypes0.booleanList") should be(allSimpleTypes0Required.booleanList)
      config.get[List[Duration]]("allSimpleTypes0.durationList") should be(allSimpleTypes0Required.durationList)
      config.get[List[Number]]("allSimpleTypes0.numberList") should be(allSimpleTypes0Required.numberList)

      // Ensure raw Config can be parsed directly from path properly
      val allConfigs = config.get[List[Config]]("config.allConfigs")
      allConfigs.size should be(3)
      allConfigs(0).get[AllSimpleTypesRequired] should be(allSimpleTypes0Required)
      allConfigs(1).get[AllSimpleTypesRequired] should be(allSimpleTypes1Required)
      allConfigs(2).get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)

      config.get[Option[List[String]]]("allSimpleTypes0.stringList") should be(allSimpleTypes0Optional.stringList)
      config.get[Option[List[Int]]]("allSimpleTypes0.intList") should be(allSimpleTypes0Optional.intList)
      config.get[Option[List[Boolean]]]("allSimpleTypes0.booleanList") should be(allSimpleTypes0Optional.booleanList)
      config.get[Option[List[Duration]]]("allSimpleTypes0.durationList") should be(allSimpleTypes0Optional.durationList)
      config.get[Option[List[Number]]]("allSimpleTypes0.numberList") should be(allSimpleTypes0Optional.numberList)

      // Ensure optional raw Config can be parsed directly from path properly
      val allConfigsOpt = config.get[Option[List[Config]]]("config.allConfigs")
      allConfigsOpt.get.size should be(3)
      allConfigsOpt.get(0).get[AllSimpleTypesRequired] should be(allSimpleTypes0Required)
      allConfigsOpt.get(1).get[AllSimpleTypesRequired] should be(allSimpleTypes1Required)
      allConfigsOpt.get(2).get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)

      config.get[Option[List[Config]]]("config.non-existing") should be(None)
    }

    it("should parse a config recursively with all known simple types") {
      config.get[AllSimpleTypesRequired]("allSimpleTypes0") should be(allSimpleTypes0Required)
      config.get[AllSimpleTypesRequired]("allSimpleTypes1") should be(allSimpleTypes1Required)
      config.get[AllSimpleTypesWithCaseRequired]("allWithCase0") should be(allWithCase0Required)
      config.get[AllSimpleTypesWithCaseOptional]("allWithCase0") should be(allWithCase0Optional)

      // Ensure raw Configs are extracted properly
      val TestConfigExtractorRequired(allWithCaseRequired0Config, allConfigs) = config.get[TestConfigExtractorRequired]("config")
      allWithCaseRequired0Config.get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)
      allConfigs.size should be(3)
      allConfigs(0).get[AllSimpleTypesRequired] should be(allSimpleTypes0Required)
      allConfigs(1).get[AllSimpleTypesRequired] should be(allSimpleTypes1Required)
      allConfigs(2).get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)

      // Ensure options of raw Configs are extracted properly
      val TestConfigExtractorOptional(allWithCaseOptional0ConfigOpt, allConfigsOpt) = config.get[TestConfigExtractorOptional]("config")
      allWithCaseOptional0ConfigOpt.get.get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)
      allConfigsOpt.get.size should be(3)
      allConfigsOpt.get(0).get[AllSimpleTypesRequired] should be(allSimpleTypes0Required)
      allConfigsOpt.get(1).get[AllSimpleTypesRequired] should be(allSimpleTypes1Required)
      allConfigsOpt.get(2).get[AllSimpleTypesWithCaseRequired] should be(allWithCase0Required)
    }

    it("should parse an empty config into all optional values if allowed by config object") {
      config.get[AllSimpleTypesOptional]("empty") should be(allSimpleTypes0OptionalEmpty)
      config.get[AllSimpleTypesWithCaseOptional]("empty") should be(allWithCase0OptionalEmpty)
      config.get[TestConfigExtractorOptional]("empty") should be(TestConfigExtractorOptional(None, None))
    }

    it("should throw a 'missing' config exception if required fields aren't specified") {
      intercept[ConfigException.Missing](config.get[AllSimpleTypesRequired]("empty"))
      intercept[ConfigException.Missing](config.get[TestConfigExtractorRequired]("empty"))
    }

    it("should throw a proper config exception if a field type is invalid") {
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidStringList"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidInt"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidIntList"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidBoolean"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidBooleanList"))
      intercept[ConfigException.BadValue](config.get[AllSimpleTypesOptional]("invalidDuration"))
      intercept[ConfigException.BadValue](config.get[AllSimpleTypesOptional]("invalidDurationList"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidNumber"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidNumberList"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidDouble"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidDoubleList"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidLong"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidLongList"))
      intercept[ConfigException.BadValue](config.get[AllSimpleTypesOptional]("invalidBytes"))
      intercept[ConfigException.BadValue](config.get[AllSimpleTypesOptional]("invalidBytesList"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesWithCaseOptional]("invalidAll"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesWithCaseOptional]("invalidAllList"))

      // Expected object but received string
      intercept[ConfigException.WrongType](config.get[TestConfigExtractorOptional]("invalidConfig0"))

      // Expected object but received array
      intercept[ConfigException.WrongType](config.get[TestConfigExtractorOptional]("invalidConfig1"))

      // Expected array of objects but received string
      intercept[ConfigException.WrongType](config.get[TestConfigExtractorOptional]("invalidConfigList0"))

      // Expected array of objects but received array containing a string
      intercept[ConfigException.WrongType](config.get[TestConfigExtractorOptional]("invalidConfigList1"))

      // Expected array of objects but received array containing an array
      intercept[ConfigException.WrongType](config.get[TestConfigExtractorOptional]("invalidConfigList2"))
    }

    it("should parse a hyphenated config name without any issues") {
      config.get[TestHyphenatedName] should be(TestHyphenatedName(`Test-Hyphenated-Name`(42)))
    }
  }

  private def config = ConfigFactory.load().getConfig("test")
}
