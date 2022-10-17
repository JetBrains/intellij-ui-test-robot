package com.intellij.remoterobot.recorder

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.remoterobot.recorder.ui.RecordUITestFrame
import com.intellij.remoterobot.recorder.ui.RecordUITestModel

class RecorderService : Disposable {

    companion object {
        fun getInstance() = ApplicationManager.getApplication().getService(RecorderService::class.java)
            ?: throw IllegalStateException("RecorderService does not exist")
    }

    var isOpened: Boolean = false
        private set

    fun getCode(): String {
        return currentModel?.code ?: ""
    }


    private var currentModel: RecordUITestModel? = null
    override fun dispose() {
    }

    fun openUI() {
        if (isOpened.not()) {
            isOpened = true
            currentModel = RecordUITestModel(this)
            currentModel?.let {
                RecordUITestFrame(it) {
                    isOpened = false
                    currentModel = null
                }
            }
        }
    }
}