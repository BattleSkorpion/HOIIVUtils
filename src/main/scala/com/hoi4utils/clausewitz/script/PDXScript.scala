package com.hoi4utils.clausewitz.script

import com.hoi4utils.clausewitz_parser.Node

import scala.collection.mutable.ListBuffer

trait PDXScript[T] {
  def set(obj: T): Unit

  /**
   * Set the node value to the given value. Obviously, if T != to the type of the value,
   * the new value may not be semantically correct. However, we need to allow this for
   * flexibility ie. setting a PDX of type double with an int value, and this also matches
   * the underlying node class functionality.
   */
  def setNode(value: T | String | Int | Double | Boolean | ListBuffer[Node] | Null): Unit

  @throws[UnexpectedIdentifierException]
  @throws[NodeValueTypeException]
  def set(expression: Node): Unit

  def get(): Option[T]
  
  def getNode : Option[Node]

  @throws[UnexpectedIdentifierException]
  def loadPDX(expression: Node): Unit

  def loadPDX(expressions: Iterable[Node]): Unit

  //void loadPDX(@NotNull File file);//void loadPDX(@NotNull File file);

  def isValidIdentifier(node: Node): Boolean

  def clearNode(): Unit

  def setNull(): Unit
    
  def loadOrElse(exp: Node, value: T): Unit

  def toScript: String

  def equals(other: PDXScript[?]): Boolean

  def getOrElse(elseValue: T): T

  def isUndefined: Boolean

  def isDefined: Boolean

  def getPDXIdentifier: String
}
