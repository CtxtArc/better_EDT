package com.example.better_edt.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

class ConfigRepository(private val context: Context) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File
        get() = File(context.filesDir, "config.json")

    fun loadConfig(): CourseConfig {
        return try {
            if (configFile.exists()) {
                val jsonText = configFile.readText(Charsets.UTF_8)
                val loadedConfig = gson.fromJson(jsonText, CourseConfig::class.java)
                if (loadedConfig != null) {
                    // Ensure default keys exist in map
                    val mergedCourses = CourseConfig.DEFAULT_GROUP_COURSES.toMutableMap()
                    loadedConfig.groupCourses.forEach { (k, v) -> mergedCourses[k] = v }

                    val mergedResources = CourseConfig.DEFAULT_GROUP_RESOURCES.toMutableMap()
                    loadedConfig.groupResources.forEach { (k, v) -> mergedResources[k] = v }

                    loadedConfig.copy(
                        groupCourses = mergedCourses,
                        groupResources = mergedResources
                    )
                } else {
                    CourseConfig()
                }
            } else {
                val defaultConfig = CourseConfig()
                saveConfig(defaultConfig)
                defaultConfig
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CourseConfig()
        }
    }

    fun saveConfig(config: CourseConfig) {
        try {
            val jsonText = gson.toJson(config)
            configFile.writeText(jsonText, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetToDefault(): CourseConfig {
        val defaultConfig = CourseConfig()
        saveConfig(defaultConfig)
        return defaultConfig
    }
}
