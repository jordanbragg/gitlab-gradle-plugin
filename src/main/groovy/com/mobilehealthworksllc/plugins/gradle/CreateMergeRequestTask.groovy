package com.mobilehealthworksllc.plugins.gradle

import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

/**
 * Created by jbragg on 9/15/16.
 */
class CreateMergeRequestTask extends DefaultTask {

    CreateMergeRequestTask(){
        setGroup("Gitlab")
        dependsOn.add('listAffectedOwners')
    }

    @TaskAction
    def createMergeRequest() {
        def branchName = GitUtils.getBranchName()
        if (isMasterBranch(branchName)) {
            println "If you want to merge master into another branch, please specify source and target branches (future)"
        } else {
            println "CREATING MERGE REQUEST FOR ${branchName}"
            def http = new HTTPBuilder(GitlabApi.getMergeRequestsUrl(project))
            http.ignoreSSLIssues()
            http.post(body: [source_branch: branchName, target_branch: 'master', title: "${branchName}"],
                    requestContentType: ContentType.URLENC,
                    query: [private_token: "${System.getenv('gitlab_token')}"]) { resp ->
                if(resp.status == 201){
                    def response = new JsonSlurper().parseText("${resp.entity.content}")
                    println "Review created: ${project.gitlab.baseUri}/${project.gitlab.projectName}/${project.gitlab.projectName}/merge_requests/${response.iid}"
                    addCommentToMergeRequest(project,"${response.id}")
                }else {
                    println "Failed to create merge request with status ${resp.status}"
                }
            }
        }
    }

    def addCommentToMergeRequest(Project project, String mergeRequestId){
        def hhtp = new HTTPBuilder(GitlabApi.getMergeRequestCommentUrl(project, mergeRequestId))
        hhtp.ignoreSSLIssues()
        def ownerBody = generateOwnerString(project)
        hhtp.post(body: [body: ownerBody],
                requestContentType: ContentType.URLENC,
                query: [private_token: "${System.getenv('gitlab_token')}"]) { cResp ->
            if(cResp.status == 201){
                println "Added mentions of owners"
            }else{
                println "Failed with status: ${cResp.status}"
            }
        }
    }

    def String generateOwnerString(Project project){
        def ownerString = "Owners affected: "
        project.owners.each { owner ->
            ownerString += "@${owner} "
        }
        return ownerString
    }

    def boolean isMasterBranch(String branchName){
       return branchName != null && !branchName.isEmpty() && "master".equals(branchName)
    }
}
