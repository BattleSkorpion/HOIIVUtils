package com.hoi4utils.clausewitz.data.country

import com.hoi4utils.clausewitz.script.AbstractPDX
import com.hoi4utils.clausewitz.script.PDXScript
import org.jetbrains.annotations.NotNull

import scala.collection.mutable.ListBuffer

object CountryTag extends Iterable[CountryTag] {
  val NULL_TAG = new CountryTag("###")
  val COUNTRY_TAG_LENGTH = 3 // standard country tag length (for a normal country tag)private final String tag;
  // scala... (this is null (????????????) if you dont use 'lazy')
  private lazy val _tagList: ListBuffer[CountryTag] = {
//    println("Initializing _tagList")
    ListBuffer[CountryTag]()
  }

  def get(tag: String): CountryTag = {
    for (countryTag <- _tagList) {
      if (countryTag.get().equals(tag)) return countryTag
    }
    NULL_TAG
  }

  override def iterator: Iterator[CountryTag] = {
    _tagList.iterator
  }
  
//  def tagList(): List[CountryTag] = _tagList
  def addTag(tag: CountryTag): Unit = {
    _tagList.addOne(tag)
  }
}

class CountryTag(tag: String) extends AbstractPDX[String](List("tag")) with Comparable[CountryTag] {
  setNode(tag)
  CountryTag.addTag(this)

  override def compareTo(o: CountryTag): Int = {
    (this.get(), o.get()) match {
      case (Some(thisTag), Some(otherTag)) => thisTag.compareTo(otherTag)
      case (Some(thisTag), None) => 1
      case (None, Some(otherTag)) => -1
    }
  }

  override def set(obj: String): Unit = {
    setNode(obj)
  }
}
