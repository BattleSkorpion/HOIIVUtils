package com.hoi4utils.clausewitz.script

import com.hoi4utils.clausewitz_parser.Node
import com.hoi4utils.clausewitz_parser.NodeValue
import scala.collection.mutable.ListBuffer

abstract class StructuredPDX(pdxIdentifiers: List[String]) extends AbstractPDX[ListBuffer[Node]](pdxIdentifiers) {
  def this(pdxIdentifiers: String*) = {
    this(pdxIdentifiers.toList)
  }

  protected def childScripts: collection.mutable.Iterable[? <: PDXScript[?]]

  @throws[UnexpectedIdentifierException]
  @throws[NodeValueTypeException]
  override def set(expression: Node): Unit = {
    usingIdentifier(expression)
    this.node = Some(expression)
    expression.$ match {
      case l: ListBuffer[Node] =>
        // then load each sub-PDXScript
        for (pdxScript <- childScripts) {
          pdxScript.loadPDX(l)
        }
      case _ =>
        throw new NodeValueTypeException(expression, "list")  // todo check through schema
    }
  }

  override def set(value: ListBuffer[Node]): ListBuffer[Node] = {
    // todo?
    super.setNode(value)
    value
  }

  override def loadPDX(expression: Node): Unit = {
    if (expression.name == null) {
      // todo check through schema?
      expression.$ match {
        case l: ListBuffer[Node] =>
          loadPDX(l)
        case _ =>
          System.out.println("Error loading PDX script: " + expression)
      }
    }
    try set(expression)
    catch {
      case e@(_: UnexpectedIdentifierException | _: NodeValueTypeException) =>
        // System.out.println("Error loading PDX script:" + e.getMessage + "\n\t" + expression) // todo expression prints entire focus node
        System.out.println("Error loading PDX script:" + e.getMessage)
    }
  }

  /**
   * Gets the child pdx property with the current identifier matching
   * the given string.
   *
   * @param identifier
   */
  def getPDXProperty(identifier: String): PDXScript[?] = {
    import scala.collection.BuildFrom.buildFromString
    for (pdx <- childScripts) {
      if (pdx.getPDXIdentifier == identifier) return pdx
    }
    null
  }

  /**
   * Gets the child pdx property with the current identifier matching
   * the given string.
   *
   * @param identifiers
   */
  def getPDXProperty(identifiers: List[String]): Option[PDXScript[?]] = {
    for (identifier <- identifiers) {
      val pdx = getPDXProperty(identifier)
      if (pdx != null) return Some(pdx)
    }
    None
  }

  /**
   * Gets the child pdx property with the current identifier matching
   * the given string.
   *
   * @param identifier
   */
  def getPDXPropertyOfType[R](identifier: String): Option[PDXScript[R]] = {
    for (pdx <- childScripts) {
      pdx match {
        case pdxScript: PDXScript[R] =>
          if (pdxScript.getPDXIdentifier == identifier) return Some(pdxScript)
        case _ =>
      }
    }
    None
  }

  /**
   * Gets the child pdx property with the current identifier matching
   * the given string.
   *
   * @param identifiers
   */
  def getPDXPropertyOfType[R](identifiers: List[String]): Option[PDXScript[R]] = {
    for (identifier <- identifiers) {
      val pdx = getPDXPropertyOfType[R](identifier)
      if (pdx.isDefined) return pdx
    }
    None
  }

  def pdxProperties: Iterable[? <: PDXScript[?]] = {
    val scripts = childScripts
    scripts match {
      case null => null
      case _ => scripts
    }
  }

  override def toScript: String = {
    if (node.isEmpty || node.get.isEmpty) return null

//    val sb = new StringBuilder()
//    sb.append(node.get.identifier)
//    sb.append(" = {\n")
//    for (pdx <- childScripts) {
//      sb.append('\t')
//      sb.append(pdx.toScript)
//    }
//    sb.toString
    // favorable. more in-order as wanted/as was originally.
    node.get.toScript
  }
}
