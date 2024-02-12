package com.intellij.remoterobot.fixtures

import com.google.gson.Gson
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.log
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.waitFor
import org.intellij.lang.annotations.Language
import java.time.Duration
import kotlin.math.roundToInt


/**
## Use it to work with JCEF embedded browser.

The fixture provides some requirements:
- found component must have a parent with JBCefBrowser.instance clientProperty
- `ide.browser.jcef.jsQueryPoolSize=10000` - possible count of callback slots must be specified for running IDE
in build.gradle:
runIdeForUiTests {
systemProperty "ide.browser.jcef.jsQueryPoolSize", "10000"
}
One fixture takes a one slot of the reserved callback slots. So if you create a lot of fixtures across the one browser
the slots could be run out. It is preferable to create one fixture and then reuse it.
 */
class JCefBrowserFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    ComponentFixture(remoteRobot, remoteComponent) {
    companion object {
        @JvmStatic
        val canvasLocator = byXpath("//div[contains(@class, 'Canvas') or contains(@class, 'JBCef')]")

        @JvmStatic
        val macLocator = byXpath("//div[@class='JBCefOsrComponent']")

        private const val FINDER = "window.elementFinder"
        private const val INIT_FINDER = """
            
            $FINDER = {};
            
            function Elm(element) {
                this.tag = element.tagName;
                this.html = element.outerHTML;
                this.location = element.getBoundingClientRect();
                this.xpath = '/' + window.elementFinder.getPathTo(element).toLowerCase();
            }
            
            $FINDER.findElement = (xpath) => {
                console.log("findElement: by '" + xpath + "'")
                return document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
            };
            
            $FINDER.scrollByXpath = (xpath) => {
                console.log("scrollByXpath: to '" + xpath + "'")
                const element = window.elementFinder.findElement(xpath)
                console.log("found: " + element)
                element.scrollIntoView();
                return "success";
            };
            
            $FINDER.findElements = (xpath) => {
                console.log("findElements: by '" + xpath + "'")
                const foundElements = [];
                const nodesSnapshot = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null );
                for (let i = 0; i < nodesSnapshot.snapshotLength; i++ ) {
                    foundElements.push( nodesSnapshot.snapshotItem(i) );
                }
                console.log("found " + foundElements.length)
                const result = foundElements.map((it) => new Elm(it))
                return JSON.stringify(result);
            };
            
            $FINDER.getPathTo = (element) => {               
                if (element.tagName.toLowerCase() === 'html') {
                    return element.tagName;
                }
                let ix = 0;
                const siblings = element.parentNode.childNodes;
                for (let i = 0; i < siblings.length; i++) {
                    const sibling = siblings[i];
                    if (sibling === element) {
                        return $FINDER.getPathTo(element.parentNode) + '/' + element.tagName + '[' + (ix + 1) + ']';
                    }
                    if (sibling.nodeType === 1 && sibling.tagName === element.tagName) {
                        ix++;
                    }
                }
            };
        """

        private val gson = Gson()
        private const val JB_BROWSER_KEY = "JBCefBrowser.instance"
        private const val JB_CEF_BROWSER_KEY = "__jbCefBrowser"
        private const val CEF_BROWSER_KEY = "__cefBrowser"
        private const val QUERY_KEY = "__Query"
        private const val RESULT_KEY = "__Result"
        private const val EXECUTE_JS_TIMEOUT_MS = 3000
        private const val EXECUTE_JS_POLL_INTERVAL_MS = 50
    }

    init {
        this.initializeBrowser()
    }

    data class Location(val x: Double, val y: Double, val width: Double, val height: Double)
    data class ElementData(val tag: String, val html: String, val location: Location, val xpath: String)
    data class ElementDataList(val elms: List<ElementData>)
    class JCEFScriptExecutionError(script: String, e: Throwable) :
        AssertionError("Failed to execute script:\n$script", e)

    private fun initializeBrowser() = step("Inject JS scripts into the Embedded browser") {
        runJs(
            """
            let currentComponent = component;
            let jbCefBrowser = null;
            while (currentComponent !== null && jbCefBrowser === null) {
                try {
                    jbCefBrowser = currentComponent.getClientProperty("$JB_BROWSER_KEY");
                } catch (e) { }
                if (jbCefBrowser === null) {
                    currentComponent = currentComponent.getParent();
                }
            }
            if (!jbCefBrowser) {
                throw new Error("Can't find cef browser");
            }
            const cefBrowser = jbCefBrowser.getCefBrowser();

            if (!cefBrowser) {
                throw new Error("Can't find cef browser");
            }
            local.put("$JB_CEF_BROWSER_KEY", jbCefBrowser);
            local.put("$CEF_BROWSER_KEY", cefBrowser);
            
            const query = com.intellij.ui.jcef.JBCefJSQuery.create(jbCefBrowser)
            query.addHandler((result)=> ctx.put("$RESULT_KEY", result))
            local.put("$QUERY_KEY", query)            
        """
        )
    }

