package test

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.dataExtractor.textRendererFixtures.ComboboxTextFixture
import com.intellij.remoterobot.fixtures.dataExtractor.textRendererFixtures.ListTextFixture
import com.intellij.remoterobot.fixtures.dataExtractor.textRendererFixtures.TreeTextFixture
import com.intellij.remoterobot.search.locators.byXpath
import org.junit.Test

class CellReaderTest {
    @Test
    fun checkCellReader() {
        System.setProperty("robot.encryption.enabled", "true")
        System.setProperty("robot.encryption.password", "my super secret")
        val remoteRobot = RemoteRobot("http://127.0.0.1:8080")

        val lists = remoteRobot.findAll<ListTextFixture>(byXpath("//div[@class='JBList']"))

        lists.forEach {
            println("--------")
            it.list().forEach {
                println(it)
            }
        }
        println("==============")


        val comboboxes = remoteRobot.findAll<ComboboxTextFixture>(byXpath("//div[@class='ComboBox']"))
        comboboxes.forEach {
            println("-------------------cccc-")
            it.list().forEach {
                println(it)
            }
        }

        val projectView = remoteRobot.find<TreeTextFixture>(byXpath("//div[@class='ProjectViewTree']"))

        println(projectView.valueAt(1))
    }
}