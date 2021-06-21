package kr.ac.kaist.jiset.js

package object builtin {
  // constants
  val SUB_PROP = "SubMap"
  val DESC_PREFIX = "DESC:"

  // get submap name
  def subName(name: String) = s"$name.$SUB_PROP"

  // get descriptor name
  def descName(name: String, key: Key) =
    DESC_PREFIX + name + key.toPropString

  // get global name
  def globalName(name: String) = {
    if (name == GLOBAL) GLOBAL
    else s"$GLOBAL.$name"
  }
}
