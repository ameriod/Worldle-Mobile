package com.nordeck.app.worldle.common.common

import com.nordeck.app.worldle.common.HistoryDatabase
import com.nordeck.app.worldle.common.db.History
import com.nordeck.app.worldle.common.db.HistoryQueries
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.`internal`.copyOnWriteList
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.reflect.KClass

internal val KClass<HistoryDatabase>.schema: SqlDriver.Schema
  get() = HistoryDatabaseImpl.Schema

internal fun KClass<HistoryDatabase>.newInstance(driver: SqlDriver,
    HistoryAdapter: History.Adapter): HistoryDatabase = HistoryDatabaseImpl(driver, HistoryAdapter)

private class HistoryDatabaseImpl(
  driver: SqlDriver,
  internal val HistoryAdapter: History.Adapter
) : TransacterImpl(driver), HistoryDatabase {
  public override val historyQueries: HistoryQueriesImpl = HistoryQueriesImpl(this, driver)

  public object Schema : SqlDriver.Schema {
    public override val version: Int
      get() = 1

    public override fun create(driver: SqlDriver): Unit {
      driver.execute(null, """
          |CREATE TABLE History (
          |  date TEXT NOT NULL PRIMARY KEY,
          |  country TEXT NOT NULL,
          |  guesses TEXT NOT NULL
          |)
          """.trimMargin(), 0)
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ): Unit {
    }
  }
}

private class HistoryQueriesImpl(
  private val database: HistoryDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), HistoryQueries {
  internal val selectByDate: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectByDate(date: String, mapper: (
    date: String,
    country: String,
    guesses: List<String>
  ) -> T): Query<T> = SelectByDateQuery(date) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      database.HistoryAdapter.guessesAdapter.decode(cursor.getString(2)!!)
    )
  }

  public override fun selectByDate(date: String): Query<History> = selectByDate(date) { date_,
      country, guesses ->
    History(
      date_,
      country,
      guesses
    )
  }

  public override fun insertOrUpdate(
    date: String,
    country: String,
    guesses: List<String>
  ): Unit {
    driver.execute(1428353181,
        """INSERT OR REPLACE INTO History(date, country, guesses)VALUES(?,?,?)""", 3) {
      bindString(1, date)
      bindString(2, country)
      bindString(3, database.HistoryAdapter.guessesAdapter.encode(guesses))
    }
    notifyQueries(1428353181, {database.historyQueries.selectByDate})
  }

  private inner class SelectByDateQuery<out T : Any>(
    public val date: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectByDate, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-110385927,
        """SELECT * FROM History WHERE date =?""", 1) {
      bindString(1, date)
    }

    public override fun toString(): String = "History.sq:selectByDate"
  }
}
