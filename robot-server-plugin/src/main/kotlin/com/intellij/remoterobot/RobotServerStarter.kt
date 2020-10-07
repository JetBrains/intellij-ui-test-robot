package com.intellij.remoterobot

import com.intellij.ide.ApplicationInitializedListener

class RobotServerStarter : ApplicationInitializedListener {

    override fun componentsInitialized() {
        RobotServerImpl().startServer()
    }
}