package com.nordeck.app.worldle.common

import com.nordeck.app.worldle.common.common.newInstance
import com.nordeck.app.worldle.common.common.schema
import com.nordeck.app.worldle.common.db.History
import com.nordeck.app.worldle.common.db.HistoryQueries
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver

public interface HistoryDatabase : Transacter {
  public val historyQueries: HistoryQueries

  public companion object {
    public val Schema: SqlDriver.Schema
      get() = HistoryDatabase::class.schema

    public operator fun invoke(driver: SqlDriver, HistoryAdapter: History.Adapter): HistoryDatabase
        = HistoryDatabase::class.newInstance(driver, HistoryAdapter)
  }
}
