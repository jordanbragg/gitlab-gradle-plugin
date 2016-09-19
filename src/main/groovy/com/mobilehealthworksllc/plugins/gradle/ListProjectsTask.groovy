package com.mobilehealthworksllc.plugins.gradle

import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Created by jbragg on 9/15/16.
 */
public class ListProjectsTask extends DefaultTask {

    ListProjectsTask() {
        setGroup("Gitlab")
    }

    @TaskAction
    def listProjects() {
        def http = new HTTPBuilder(GitlabApi.getProjectsUrl(project))
        http.ignoreSSLIssues()
        http.request(Method.GET) {
            uri.query = [private_token: "${System.getenv('gitlab_token')}"]
            contentType = ContentType.JSON

            response.success = { resp ->
                def responseStr = "${resp.entity.content}"
                println JsonOutput.prettyPrint(responseStr)
            }

            response.failure = { resp ->
                println "Request failed with status ${resp.status}"
            }
        }
    }
}
