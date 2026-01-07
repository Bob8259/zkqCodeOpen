package com.coc.zkqcode.utils.hotupdate

import android.content.Context
import dalvik.system.DexClassLoader
import java.io.File

object HotUpdate {

    fun <T> loadAndInstantiate(
        context: Context,
        jarFilePath: String,
        className: String
    ): T? {
        val jarFile = File(jarFilePath)
        if (!jarFile.exists()) {
            println("JAR file not found at $jarFilePath")
            return null
        }

        val loadedClass = loadClassFromJar(context, jarFilePath, className)
        if (loadedClass != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                val instance = loadedClass.getDeclaredConstructor().newInstance() as? T
                if (instance != null) {
//                    println("Hot-updated class $className loaded and instantiated successfully!")
                    return instance
                } else {
//                    println("Failed to cast instance of $className to target type.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                println("Failed to instantiate hot-updated class $className: ${e.message}")
            }
        }
        return null
    }

    private fun loadClassFromJar(context: Context, jarFilePath: String, className: String): Class<*>? {
        val optimizedDirectory = context.getDir("dex", Context.MODE_PRIVATE)
        val dexClassLoader = DexClassLoader(jarFilePath, optimizedDirectory.absolutePath, null, context.classLoader)
        return try {
            val loadedClass = dexClassLoader.loadClass(className)
//            println("Loaded class $className from $jarFilePath")
            loadedClass
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
//            println("Class $className not found in $jarFilePath: ${e.message}")
            null
        }
    }
}
