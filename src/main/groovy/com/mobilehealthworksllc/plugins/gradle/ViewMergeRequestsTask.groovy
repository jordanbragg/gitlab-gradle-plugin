package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.gitlab.MergeRequests
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

/**
 * Created by jbragg on 9/15/16.
 */
class ViewMergeRequestsTask extends DefaultTask {
    ViewMergeRequestsTask(){
        setGroup("Gitlab")
    }

    @TaskAction
    def viewRequests(){
        MergeRequests.viewMergeRequests(project,
                {resp ->
                    def responseStr = "${resp.entity.content}"
                    getLogger().lifecycle(JsonOutput.prettyPrint(responseStr))
                },
                {failure ->
                    throw new TaskExecutionException(this,new Exception("Request failed with status ${failure.status}"))
                })
    }
}
