package com.pellucid.caseconfig.extractors

import com.pellucid.caseconfig.macrocompat

object ExtractorMacros {

  /**
   * An implicit macro that extracts any generic case class `T` from a config
   * object. If `T` is not a case class, compilation is aborted.
   */
  def caseClassExtractorImpl[T: c.WeakTypeTag](c: macrocompat.Context): c.Expr[CTypeExtractor[T]] = {
    import c.universe._
    val ccTpe = weakTypeOf[T].typeSymbol

    // Generic type that is not a simple type can only be a case class
    if (!ccTpe.isClass || !ccTpe.asClass.isCaseClass)
      c.abort(c.enclosingPosition, s"$ccTpe is not a simple type or a case class")

    // Extract the constructor arguments from the case class
    val ccArgs = macrocompat.declarations(c)(ccTpe.typeSignature)
      .toList
      .collect {
      case term: TermSymbol if term.isVal && term.isCaseAccessor => term
    }
      .map { termSymbol =>
      val path = q"${macrocompat.decodedName(c)(termSymbol)}"

      // For each case accessor, we want to recursively extract their
      // inner type using an implicit extractor. Note that the bizarre use
      // of the `def extractor` is a work-around so that the scala
      // compiler is able to perform implicit recursion without running
      // into divergent implicit issues as a result of some arbitrary
      // heuristics
      q"""
          def extractor(implicit ev: _root_.com.pellucid.caseconfig.extractors.CTypeExtractor[${termSymbol.typeSignature}]) = ev
          extractor.apply(targetConfig, Some($path))
        """
    }

    // Create our implicit extractor for the case class type
    val tree = q"""
      new _root_.com.pellucid.caseconfig.extractors.CTypeExtractor[$ccTpe] {
        def apply(config: _root_.com.typesafe.config.Config, pathOpt: Option[String]): $ccTpe = {
          val targetConfig = pathOpt match {
            case None => config
            case Some(path) => config.getConfig(path)
          }

          new $ccTpe(..$ccArgs)
        }
      }
    """

    c.Expr[CTypeExtractor[T]](tree)
  }
}
