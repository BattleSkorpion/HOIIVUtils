package com.hoi4utils.clausewitz_parser

import com.hoi4utils.clausewitz.BoolType
import org.jetbrains.annotations.NotNull

import java.util
import java.util.{ArrayList, List}
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Stream
import scala.collection.mutable.ListBuffer

//object Node {
//  private val boolType: BoolType = null
//}

class Node(protected[clausewitz_parser] var _identifier: String, protected[clausewitz_parser] var _operator: String,
           protected[clausewitz_parser] var nodeValue: NodeValue, protected[clausewitz_parser] var nameToken: Token,
           protected[clausewitz_parser] var operatorToken: Token)
  extends NodeIterable[Node] {

  if (nodeValue == null) nodeValue = new NodeValue
  
  def this(value: NodeValue) = {
    this(null, null, value, null, null)
  }

  def this() = {
    this(null.asInstanceOf[NodeValue])
  }

  def this(value: ListBuffer[Node]) = {
    this(new NodeValue(value))
  }

  def name: String = identifier

  def getValue: String | Int | Double | Boolean | ListBuffer[Node] | Null = nodeValue.getValue

  //def getValue(id:String): NodeValue = find(id).nodeValue
  def getValue(id: String): NodeValue = {
    val value = find(id)
    value match {
      case Some(node) => node.nodeValue
      case None => null
    }
  }

  def setValue(value: String | Int | Double | Boolean | ListBuffer[Node] | Null): Unit = {
    this.nodeValue.setValue(value)
  }

  def isParent: Boolean = nodeValue.isList

  def valueIsNull: Boolean = this.$ == null
  
  def isEmpty: Boolean = {
    valueIsNull && identifier == null && operator == null
  }

  override def toString: String = identifier + operator + nodeValue.asString // todo

  def nameAsInteger: Int = identifier.toInt

  def nameEquals(s: String): Boolean = {
    if (identifier == null) return false
    identifier == s
  }
  
  def setNull(): Unit = nodeValue = new NodeValue
  
  def valueIsInstanceOf(clazz: Class[?]): Boolean = nodeValue.valueIsInstanceOf(clazz)
  
  def $ : String | Int | Double | Boolean | ListBuffer[Node] | Null = getValue

  def identifier: String = _identifier

  def identifier_= (identifier: String): Unit = {
    _identifier = identifier
  }

  def operator: String = _operator

  def operator_= (operator: String): Unit = {
    _operator = operator
  }
}