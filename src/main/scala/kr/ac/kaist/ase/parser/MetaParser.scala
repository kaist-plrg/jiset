package kr.ac.kaist.ase.parser
import scala.io.Source

object MetaParser {
  def apply(filename: String) = {
    val source = scala.io.Source.fromFile(filename)
    val lines = try source.getLines.toList finally source.close()
    val metadata = lines.dropWhile((x) => !(x contains "/*---")).tail.takeWhile((x) => !(x contains "---*/"))
    val (negative, flags, includes, _) = metadata.foldLeft[(Option[String], List[String], List[String], Boolean)]((None, List(), List(), false)) {
      case ((negative, flags, includes, isNeg), line) => {
        val isNegn = if (line contains "negative:") true else isNeg
        val negativen = if ((line contains "type:") && isNeg) Some(line.split(' ').last) else negative
        val flagsn = if (line contains "flags:") line.dropWhile(_ != '[').tail.takeWhile(_ != ']').split(", ").toList else flags
        val includesn = if (line contains "includes:") line.dropWhile(_ != '[').tail.takeWhile(_ != ']').split(", ").toList else includes
        (negativen, flagsn, includesn, isNegn)
      }
    }
    MetaData(negative, flags, includes)
  }
}

case class MetaData(negative: Option[String], flags: List[String], includes: List[String])

