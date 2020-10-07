package com.intellij.remoterobot.services

import com.intellij.remoterobot.data.ObjectContainer
import com.intellij.util.lang.UrlClassLoader
import com.sun.beans.finder.ClassFinder
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectStreamClass

object LambdaLoader {

    private val byteClassLoader = ByteClassLoader(javaClass.classLoader)
    private var tempDebuggerClassLoader: ClassLoader? = null

    fun getFunction(container: ObjectContainer): Any {
        if (container.className.contains("generated_for_debugger_class", true)) {
            loadClassForDebugger(container.className, container.classMap)
            return readObject(container.objectBytes, tempDebuggerClassLoader!!)
        } else {
            container.classMap.forEach { byteClassLoader.loadClass(it.key, it.value) }
        }
        return readObject(container.objectBytes, byteClassLoader)
    }

    private fun loadClassForDebugger(name: String, classMap: Map<String, ByteArray>): Class<*> {
        val pluginClsLoader = this::class.java.classLoader
        tempDebuggerClassLoader = UrlClassLoader.build().parent(pluginClsLoader).get()

        val defineClassMethod = ClassLoader::class.java.declaredMethods.first {
            it.name == "defineClass" && it.parameterTypes.size == 4
        }

        defineClassMethod.isAccessible = true
        classMap.forEach { (clName, byteArray) ->
            defineClassMethod.invoke(
                tempDebuggerClassLoader,
                clName,
                byteArray,
                0,
                byteArray.size
            )
        }
        return tempDebuggerClassLoader!!.loadClass(name)
    }

    private fun readObject(bytes: ByteArray, classLoader: ClassLoader): Any =
        ObjectInputStreamWithLoader(bytes.inputStream(), classLoader).use { it.readObject() }

    private class ObjectInputStreamWithLoader constructor(inputStream: InputStream, private val loader: ClassLoader) :
        ObjectInputStream(inputStream) {

        override fun resolveClass(classDesc: ObjectStreamClass): Class<*> {
            val cname = classDesc.name
            return ClassFinder.resolveClass(cname, this.loader)
        }
    }

    private class ByteClassLoader(parent: ClassLoader) : ClassLoader(parent) {
        fun loadClass(className: String, classBytes: ByteArray): Class<*> {
            return findLoadedClass(className) ?: defineClass(className, classBytes, 0, classBytes.size)
        }
    }
}