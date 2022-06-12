package com.nordeck.app.worldle

import com.nordeck.app.worldle.model.AssetsFileLoader
import com.nordeck.app.worldle.model.Country
import com.nordeck.app.worldle.model.Repository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DistanceAndBearingTests {

    lateinit var countries: List<Country>

    fun List<Country>.get(name: String): Country = this.first { it.name.contains(name, true) }

    @Before
    fun setup() {
        countries =
            Repository.getCountriesInternal(AssetsFileLoader(RuntimeEnvironment.getApplication()))
    }

    @Test
    fun uganda() {
        val uganda = countries.get("uganda")

        val greece = countries.get("greece")
        assertEquals(2687, ConversionMath.metersToMiles(greece.getDistanceTo(uganda)))

        val somalia = countries.get("somalia")
        assertEquals(994, ConversionMath.metersToMiles(somalia.getDistanceTo(uganda)))

        val rwanda = countries.get("rwanda")
        assertEquals(283, ConversionMath.metersToMiles(rwanda.getDistanceTo(uganda)))
    }
}