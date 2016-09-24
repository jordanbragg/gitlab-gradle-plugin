package com.mobilehealthworksllc.plugins.gradle.internal.gitlab

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.gradle.api.Project

/**
 * Created by jbragg on 9/23/16.
 */
class Projects {
    def static void getProjects(Project project, Closure onSuccess, Closure onFailure){
        def token = GitlabApi.getPrivateToken()
        def http = new HTTPBuilder(GitlabApi.getProjectsUrl(project))
        http.ignoreSSLIssues()
        http.request(Method.GET) {
            uri.query = [private_token: token]
            contentType = ContentType.JSON

            response.success = onSuccess

            response.failure = onFailure
        }
    }

    def static void viewCurrentProject(Project project, Closure onSuccess, Closure onFailure){
        def token = GitlabApi.getPrivateToken()
        def http = new HTTPBuilder(GitlabApi.getProjectUrl(project))
        http.ignoreSSLIssues()
        http.request(Method.GET) {
            uri.query =  [ private_token: token ]
            contentType = ContentType.JSON

            response.success = onSuccess

            response.failure = onFailure
        }
    }
}
