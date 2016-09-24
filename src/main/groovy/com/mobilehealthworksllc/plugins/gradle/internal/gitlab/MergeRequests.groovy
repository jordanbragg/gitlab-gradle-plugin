package com.mobilehealthworksllc.plugins.gradle.internal.gitlab

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.gradle.api.Project

/**
 * Created by jbragg on 9/23/16.
 */
class MergeRequests {
    def static void viewMergeRequests(Project project, Closure onSuccess, Closure onFailure){
        def token = GitlabApi.getPrivateToken()
        def http = new HTTPBuilder(GitlabApi.getMergeRequestsUrl(project))
        println GitlabApi.getMergeRequestsUrl(project)
        http.ignoreSSLIssues()
        http.request(Method.GET) {
            uri.query =  [ private_token: token, state: 'opened' ]
            contentType = ContentType.JSON

            response.success = onSuccess

            response.failure = onFailure
        }
    }

    def static void create(Project project, String branchName, Closure onResponse){
        def token = GitlabApi.getPrivateToken()
        def http = new HTTPBuilder(GitlabApi.getMergeRequestsUrl(project))
        http.ignoreSSLIssues()
        http.post(body: [source_branch: branchName, target_branch: 'master', title: "${branchName}"],
                requestContentType: ContentType.URLENC,
                query: [private_token: token]) { resp ->
            onResponse.call(resp)
        }
    }
}
