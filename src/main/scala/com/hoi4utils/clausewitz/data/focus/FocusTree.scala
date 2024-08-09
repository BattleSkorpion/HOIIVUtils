package com.hoi4utils.clausewitz.data.focus

import com.hoi4utils.clausewitz.HOIIVFile
import com.hoi4utils.clausewitz.data.country.{CountryTag, CountryTagsManager}
import com.hoi4utils.clausewitz.script.*
import com.hoi4utils.clausewitz.localization.*
import javafx.collections.{FXCollections, ObservableList}
import org.jetbrains.annotations.*

import java.io.File
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable
import scala.jdk.javaapi.CollectionConverters

/**
 * ALL of the FocusTree/FocusTrees
 * Localizable data: focus tree name. Each focus is its own localizable data.
 */
// todo extends file?
object FocusTree {
  private val focusTreeFileMap = new mutable.HashMap[File, FocusTree]()
  private val focusTrees = new mutable.ListBuffer[FocusTree]()

  def get(focus_file: File): Option[FocusTree] = {
    if (!focusTreeFileMap.contains(focus_file)) new FocusTree(focus_file)
    focusTreeFileMap.get(focus_file)
  }

  def observeFocusTrees: ObservableList[FocusTree] = {
    FXCollections.observableArrayList(CollectionConverters.asJava(focusTrees))
  }

  def read(): Unit = {
    if (!HOIIVFile.mod_focus_folder.exists || !HOIIVFile.mod_focus_folder.isDirectory) {
      System.err.println("Focus folder does not exist or is not a directory.")
      return
    }
    if (HOIIVFile.mod_focus_folder.listFiles == null || HOIIVFile.mod_focus_folder.listFiles.length == 0) {
      System.err.println("No focuses found in " + HOIIVFile.mod_focus_folder)
      return
    }
    for (f <- HOIIVFile.mod_focus_folder.listFiles) {
      if (f.getName.endsWith(".txt")) new FocusTree(f)
    }
  }

  def add(focusTree: FocusTree): Iterable[FocusTree] = {
    focusTreeFileMap.put(focusTree.focus_file, focusTree)
    focusTrees += focusTree
    focusTrees
  }

  def listFocusTrees: Iterable[FocusTree] = focusTreeFileMap.values

  /**
   * Returns focus tree corresponding to the tag, if it exists
   *
   * @param tag
   * @return The focus tree, or null if could not be found/not yet created.
   */
  def get(tag: CountryTag): FocusTree = {
    //focusTrees.values.stream.filter((focusTree: FocusTree) => focusTree.country.nodeEquals(tag)).findFirst.orElse(null)
    for (tree <- listFocusTrees) {
      //if (tree.country.equals(tag)) return tree
      var countryTag = tree.country.get()
      countryTag match {
        case Some(t) => if (t.tag.equals(tag)) return tree
        case None => 
      }
    }
    null
  }

//  private def updateObservableValues(): Unit = {
//    //focusTreesList.setAll(focusTrees.values)
//  }

//  try focusTrees.addListener((change: MapChangeListener.Change[_ <: File, _ <: FocusTree]) => {
//    updateObservableValues()
//    focusTreesList.sort(Comparator.naturalOrder)
//
//  }.asInstanceOf[MapChangeListener[File, FocusTree]])

}

class FocusTree private(private var focus_file: File)
  extends StructuredPDX("focus_tree") with Localizable with Comparable[FocusTree] with Iterable[Focus] {
  /* pdxscript */
  final var country: ReferencePDX[CountryTag] = new ReferencePDX[CountryTag](() => CountryTag.toList, t => Some(t.get), "country")
  final var focuses: MultiPDX[Focus] = new MultiPDX[Focus](None, Some(() => new Focus(this)), "focus")
  final var id: StringPDX = new StringPDX("id")
  private val focusIDList: ListBuffer[String] = null
  // private boolean defaultFocus; // ! todo Do This
  // private Point continuousFocusPosition; // ! todo DO THIS

  //obj.addAll(childScripts)  // todo
  loadPDX(focus_file)
  FocusTree.add(this)
  //private final ObservableMap<String, Focus> focuses;

  override protected def childScripts: mutable.Iterable[? <: PDXScript[?]] = ListBuffer(id, country, focuses)

//  private def checkPendingFocusReferences(): Unit = {
//  }

  /**
   * Lists last set of focuses
   *
   * @return
   */
  def listFocusIDs: ListBuffer[String] = {
    if (focusIDList == null) return null // bad :(
    focusIDList
  }

  def countryTag: CountryTag = {
    country.get() match {
      case Some(t) => t
      case None => null
    }
  }

  def focusFile: File = focus_file

//  override def equals(other: AnyRef): Boolean = {
//    if (other == null) return false
//    if (other.getClass eq this.getClass) return this.focus_file eq other.asInstanceOf[FocusTree].focus_file
//    false
//  }

  override def toString: String = {
    val v = id.get()
    v match {
      case Some(id) => return id
      case None =>if (country.get() != null) return country.get().get.toString
    }
    super.toString
  }

  def minX: Int = {
    if (focuses.isEmpty) return 0
    focuses.minBy(_.absoluteX).absoluteX
  }

  def listFocuses: List[Focus] = {
    focuses.toList
  }

  override def compareTo(o: FocusTree): Int = {
//    var c = 0
//    this.country.get() match {
//      case Some(countryTag) => o.country.get() match {
//        case Some(otherCountryTag) => c = countryTag.compareTo(otherCountryTag)
//        case None => _
//      }
//      case None => _
//    }
//    var d = 0
//    this.id.get() match {
//      case Some(id) => o.id.get() match {
//        case Some(otherId) => d = id.compareTo(otherId)
//        case None => _
//      }
//      case None => _
//    }
//    if (c == 0) d
//    else c
    (this.country.get(), this.id.get()) match {
      case (Some(countryTag), Some(id)) =>
        (o.country.get(), o.id.get()) match {
          case (Some(otherCountryTag), Some(otherID)) =>
            val c = countryTag.compareTo(otherCountryTag)
            if (c == 0) id.compareTo(otherID) else c
          case _ => 0
        }
      case _ => 0
    }
  }

  override def iterator: Iterator[Focus] = focuses.iterator

  override def getLocalizableProperties: mutable.HashMap[Property, String] = {
    // lets us map null if we use hashmap instead of generic of() method
    val properties = new mutable.HashMap[Property, String]
    id.get() match {
      case Some(id) => properties.put(Property.NAME, id)
      case None => //properties.put(Property.NAME, null)
    }
    properties
  }

  /**
   * Get the localizable group of this focus tree, which is the list of focuses.
   *
   * @return
   */
  override def getLocalizableGroup: Iterable[? <: Localizable] = focuses

  override def equals(other: PDXScript[?]): Boolean = {
    if (other.isInstanceOf[FocusTree]) return this == other
    false
  }
}