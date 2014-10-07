package com.pellucid.caseconfig

import scala.concurrent.duration.Duration

case class AllSimpleTypesRequired(string: String, int: Int, boolean: Boolean, duration: Duration, number: Number)
case class AllSimpleTypesOptional(string: Option[String], int: Option[Int], boolean: Option[Boolean], duration: Option[Duration], number: Option[Number])

case class AllSimpleTypesWithCaseRequired(string: String, int: Int, boolean: Boolean, all: AllSimpleTypesRequired, duration: Duration, number: Number)
case class AllSimpleTypesWithCaseOptional(string: Option[String], int: Option[Int], boolean: Option[Boolean], all: Option[AllSimpleTypesRequired], duration: Option[Duration], number: Option[Number])