package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.gitlab.Projects
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

/**
 * Created by jbragg on 9/23/16.
 */
class GetProjectIdTask extends DefaultTask {
    GetProjectIdTask(){
        setGroup("Gitlab")
    }

    @TaskAction
    def getProjectId(){
        Projects.getProjects(project,
                { resp ->
                    def response = new JsonSlurper().parseText("${resp.entity.content}")
                    def isFound = false
                    response.each { proj ->
                        if(project.gitlab.projectName.equals(proj.name)){
                            getLogger().lifecycle("Project id for ${project.gitlab.projectName} : ${proj.id}")
                            isFound = true
                            return;
                        }
                    }
                    if(!isFound)
                        throw new TaskExecutionException(this,new Exception("Could not find project named ${project.gitlab.projectName}"))
                },
                { failure ->
                    throw new TaskExecutionException(this,new Exception("Request failed with status: ${failure.status}"))
                })
    }
}
