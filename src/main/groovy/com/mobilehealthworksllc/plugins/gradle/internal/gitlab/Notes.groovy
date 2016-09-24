package com.mobilehealthworksllc.plugins.gradle.internal.gitlab

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import org.gradle.api.Project

/**
 * Created by jbragg on 9/23/16.
 */
class Notes {
    def static void addNote(Project project, String mergeRequestId, String note, Closure onResponse){
        def token = GitlabApi.getPrivateToken()
        def http = new HTTPBuilder(GitlabApi.getMergeRequestCommentUrl(project, mergeRequestId))
        http.ignoreSSLIssues()
        http.post(body: [body: note],
                requestContentType: ContentType.URLENC,
                query: [private_token: token]) { cResp ->
                onResponse.call(cResp)
        }
    }
}
