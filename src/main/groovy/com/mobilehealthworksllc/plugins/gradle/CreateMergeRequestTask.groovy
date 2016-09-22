package com.mobilehealthworksllc.plugins.gradle

import groovy.json.JsonSlurper
import groovy.swing.SwingBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

/**
 * Created by jbragg on 9/15/16.
 */
class CreateMergeRequestTask extends DefaultTask {

    CreateMergeRequestTask() {
        setGroup("Gitlab")
        dependsOn.add('listAffectedOwners')
    }

    @TaskAction
    def createMergeRequest() {
        def branchName = GitUtils.getBranchName()
        validateBranch(branchName)
        getLogger().lifecycle("Creating Merge Request For ${branchName}")
        def http = new HTTPBuilder(GitlabApi.getMergeRequestsUrl(project))
        http.ignoreSSLIssues()
        http.post(body: [source_branch: branchName, target_branch: 'master', title: "${branchName}"],
                requestContentType: ContentType.URLENC,
                query: [private_token: "${System.getenv('gitlab_token')}"]) { resp ->
            if (resp.status == 201) {
                def response = new JsonSlurper().parseText("${resp.entity.content}")
                println "Review created: ${project.gitlab.baseUri}/${project.gitlab.projectName}/${project.gitlab.projectName}/merge_requests/${response.iid}"
                addCommentToMergeRequest(project, "${response.id}")
            } else {
                println "Failed to create merge request with status ${resp.status}"
            }
        }
    }

    def validateBranch(String branchName) {
        if (isMasterBranch(branchName)) {
            throw new TaskExecutionException("You are currently on master branch, please switch to the branch you wish to create a request for.")
        }
        ensureRemoteUpToDate(branchName)
    }

    def ensureRemoteUpToDate(String branchName) {
        def pushChanges
        if (!GitUtils.isAllChangesPushed()) {
            while (pushChanges == null) {
                getLogger().error("You must provide a value for whether to push the branch or not")
                pushChanges = promptUserForPushingChanges()
            }

            if ("y".equals(pushChanges.toLowerCase())) {
                getLogger().lifecycle("Pushing branch ${branchName} to origin")
                GitUtils.pushToOrigin()
                getLogger().lifecycle("Changes pushed to origin successfully")
            } else {
                getLogger().lifecycle("Local changes not pushed to origin")
                if (!GitUtils.isBranchInRemote()) {
                    throw new TaskExecutionException("Cannot create merge because the branch is not in origin. Please re-run and accept pushing when prompted.")
                }
            }
        }
    }

    def String promptUserForPushingChanges() {
        def pushChanges
        new SwingBuilder().edt {
            dialog(modal: true, title: 'Problems with creating merge request', alwaysOnTop: true, resizable: false, locationRelativeTo: null, pack: true, show: true) {
                vbox {
                    label(text: "Changes aren't all push to remote, push now? (y/n): ")
                    def input1 = textField(columns: 1, id: 'name')
                    button(defaultButton: true, text: 'OK', actionPerformed: {
                        pushChanges = input1.text;
                        dispose();
                    })
                }
            }
        }
        return pushChanges
    }

    def addCommentToMergeRequest(Project project, String mergeRequestId) {
        def hhtp = new HTTPBuilder(GitlabApi.getMergeRequestCommentUrl(project, mergeRequestId))
        hhtp.ignoreSSLIssues()
        def ownerBody = generateOwnerString(project)
        hhtp.post(body: [body: ownerBody],
                requestContentType: ContentType.URLENC,
                query: [private_token: "${System.getenv('gitlab_token')}"]) { cResp ->
            if (cResp.status == 201) {
                println "Added mentions of owners"
            } else {
                println "Failed with status: ${cResp.status}"
            }
        }
    }

    def String generateOwnerString(Project project) {
        def ownerString = "Owners affected: "
        project.owners.each { owner ->
            ownerString += "@${owner} "
        }
        return ownerString
    }

    def boolean isMasterBranch(String branchName) {
        return branchName != null && !branchName.isEmpty() && "master".equals(branchName)
    }
}
