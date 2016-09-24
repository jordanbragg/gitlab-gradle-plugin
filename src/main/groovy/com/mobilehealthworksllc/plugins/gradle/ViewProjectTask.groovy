package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.gitlab.Projects
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

/**
 * Created by jbragg on 9/15/16.
 */
class ViewProjectTask extends DefaultTask{

    ViewProjectTask(){
        setGroup("Gitlab")
    }

    @TaskAction
    def viewProject(){
        Projects.viewCurrentProject(project,
                {resp ->
                    def responseStr = "${resp.entity.content}"
                    getLogger().lifecycle(JsonOutput.prettyPrint(responseStr))
                },
                {failure ->
                    throw new TaskExecutionException("Request failed with status ${failure.status}")
                })
    }
}
