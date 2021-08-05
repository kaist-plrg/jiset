package kr.ac.kaist.jiset.util

import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.{ Map => MMap }

trait BasicJsonProtocol {
  // JSON format based on parsers and stringifier
  def stringCodec[T](
    parser: String => T,
    stringifier: T => String
  ): (Decoder[T], Encoder[T]) = {
    val decoder = new Decoder[T] {
      final def apply(c: HCursor): Decoder.Result[T] = c.value.asString match {
        case None => decodeFail(s"Expected a string instead of ${c.value}", c)
        case Some(str) => Right(parser(str))
      }
    }
    val encoder = new Encoder[T] {
      final def apply(x: T): Json = Json.fromString(stringifier(x))
    }
    (decoder, encoder)
  }

  // JSON protocol for parametric map structures
  implicit def MapDecoder[K, V](
    implicit
    KDecoder: Decoder[K],
    VDecoder: Decoder[V]
  ): Decoder[Map[K, V]] = new Decoder[Map[K, V]] {
    final def apply(c: HCursor): Decoder.Result[Map[K, V]] =
      pairsDecoder[K, V](c).map(_.toMap)
  }
  implicit def MapEncoder[K, V](
    implicit
    KEncoder: Encoder[K],
    VEncoder: Encoder[V]
  ): Encoder[Map[K, V]] = new Encoder[Map[K, V]] {
    final def apply(map: Map[K, V]): Json =
      pairsEncoder[K, V](map.toVector)
  }

  // JSON protocol for parametric mutable map structures
  implicit def MMapDecoder[K, V](
    implicit
    KDecoder: Decoder[K],
    VDecoder: Decoder[V]
  ): Decoder[MMap[K, V]] = new Decoder[MMap[K, V]] {
    final def apply(c: HCursor): Decoder.Result[MMap[K, V]] =
      pairsDecoder[K, V](c).map(MMap.from)
  }
  implicit def MMapEncoder[K, V](
    implicit
    KEncoder: Encoder[K],
    VEncoder: Encoder[V]
  ): Encoder[MMap[K, V]] = new Encoder[MMap[K, V]] {
    final def apply(map: MMap[K, V]): Json =
      pairsEncoder[K, V](map.toVector)
  }

  // internal helper for pairs
  private def pairsDecoder[K, V](c: HCursor)(
    implicit
    KDecoder: Decoder[K],
    VDecoder: Decoder[V]
  ): Decoder.Result[Vector[(K, V)]] = {
    val pairs: Vector[Json] = c.value.asArray.get
    optional(Right((for {
      pairJson <- pairs
      pair <- pairJson.asArray
      (kJson, vJson) <- pair match {
        case Vector(kJson, vJson) => Some((kJson, vJson))
        case _ => None
      }
      k <- KDecoder(kJson.hcursor).toOption
      v <- VDecoder(vJson.hcursor).toOption
    } yield k -> v))).getOrElse {
      decodeFail(s"invalid format for map structures: ${c.value}", c)
    }
  }
  private def pairsEncoder[K, V](pairs: Vector[(K, V)])(
    implicit
    KEncoder: Encoder[K],
    VEncoder: Encoder[V]
  ): Json = pairs.map {
    case (k, v) => Vector(k.asJson, v.asJson).asJson
  }.asJson

  // JSON protocol for objects defined with unique ids
  def UIdDecoder[T <: UId[T], U <: T](
    uidGen: UIdGen[T]
  ): Decoder[U] = new Decoder[U] {
    final def apply(c: HCursor): Decoder.Result[U] = optional {
      val uid: Int = c.value.asNumber.get.toInt.get
      Right(uidGen.get(uid).asInstanceOf[U])
    }.getOrElse {
      decodeFail(s"invalid format: ${c.value}", c)
    }
  }
  def UIdEncoder[T <: UId[_]]: Encoder[T] = new Encoder[T] {
    final def apply(data: T): Json = data.uid.asJson
  }

  // for double values
  implicit lazy val DoubleDecoder: Decoder[Double] = new Decoder[Double] {
    final def apply(c: HCursor): Decoder.Result[Double] = {
      optional(Right(c.value.asString match {
        case Some(name) => name match {
          case "Infinity" => Double.PositiveInfinity
          case "-Infinity" => Double.NegativeInfinity
          case "NaN" => Double.NaN
        }
        case None => c.value.asNumber.get.toDouble
      })).getOrElse(decodeFail(s"invalid double: ${c.value}", c))
    }
  }
  implicit lazy val DoubleEncoder: Encoder[Double] = Encoder.instance {
    case t => Json.fromDoubleOrString(t)
  }

  // decoding failure
  def decodeFail[T](msg: String, c: HCursor): Decoder.Result[T] =
    Left(DecodingFailure(msg, c.history))
}
