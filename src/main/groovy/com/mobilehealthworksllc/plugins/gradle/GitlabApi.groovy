package com.mobilehealthworksllc.plugins.gradle

import org.gradle.api.Project

/**
 * Created by jbragg on 9/15/16.
 */
class GitlabApi {

    public static String getProjectsUrl(Project project){
        return UrlUtils.buildBaseUrl(project) + "/projects"
    }

    public static String getProjectUrl(Project project){
        return getProjectsUrl(project) + "/" + project.gitlab.projectId
    }

    public static String getMergeRequestsUrl(Project project){
        return getProjectUrl(project) + "/merge_requests";
    }

    public static String getMergeRequestCommentUrl(Project project, String mergeRequestId){
        GitlabApi.getMergeRequestsUrl(project) + "/${mergeRequestId}/notes"
    }
}
