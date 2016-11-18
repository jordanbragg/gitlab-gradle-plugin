package com.mobilehealthworksllc.plugins.gradle.internal.utils;

import org.gradle.api.Project;

/**
 * Created by jbragg on 11/17/16.
 */
class CommonUtils {
    def static String generateOwnerString(Project project) {
        def ownerString = "Owners Affected by changes: "
        project.owners.each { owner ->
                ownerString += "@${owner} "
        }
        return ownerString
    }
}
