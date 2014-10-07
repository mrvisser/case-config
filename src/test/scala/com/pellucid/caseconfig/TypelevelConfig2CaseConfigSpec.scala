package com.pellucid.caseconfig

import com.typesafe.config.{ConfigException, ConfigFactory}
import org.scalatest.{Matchers, FunSpec}

import scala.concurrent.duration._

class TypelevelConfig2CaseConfigSpec extends FunSpec with Matchers {

  describe("get(String)") {

    it("should parse a config recursively with all known simple types") {
      config.get[AllSimpleTypesWithCaseRequired]("allWithCase") should be(
        AllSimpleTypesWithCaseRequired("hello", 0, false, AllSimpleTypesRequired("world", 1, true, 1 minute, 1000), 5 minutes, 2.5)
      )
    }

    it("should parse an empty config into all optional values if allowed by config object") {
      config.get[AllSimpleTypesWithCaseOptional]("empty") should be(
        AllSimpleTypesWithCaseOptional(None, None, None, None, None, None)
      )

      config.get[AllSimpleTypesOptional]("empty") should be(
        AllSimpleTypesOptional(None, None, None, None, None)
      )
    }

    it("should parse a full config into a Some of all optional values") {
      config.get[AllSimpleTypesWithCaseOptional]("allWithCase") should be(
        AllSimpleTypesWithCaseOptional(Some("hello"), Some(0), Some(false),
          Some(AllSimpleTypesRequired("world", 1, true, 1 minute, 1000)),
          Some(5 minutes), Some(2.5))
      )
    }
    
    it("should gracefully ignore unused config values") {
      config.get[AllSimpleTypesRequired]("allSimpleTypesWithUnused") should be(
        AllSimpleTypesRequired("hello", 0, false, 5 minutes, 2.5)
      )
    }

    it("should throw a 'missing' config exception if required fields aren't specified") {
      intercept[ConfigException.Missing](config.get[AllSimpleTypesRequired]("empty"))
    }

    it("should throw a proper config exception if a field type is invalid") {
      // TODO: Return an Either[E, T] instead of throwing an exception
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidInt"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidBoolean"))
      intercept[ConfigException.BadValue](config.get[AllSimpleTypesOptional]("invalidDuration"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesOptional]("invalidNumber"))
      intercept[ConfigException.WrongType](config.get[AllSimpleTypesWithCaseOptional]("invalidAll"))
    }
  }

  private def config = ConfigFactory.load().getConfig("test")
}
