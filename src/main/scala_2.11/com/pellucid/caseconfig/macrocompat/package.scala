package com.pellucid.caseconfig

import scala.reflect.macros.blackbox

package object macrocompat {
  type Context = blackbox.Context

  def decodedName(c: Context)(symbol: c.universe.TermSymbol): String =
    symbol.name.decodedName.toString

  def declarations(c: Context)(tpe: c.universe.Type): c.universe.MemberScope =
    tpe.decls
}
