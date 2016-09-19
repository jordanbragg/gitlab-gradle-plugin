package com.mobilehealthworksllc.plugins.gradle

import org.gradle.api.Project

/**
 * Created by jbragg on 9/15/16.
 */
class UrlUtils {
    public static String buildBaseUrl(Project project){
        return project.gitlab.baseUri + project.gitlab.apiUri
    }
}
