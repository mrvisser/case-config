package com.pellucid.caseconfig

package object macrocompat {
  type Context = scala.reflect.macros.Context

  def decodedName(c: Context)(symbol: c.universe.TermSymbol): String =
    symbol.name.decoded.toString

  def declarations(c: Context)(tpe: c.universe.Type): Iterable[c.universe.Symbol] =
    tpe.declarations
}