    fun findElement(@Language("XPath") xpath: String, wait: Duration = Duration.ofSeconds(5)): DomElement =
        step("find DOM element by '$xpath'") {
            var element: DomElement? = null
            try {
                waitFor(wait) {
                    element = findElements(xpath).firstOrNull()
                    element != null
                }
            } catch (e: Throwable) {
            }
            return@step element ?: throw IllegalStateException("No such element: $xpath")
        }

    fun exist(@Language("XPath") xpath: String): Boolean = findElements(xpath).isNotEmpty()

    fun findElements(@Language("XPath") xpath: String): List<DomElement> = step("find DOM elements by '$xpath'") {
        val result = executeJsInBrowser("$FINDER.findElements(\"${xpath.escapeXpath()}\")")
        try {
            return@step gson.fromJson("{elms: $result}", ElementDataList::class.java).elms
                .map { DomElement(this, it) }.toList()
        } catch (e: Throwable) {
            log.error("Can't find elements by xpath '$xpath'. JS result:\n$result")
            throw e
        }
    }

    fun scrollTo(@Language("XPath") xpath: String) {
        val result = executeJsInBrowser("$FINDER.scrollByXpath(\"${xpath.escapeXpath()}\")")
        if (result != "success") {
            throw IllegalStateException("Failed to scroll to element")
        }
    }

    fun getDom(): String {
        return executeJsInBrowser("""document.documentElement.outerHTML""")
    }

    fun executeJsInBrowser(@Language("JavaScript") js: String): String = synchronized(this) {
        val script = """
            const cefBrowser = local.get("$CEF_BROWSER_KEY");
            if (!cefBrowser) {
                throw new Error("$CEF_BROWSER_KEY was not initialized");
            }
            const query = local.get("$QUERY_KEY");
            if (!query) {
                throw new Error("$QUERY_KEY was not initialized");
            }
            const inProgress = "IN PROGRESS"
            local.put("$RESULT_KEY", inProgress);
            const getResult = () => local.get("$RESULT_KEY")

            const initScript = `${INIT_FINDER.makeItOneLine()}`
            cefBrowser.executeJavaScript(initScript, cefBrowser.getURL(), 0);
            
            cefBrowser.executeJavaScript(query.inject('$js'), cefBrowser.getURL(), 0);
            let x = 0;
            // noinspection EqualityComparisonWithCoercionJS
            while(getResult() == inProgress) {
                Thread.sleep($EXECUTE_JS_POLL_INTERVAL_MS);
                x++;
                if (x * $EXECUTE_JS_POLL_INTERVAL_MS > $EXECUTE_JS_TIMEOUT_MS) {
                    throw "No result from script '$js' in embedded browser. Check logs in browsers DevTools(right click at the browser)" 
                }
            }
            getResult();
        """

        try {
            return callJs(script)
        } catch (e: Throwable) {
            throw JCEFScriptExecutionError(script, e)
        }
    }

    private fun String.makeItOneLine(): String = split("\n")
        .map {
            var str = it.trim()
            if (str.isNotEmpty() && str.endsWith(";").not() && str.endsWith("{").not() && str.endsWith("}").not()) {
                str = "$str;"
            }
            str
        }
        .let { buildString { it.forEach { append("$it ") } } }

    private fun String.escapeXpath() = replace("'", "\\x27")
        .replace("\"", "\\x22")


    fun findElementByText(text: String): DomElement {
        return findElement("//*[text()='$text']")
    }

    fun findElementsByText(text: String): List<DomElement> {
        return findElements("//*[text()='$text']")
    }

    fun findElementByContainsText(text: String): DomElement {
        return findElement("//*[contains(text(), '$text')]")
    }

    fun findElementsByContainsText(text: String): List<DomElement> {
        return findElements("//*[contains(text(), '$text')]")
    }
}

class DomElement(val container: JCefBrowserFixture, var elementData: JCefBrowserFixture.ElementData) {
    private val x
        get() = elementData.location.x.roundToInt()

    private val y
        get() = elementData.location.y.roundToInt()

    private val width
        get() = elementData.location.width.roundToInt()

    private val height
        get() = elementData.location.height.roundToInt()

    private val centerX
        get() = x + width / 2

    private val centerY
        get() = y + height / 2

    private val xpath
        get() = elementData.xpath

    val html
        get() = elementData.html

    fun clickAtCenter() = step("click at ${elementData.tag}") {
        scroll()
        container.runJs(
            """
           robot.click(component, new Point($centerX, $centerY)) 
        """
        )
    }

    fun click() = step("click at ${elementData.tag}") {
        scroll()
        container.runJs(
            """
           robot.click(component, new Point(${x + height / 2}, $centerY)) 
        """
        )
    }

    fun scroll() = step("scroll to ${elementData.tag}") {
        container.scrollTo(xpath)
        elementData = container.findElement(xpath).elementData
    }

    override fun toString(): String {
        return elementData.html
    }
}