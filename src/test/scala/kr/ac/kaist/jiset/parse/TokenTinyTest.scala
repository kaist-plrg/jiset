package kr.ac.kaist.jiset.parse

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.algorithm.token._
import kr.ac.kaist.jiset.extractor.algorithm._
import scala.util.Random
import org.scalatest._

class TokenTinyTest extends ParseTest {
  val name: String = "parseTokenTest"

  // random repetition
  def randomRepeat[T](f: => T): List[T] =
    (0 until Random.nextInt(5)).map(_ => f).toList

  // random string
  def randomString: String = (Random.alphanumeric take 10).mkString

  // random integer
  val randomInt: Int => Int = Random.nextInt(_)

  // random simple token
  def randomSimpleToken: Token = randomInt(10) match {
    case 0 => Const(randomString)
    case 1 => Code(randomString)
    case 2 => Value(randomString)
    case 3 => Id(randomString)
    case 4 => Text(randomString)
    case 5 => Nt(randomString)
    case 6 => Link(randomString)
    case 7 => Next(randomInt(10))
    case 8 => In
    case 9 => Out
  }

  // random token
  def randomToken: Token = randomInt(13) match {
    case 0 => Sup(randomTokenList)
    case 1 => Gr(randomString, randomRepeat(randomString))
    case 2 => Sub(randomTokenList)
    case _ => randomSimpleToken
  }

  // random token list
  def randomTokenList: List[Token] = randomRepeat(randomSimpleToken)

  // iteration of random tests
  val ITER = 500

  // registration
  def init: Unit = check("random-check", for (_ <- 0 until 500) {
    val tokens = randomTokenList
    val string = tokens.mkString(" ")
    val newTokens = TokenParser.listFrom(string)
    val newString = newTokens.mkString(" ")
    assert(tokens == newTokens)
    assert(string == newString)
  })
  init
}
