package com.hoi4utils.clausewitz.code.effect

import com.hoi4utils.clausewitz.code.scope.ScopeType
import com.hoi4utils.clausewitz.script.*
import com.hoi4utils.clausewitz_parser.Node
import org.jetbrains.annotations.NotNull

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util
import scala.collection.mutable.ListBuffer
import scala.util.{Try, Using}

object EffectDatabase {
  private def newStructuredEffectBlock(pdxIdentifier: String, childScripts: ListBuffer[? <: PDXScript[?]]) = new StructuredPDX(pdxIdentifier) {
    override protected def childScripts: Iterable[? <: PDXScript[?]] = return childScripts

    def nodeEquals(other: PDXScript[?]): Boolean = {
      // todo
      false
    }
  }

  // todo idk
  try {
    Class.forName("org.sqlite.JDBC")
  }
  catch {
    case e: ClassNotFoundException =>
      throw new RuntimeException(e)
  }

}

class EffectDatabase(databaseName: String) {
  private var connection: Connection = _

  try {
    val url = getClass.getClassLoader.getResource(databaseName)
    if (url == null) throw new SQLException("Unable to find '" + databaseName + "'")
    val tempFile = File.createTempFile("effects", ".db")
    tempFile.deleteOnExit()
//    try {
//      val inputStream = url.openStream
//      try Files.copy(inputStream, tempFile.toPath, StandardCopyOption.REPLACE_EXISTING)
//      finally if (inputStream != null) inputStream.close()
//    }
    Using(url.openStream) { inputStream =>
      Files.copy(inputStream, tempFile.toPath, StandardCopyOption.REPLACE_EXISTING)
    }.recover {
      case e: Exception =>
        println(s"An error occurred: ${e.getMessage}")
    }
    connection = DriverManager.getConnection("jdbc:sqlite:" + tempFile.getAbsolutePath)
    loadEffects
  } catch {
    case e@(_: IOException | _: SQLException) =>
      e.printStackTrace()
  }

  // public static void main(String[] args) {
  // EffectDatabase effectDB = new EffectDatabase("effects.db");
  //
  // // Retrieve and use modifiers
  //// effectDatabase.loadEffects();
  // effectDB.createTable();
  //
  // // Close the database connection
  // effectDB.close();
  // }
  def this() = {
    this("databases/effects.db")
  }

  private def createTable(): Unit = {
    val createTableSQL = "CREATE TABLE IF NOT EXISTS effects ("
      + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "identifier TEXT,"
      + "supported_scopes TEXT," // Add this column for supported scopes
      + "supported_targets TEXT" // Add this column for supported targets
      + ")"

    try {
      val createTable = connection.prepareStatement(createTableSQL)
      createTable.executeUpdate
    } catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }

