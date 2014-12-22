# CaseConfig

CaseConfig is an extension for Typesafe Config that provides a safer and cleaner
approach to reading values from a configuration file. CaseConfig's model-driven
approach of parsing configuration into types provides validation for "required"
fields and field types for free. It also provides a clearer way of accessing
configuration fields using case class values instead of `Config` objects of
`Config` objects, which is analogous to dealing with a heterogeneous `Map` of
`Map`s.

## Set Up

CaseConfig is available for both Scala 2.10 and 2.11. If you are using sbt,
simply add the following to your `build.sbt`:

```
resolvers += "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven"

libraryDependencies += "com.pellucid" %% "framian" % "0.3.3"
```

## Usage

There isn't much to it, so lets just dive into some examples.

### Example 1: Get a required String

Good times when the configuration field exists:
```scala
import com.pellucid.caseconfig._
import com.typesafe.config.ConfigFactory

val config = ConfigFactory.parseString(
        """
        |{
        |   myfield = "my value"
        |}
        """.stripMargin)

val myfield = config.get[String]("myfield")
```

Result: `myfield = "my value"`

An error when it does not exist:
```scala
import com.pellucid.caseconfig._
import com.typesafe.config.ConfigFactory

val config = ConfigFactory.parseString(
        """
        |{
        |   myfield = "my value"
        |}
        """.stripMargin)

val missing = config.get[String]("missing")
```

Result:

```
com.typesafe.config.ConfigException$Missing: No configuration setting found for key 'missing'
  at com.typesafe.config.impl.SimpleConfig.findKey(SimpleConfig.java:124)
  at com.typesafe.config.impl.SimpleConfig.find(SimpleConfig.java:145)
  at com.typesafe.config.impl.SimpleConfig.find(SimpleConfig.java:159)
  at com.typesafe.config.impl.SimpleConfig.find(SimpleConfig.java:164)
  at com.typesafe.config.impl.SimpleConfig.getString(SimpleConfig.java:206)
  at com.pellucid.caseconfig.types.Types$StringType$.get(Types.scala:195)
  at com.pellucid.caseconfig.types.Types$StringType$.get(Types.scala:194)
  at com.pellucid.caseconfig.package$TypelevelConfig2CaseConfig.get(package.scala:52)
  ... 46 elided
```

### Example 2: Get an optional string

To specify that a configuration value is optional, its type should be an
`Option` of its value type. If the field doesn't exist it results in a `None`
rather than a validation error.

```scala
import com.pellucid.caseconfig._
import com.typesafe.config.ConfigFactory

val config = ConfigFactory.parseString(
        """
        |{
        |   myfield = "my value"
        |}
        """.stripMargin)

val myfield = config.get[Option[String]]("missing")
```

Result: `myfield = None`

### Example 3: Get a case class from a configuration object

If a case class is provided, its fields are decomposed and the case class is
instantiated using the fields and values from the configuration object. This
happens recursively, so it is possible to extract case classes nested in case
classes to build complex configuration objects.

```scala
import com.pellucid.caseconfig._
import com.typesafe.config.ConfigFactory

case class CacheConfig(evictionStrategy: String, maxSize: Bytes)
case class ConnectionPoolConfig(servers: List[String], maxConnections: Int, minConnections: Option[Int])
case class MyConfig(pool: ConnectionPoolConfig, cache: Option[CacheConfig], latencyPercentiles: Optional[List[Double]])

val config = ConfigFactory.parseString(
        """
        |{
        |   pool = {
        |       servers = [ "web0.myapp.ninja", "web1.myapp.ninja" ]
        |       maxConnections = 12
        |   }
        |
        |   latencyPercentiles = [ 50, 75, 90, 95, 99, 99.9 ]
        |}
        """.stripMargin)

val myConfig = config.get[MyConfig]
```

Result:

```
myConfig =
    MyConfig(
        ConnectionPoolConfig(
            List(
                "web0.myapp.ninja",
                "web1.myapp.ninja"
            ),
            12,
            None
        ),
        None,
        Some(
            List(50.0, 75.0, 90.0, 95.0, 99.0, 99.9)
        )
    )
```

## Contributors

* Eugene Burmako: Macro support
* Branden Visser: Core maintainer

## License

This software is licensed under the Apache 2 license, quoted below.

Copyright 2014 [Pellucid Analytics](http://www.pellucid.com/)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
