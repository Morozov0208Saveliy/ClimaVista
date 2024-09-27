package com.example.climavista

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import com.example.climavista.model.CurrentResponseApi
import com.example.climavista.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CityListFragmentTest {


    @get:Rule(order = 0)
    var rule = RuleChain.outerRule(HiltAndroidRule(this))


    @Test
    fun testSearchCityAndDisplayCityList() {
        // Запуск MainActivity
        println(" in one ")
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.use {


            println(" in two")

            onView(withId(R.id.addCity)).check(matches(isDisplayed()))

            onView(withId(R.id.addCity)).perform(click())

            Thread.sleep(2000)

            // Проверка, что поле для поиска отображено
            onView(withId(R.id.cityEdt)).check(matches(isDisplayed()))

            //
            println("in three")
//        // Ввод текста в поле поиска
            onView(withId(R.id.cityEdt)).perform(replaceText("Москва"), closeSoftKeyboard())
//
//        // Ожидание загрузки данных
            Thread.sleep(2000)
        }
    }
}