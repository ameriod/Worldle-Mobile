package com.nordeck.app.worldle.common.db

import com.squareup.sqldelight.ColumnAdapter
import kotlin.String
import kotlin.collections.List

public data class History(
  public val date: String,
  public val country: String,
  public val guesses: List<String>
) {
  public override fun toString(): String = """
  |History [
  |  date: $date
  |  country: $country
  |  guesses: $guesses
  |]
  """.trimMargin()

  public class Adapter(
    public val guessesAdapter: ColumnAdapter<List<String>, String>
  )
}
