object BaseType {
  private val map: Map[String, Map[String, Value]] = Map(
    "ObjectEnvironmentRecord" -> Map(
      "withEnvironment" -> Bool(false)
    )
  )

  def getMap(s: String): Map[Value, Value] = map.getOrElse(s, Map()).map {
    case (s, v) => Str(s) -> v
  }
}
