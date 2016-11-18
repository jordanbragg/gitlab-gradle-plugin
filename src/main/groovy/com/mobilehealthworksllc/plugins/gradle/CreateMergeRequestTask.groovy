package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.gitlab.MergeRequests
import com.mobilehealthworksllc.plugins.gradle.internal.gitlab.Notes
import com.mobilehealthworksllc.plugins.gradle.internal.utils.CommonUtils
import com.mobilehealthworksllc.plugins.gradle.internal.utils.DialogUtils
import com.mobilehealthworksllc.plugins.gradle.internal.utils.GitUtils
import groovy.json.JsonSlurper
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

    /**
     * This task does the following:
     * 1. Validates and prompts to ensure remote branch is in place and all changes are pushed
     * 2. Create a merge request for current branch
     * 3. Adds comment to review to notify affected owners/observers
     * @return
     */
    @TaskAction
    def createMergeRequest() {
        def branchName = GitUtils.getBranchName()
        validateBranch(branchName)
        getLogger().lifecycle("Creating Merge Request For ${branchName}")
        MergeRequests.create(project, branchName,
                { resp ->
                    if (resp.status == 201) {
                        def response = new JsonSlurper().parseText("${resp.entity.content}")
                        project.ext.reviewUrl = "${project.gitlab.baseUri}/${project.gitlab.projectName}/${project.gitlab.projectName}/merge_requests/${response.iid}"
                        getLogger().lifecycle("Review created: ${project.ext.reviewUrl}")
                        addCommentToMergeRequest(project, "${response.id}")
                        "open ${project.ext.reviewUrl}".execute().waitFor()
                    } else {
                        throw new TaskExecutionException(this, new Exception("Failed to create merge request with status ${resp.status}"))
                    }
                })
    }

    def validateBranch(String branchName) {
        if (GitUtils.isMasterBranch(branchName)) {
            throw new TaskExecutionException(this, new Exception("You are currently on master branch, please switch to the branch you wish to create a request for."))
        }
        ensureRemoteUpToDate(branchName)
    }

    def ensureRemoteUpToDate(String branchName) {
        def pushChanges
        if (!GitUtils.isAllChangesPushed()) {
            while (pushChanges == null) {
                getLogger().error("You must provide a value for whether to push the branch or not")
                pushChanges = DialogUtils.promptUserForPushingChanges()
            }

            if ("y".equals(pushChanges.toLowerCase())) {
                getLogger().lifecycle("Pushing branch ${branchName} to origin")
                GitUtils.pushToOrigin()
                getLogger().lifecycle("Changes pushed to origin successfully")
            } else {
                getLogger().lifecycle("Local changes not pushed to origin")
                if (!GitUtils.isBranchInRemote()) {
                    throw new TaskExecutionException(this, new Exception("Cannot create merge because the branch is not in origin. Please re-run and accept pushing when prompted."))
                }
            }
        }
    }

    def addCommentToMergeRequest(Project project, String mergeRequestId) {
        def ownerBody = CommonUtils.generateOwnerString(project)
        Notes.addNote(project, mergeRequestId, ownerBody,
                { resp ->
                    if (resp.status == 201) {
                        getLogger().lifecycle("Added mentions of owners")
                    } else {
                        throw new TaskExecutionException(this, new Exception("Failed with status: ${resp.status}"))
                    }
                })
    }


}
