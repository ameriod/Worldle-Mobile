package com.nordeck.app.worldle.common.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.String
import kotlin.Unit
import kotlin.collections.List

public interface HistoryQueries : Transacter {
  public fun <T : Any> selectByDate(date: String, mapper: (
    date: String,
    country: String,
    guesses: List<String>
  ) -> T): Query<T>

  public fun selectByDate(date: String): Query<History>

  public fun insertOrUpdate(
    date: String,
    country: String,
    guesses: List<String>
  ): Unit
}
