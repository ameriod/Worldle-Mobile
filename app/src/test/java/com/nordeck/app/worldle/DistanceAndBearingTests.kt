package com.nordeck.app.worldle

import com.nordeck.app.worldle.model.AssetsFileLoader
import com.nordeck.app.worldle.model.Country
import com.nordeck.app.worldle.model.Direction
import com.nordeck.app.worldle.model.Repository
import com.nordeck.app.worldle.model.getDirectionTo
import com.nordeck.app.worldle.model.getDistanceTo
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import timber.log.Timber

@RunWith(RobolectricTestRunner::class)
class DistanceAndBearingTests {

    private lateinit var countries: List<Country>

    private fun List<Country>.get(name: String): Country =
        this.first { it.name.contains(name, true) }

    private val Int.miles get() = ConversionMath.metersToMiles(this)

    @Before
    fun setup() {
        countries = Repository.getCountriesInternal(
            AssetsFileLoader(context = RuntimeEnvironment.getApplication())
        )
        Timber.plant(object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                println("$priority - $tag - $message - $t")
            }
        })
    }

    @After
    fun tearDown() {
        Timber.uprootAll()
    }

    @Test
    fun uganda() {
        val uganda = countries.get("uganda")

        // S
        val greece = countries.get("greece")
        assertEquals(2687, greece.getDistanceTo(uganda).miles)
        assertEquals(Direction.SSE, greece.getDirectionTo(uganda))

        // W
        val somalia = countries.get("somalia")
        assertEquals(994, somalia.getDistanceTo(uganda).miles)
        assertEquals(Direction.WSW, somalia.getDirectionTo(uganda))

        // NE
        val rwanda = countries.get("rwanda")
        assertEquals(283, rwanda.getDistanceTo(uganda).miles)
        assertEquals(Direction.NE, rwanda.getDirectionTo(uganda))
    }

    @Test
    fun bhutan() {
        val bhutan = countries.get("bhutan")

        // N
        val bangladesh = countries.get("bangladesh")
        assertEquals(264, bangladesh.getDistanceTo(bhutan).miles)
        assertEquals(Direction.N, bangladesh.getDirectionTo(bhutan))

        // NE
        val angola = countries.get("angola")
        assertEquals(5538, angola.getDistanceTo(bhutan).miles)
        assertEquals(Direction.ENE, angola.getDirectionTo(bhutan))

        // E
        val andorra = countries.get("andorra")
        assertEquals(4905, andorra.getDistanceTo(bhutan).miles)
        assertEquals(Direction.ESE, andorra.getDirectionTo(bhutan))

        // E
        val aruba = countries.get("aruba")
        assertEquals(9374, aruba.getDistanceTo(bhutan).miles)
        assertEquals(Direction.E, aruba.getDirectionTo(bhutan))
    }

    @Test
    fun congo() {
        val congo = countries.get("congo")

        // S
        val albania = countries.get("albania")
        assertEquals(2872, albania.getDistanceTo(congo).miles)
        assertEquals(Direction.S, albania.getDirectionTo(congo))

        // N
        val southAfrica = countries.get("south africa")
        assertEquals(2147, southAfrica.getDistanceTo(congo).miles)
        assertEquals(Direction.NNW, southAfrica.getDirectionTo(congo))

        // E
        val anguilla = countries.get("anguilla")
        assertEquals(5495, anguilla.getDistanceTo(congo).miles)
        assertEquals(Direction.ESE, anguilla.getDirectionTo(congo))

        // W
        val philippines = countries.get("philippines")
        assertEquals(7295, philippines.getDistanceTo(congo).miles)
        assertEquals(Direction.W, philippines.getDirectionTo(congo))

        // NW
        val zambia = countries.get("zambia")
        assertEquals(1213, zambia.getDistanceTo(congo).miles)
        assertEquals(Direction.NW, zambia.getDirectionTo(congo))

        // NW
        val drc = countries.get("democratic republic of the congo")
        assertEquals(486, drc.getDistanceTo(congo).miles)
        assertEquals(Direction.WNW, drc.getDirectionTo(congo))
    }

    @Test
    fun new_zealand() {
        val newZealand = countries.get("new zealand")

        // SE
        val australia = countries.get("australia")
        assertEquals(2583, australia.getDistanceTo(newZealand).miles)
        assertEquals(Direction.ESE, australia.getDirectionTo(newZealand))

        // S
        val newCaledonia = countries.get("caledonia")
        assertEquals(1484, newCaledonia.getDistanceTo(newZealand).miles)
        assertEquals(Direction.SSE, newCaledonia.getDirectionTo(newZealand))

        // SE
        val papua = countries.get("papua")
        assertEquals(3056, papua.getDistanceTo(newZealand).miles)
        assertEquals(Direction.SE, papua.getDirectionTo(newZealand))

        // W
        val chile = countries.get("chile")
        assertEquals(5677, chile.getDistanceTo(newZealand).miles)
        assertEquals(Direction.W, chile.getDirectionTo(newZealand))

        // SE
        val germany = countries.get("germany")
        assertEquals(11410, germany.getDistanceTo(newZealand).miles)
        assertEquals(Direction.ESE, germany.getDirectionTo(newZealand))

        // E
        val southAfrica = countries.get("south africa")
        assertEquals(7184, southAfrica.getDistanceTo(newZealand).miles)
        assertEquals(Direction.E, southAfrica.getDirectionTo(newZealand))
    }
}
