package com.intellij.remoterobot.services

import com.intellij.remoterobot.data.ObjectContainer
import com.intellij.util.lang.UrlClassLoader
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
            return when (val className = classDesc.name) {
                Boolean::class.javaPrimitiveType!!.name -> Boolean::class.javaPrimitiveType
                Char::class.javaPrimitiveType!!.name -> Char::class.javaPrimitiveType
                Byte::class.javaPrimitiveType!!.name -> Byte::class.javaPrimitiveType
                Short::class.javaPrimitiveType!!.name -> Short::class.javaPrimitiveType
                Int::class.javaPrimitiveType!!.name -> Int::class.javaPrimitiveType
                Long::class.javaPrimitiveType!!.name -> Long::class.javaPrimitiveType
                Float::class.javaPrimitiveType!!.name -> Float::class.javaPrimitiveType
                Double::class.javaPrimitiveType!!.name -> Double::class.javaPrimitiveType
                Void.TYPE.name -> Void.TYPE
                else -> findClass(className, loader)
            } ?: throw IllegalArgumentException("Can't resolve class '${classDesc.name}'")
        }

        private fun findClass(name: String, loader: ClassLoader?): Class<*>? {
            checkPackageAccess(name)
            val classLoader = loader ?: Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
            if (loader != null) {
                try {
                    return Class.forName(name, false, classLoader)
                } catch (exception: ClassNotFoundException) {
                    // use default class loader instead
                } catch (exception: SecurityException) {
                    // use default class loader instead
                }
            }
            return Class.forName(name)
        }

        private fun checkPackageAccess(name: String) {
            val s = System.getSecurityManager()
            if (s != null) {
                var cname = name.replace('/', '.')
                if (cname.startsWith("[")) {
                    val b = cname.lastIndexOf('[') + 2
                    if (b > 1 && b < cname.length) {
                        cname = cname.substring(b)
                    }
                }
                val i = cname.lastIndexOf('.')
                if (i != -1) {
                    s.checkPackageAccess(cname.substring(0, i))
                }
            }
        }
    }

    private class ByteClassLoader(parent: ClassLoader) : ClassLoader(parent) {
        fun loadClass(className: String, classBytes: ByteArray): Class<*> {
            return findLoadedClass(className) ?: defineClass(className, classBytes, 0, classBytes.size)
        }
    }
}