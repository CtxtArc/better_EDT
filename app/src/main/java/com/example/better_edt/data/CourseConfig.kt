package com.example.better_edt.data

data class CourseConfig(
    val groupCourses: Map<String, List<String>> = DEFAULT_GROUP_COURSES,
    val groupResources: Map<String, String> = DEFAULT_GROUP_RESOURCES,
    val academicYear: String = "2026-2027",
    val username: String = "",
    val password: String = ""
) {
    companion object {
        val DEFAULT_GROUP_COURSES: Map<String, List<String>> = mapOf(
            "G1" to listOf(
                "IN422", "SN422", "LV420", "SP420",
                "HU420", "IN420", "IN450", "MAC40", "MIC40", "PR422", "SN420", "TE420"
            ),
            "G2" to emptyList(),
            "G3" to listOf(
                "AU424", "AU426", "EP424", "EP426", "IN424", "IN426", "IN428", "SN424"
            ),
            "G4" to emptyList()
        )

        val DEFAULT_GROUP_RESOURCES: Map<String, String> = mapOf(
            "G1" to "5926",
            "G2" to "5925",
            "G3" to "5923",
            "G4" to "5924"
        )

        val ALL_GROUPS = listOf("G1", "G2", "G3", "G4")
    }

    /**
     * Returns a sorted list of all unique courses present in any group.
     */
    fun getAllCourses(): List<String> {
        return groupCourses.values.flatten().distinct().sorted()
    }

    /**
     * Checks if a course is assigned to a specific group.
     */
    fun isCourseInGroup(course: String, group: String): Boolean {
        return groupCourses[group]?.contains(course.uppercase()) == true
    }

    /**
     * Returns the set of groups a course belongs to.
     */
    fun getGroupsForCourse(course: String): Set<String> {
        val upperCourse = course.uppercase()
        return ALL_GROUPS.filter { group ->
            groupCourses[group]?.contains(upperCourse) == true
        }.toSet()
    }

    /**
     * Toggles a course for a given group and returns a new updated CourseConfig.
     */
    fun toggleCourseGroup(course: String, group: String): CourseConfig {
        val upperCourse = course.uppercase().trim()
        val currentList = groupCourses[group] ?: emptyList()
        val newList = if (currentList.contains(upperCourse)) {
            currentList - upperCourse
        } else {
            (currentList + upperCourse).distinct()
        }
        val updatedMap = groupCourses.toMutableMap()
        updatedMap[group] = newList
        return copy(groupCourses = updatedMap)
    }

    /**
     * Adds a new course assigned to the given groups.
     */
    fun addCourse(course: String, groups: Set<String>): CourseConfig {
        val upperCourse = course.uppercase().trim()
        if (upperCourse.isBlank()) return this

        val updatedMap = groupCourses.toMutableMap()
        ALL_GROUPS.forEach { group ->
            val currentList = updatedMap[group] ?: emptyList()
            if (groups.contains(group)) {
                updatedMap[group] = (currentList + upperCourse).distinct()
            }
        }
        return copy(groupCourses = updatedMap)
    }

    /**
     * Removes a course from all groups.
     */
    fun removeCourse(course: String): CourseConfig {
        val upperCourse = course.uppercase().trim()
        val updatedMap = groupCourses.mapValues { (_, list) ->
            list.filter { it != upperCourse }
        }
        return copy(groupCourses = updatedMap)
    }

    /**
     * Updates the resource ID for a group (e.g. G1 -> 5926).
     */
    fun updateGroupResource(group: String, resourceId: String): CourseConfig {
        val updatedMap = groupResources.toMutableMap()
        updatedMap[group] = resourceId.trim()
        return copy(groupResources = updatedMap)
    }

    /**
     * Updates credentials for Basic Auth HTTP requests.
     */
    fun updateCredentials(username: String, password: String): CourseConfig {
        return copy(username = username.trim(), password = password)
    }
}
