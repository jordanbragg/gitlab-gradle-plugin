package com.mobilehealthworksllc.plugins.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by jbragg on 9/15/16.
 */
class ListOwnersTask extends DefaultTask {
    ListOwnersTask(){
        setGroup("Gitlab")
    }

    @TaskAction
    def listOwners(){
        def files = GitUtils.getAffectedFiles()
        def String combinedFileString = files.split('\n').join(',')
        def Set<String> owners = new HashSet<>()
        project.gitlab.observers.each { observer ->
            def pattern = ~(".*"+observer.filePattern+".*")
            def matcher = combinedFileString =~ pattern
            if (matcher.matches()){
                owners.addAll(observer.owners)
            }
        }
        println "Owners of merge"
        println owners
        project.ext.owners = owners
    }
}
