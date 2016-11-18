package com.mobilehealthworksllc.plugins.gradle.internal.gitlab

import com.mobilehealthworksllc.plugins.gradle.internal.utils.CommonUtils
import com.mobilehealthworksllc.plugins.gradle.internal.utils.GitUtils
import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.gradle.api.Project
import org.gradle.api.tasks.TaskExecutionException

/**
 * Created by jbragg on 9/23/16.
 */
class MergeRequests {
    def static void viewMergeRequests(Project project, Closure onSuccess, Closure onFailure){
        def token = GitlabApi.getPrivateToken()
        def http = new HTTPBuilder(GitlabApi.getMergeRequestsUrl(project))
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

    def static Object getCurrentMergeRequest(project){
        MergeRequests.viewMergeRequests(project, {resp ->
            def response = new JsonSlurper().parseText("${resp.entity.content}")
            def branchName = GitUtils.getBranchName()
            response.each {
                if(branchName == it.source_branch){
                    return it
                }
            }
            return null
        },{failure ->
            throw new TaskExecutionException(this,new Exception("Request failed with status ${failure.status}"))
        });
    }
}
