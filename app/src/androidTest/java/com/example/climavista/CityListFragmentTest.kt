package com.example.climavista

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.climavista.ui.MainActivity
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CitySearchFragmentTest {

    @Test
    fun testSearchCityAndDisplayCityList() {
        // Запуск MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Проверка, что поле для поиска отображено
        onView(withId(R.id.cityEdt)).check(matches(isDisplayed()))

        // Ввод текста в поле поиска
        onView(withId(R.id.cityEdt)).perform(typeText("Москва"), closeSoftKeyboard())

        // Ожидание загрузки данных
        Thread.sleep(2000)

        // Проверка, что список городов отображается
        onView(withId(R.id.cityView))
            .check(matches(hasDescendant(withText("Москва"))))

    }
}