  def loadEffects: List[Effect] = {
    val loadedEffects = new ListBuffer[Effect]
    val retrieveSQL = "SELECT * FROM effects"
    try {
      val retrieveStatement = connection.prepareStatement(retrieveSQL)
      val resultSet = retrieveStatement.executeQuery
      while (resultSet.next) {
        val pdxIdentifier = resultSet.getString("identifier")
        // System.out.println("id " + identifier);
        val supportedScopes_str = resultSet.getString("supported_scopes")
        val supportedTargets_str = resultSet.getString("supported_targets")
        val requiredParametersFull_str = resultSet.getString("required_parameters_full")
        val requiredParametersSimple_str = resultSet.getString("required_parameters_simple")
        val optionalParameters_str = resultSet.getString("optional_parameters")

        var supportedScopes = parseEnumSet(supportedScopes_str)
        var supportedTargets = parseEnumSet(supportedTargets_str)
        // im just guessing at this code right now, I just want it to work basically.
        if (supportedScopes.isEmpty) {
          throw new InvalidParameterException("Invalid scope definition: " + supportedScopes_str)
        }

        /* required parameters */
        val requiredParametersFull = Option(requiredParametersFull_str)
        val requiredParameterSimple = Option(requiredParametersSimple_str)

        var effects = new ListBuffer[Effect]

        (requiredParametersFull, requiredParameterSimple) match {
          case (Some(requiredParametersFull), Some(requiredParameterSimple)) =>
            // todo
//            effects.addOne(new SimpleEffect(pdxIdentifier, () => new ReferencePDX[Effect](loadedEffects, _.identifier, pdxIdentifier)) with BlockEffect(pdxIdentifier, new StructuredPDX(pdxIdentifier)) {
//              // Implement abstract methods from SimpleEffect and BlockEffect here
//              override def someAbstractMethodFromSimpleEffect(): ReturnType = {
//                // Implementation
//              }
//
//              override def someAbstractMethodFromBlockEffect(): ReturnType = {
//                // Implementation
//              }
//            })
          case (Some(requiredParametersFull), None) =>
            val alternateParameters = requiredParametersFull_str.split("\\s+\\|\\s+")
            for (alternateParameter <- alternateParameters) {
              val parametersStrlist = alternateParameter.split("\\s+,\\s+")
              val childScripts = new ListBuffer[PDXScript[?]]
              for (i <- parametersStrlist.indices) {
                val parameterStr = parametersStrlist(i)
                var data = parameterStr.splitWithDelimiters("(<[a-z_-]+>|\\|)", -1)
                data = data.filter((s: String) => s.nonEmpty)
                if (data.length >= 2) {
                  val paramIdentifierStr = data(0).trim
                  val paramTypeStr = data(1).trim
                  val paramValueType = ParameterValueType.of(paramTypeStr)
                }
                else throw new InvalidParameterException("Invalid parameter definition: " + parameterStr)
                if (data.length >= 3) {
                  // idk
                }
              }
            }
            effects.addOne(new BlockEffect(pdxIdentifier,
              new StructuredPDX(pdxIdentifier) {
                override protected def childScripts: Iterable[? <: PDXScript[?]] = List.empty
              })
            {
            })
          case (None, Some(requiredParameterSimple)) =>
            val alternateParameters = requiredParametersSimple_str.split("\\s+\\|\\s+")
            for (alternateParameter <- alternateParameters) {
              val parametersStrlist = alternateParameter.split("\\s+,\\s+")
              for (i <- parametersStrlist.indices) {
                val parameterStr = parametersStrlist(i)
                var data = parameterStr.splitWithDelimiters("(<[a-z_-]+>|\\|)", -1)
                data = data.filter((s: String) => s.nonEmpty)
                if (data.length >= 2) {
                  val paramIdentifierStr = data(0).trim
                  val paramTypeStr = data(1).trim
                  val paramValueType = ParameterValueType.of(paramTypeStr)
                }
                else throw new InvalidParameterException("Invalid parameter definition: " + parameterStr)
                if (data.length >= 3) {
                  // idk
                }
              }
            }
            effects.addOne(new SimpleEffect(pdxIdentifier, () => new ReferencePDX[Effect](loadedEffects, _.identifier, pdxIdentifier)) {
            })
          case (None, None) =>
            // todo (bad)
        }
      }
    } catch {
      case e: SQLException =>
        e.printStackTrace()
    }
    loadedEffects.toList
  }

//  private def parseEnumSet(enumSetString: String): EnumSet[ScopeType] = {
//    if (enumSetString == null || enumSetString.isEmpty || enumSetString == "none") null
//    else {
//      val enumValues = enumSetString.split(", ")
//      enumValues.map(ScopeType.valueFromString).collect(Collectors.toCollection(() => EnumSet.noneOf(classOf[ScopeType])))
//    }
//  }
  private def parseEnumSet(enumSetString: String): Option[Set[ScopeType]] = {
    Option(enumSetString).filter(_.nonEmpty).filter(_ != "none").map { str =>
      str.split(", ").flatMap { enumName =>
        Try(ScopeType.valueOf(enumName)).toOption
      }.toSet
    }
  }

  def close(): Unit = {
    try connection.close()
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }
}
