package com.mobilehealthworksllc.plugins.gradle.internal.gitlab

import com.mobilehealthworksllc.plugins.gradle.internal.utils.DialogUtils
import com.mobilehealthworksllc.plugins.gradle.internal.utils.UrlUtils
import org.gradle.api.Project
import org.gradle.api.tasks.TaskExecutionException

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

    public static String getPrivateToken(){
        def token = System.getenv('gitlab_token')
        if(token == null){
            token = DialogUtils.promptUserForPrivateToken()
            if(token == null || token.isEmpty())
                throw new TaskExecutionException("A private token must be provided in order to perform requests. Set via gitlab_token environment variable or provide in the prompt.")
        }
        return token
    }
}
