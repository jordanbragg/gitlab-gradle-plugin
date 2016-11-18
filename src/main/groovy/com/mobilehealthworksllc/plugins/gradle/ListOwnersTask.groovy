package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.utils.GitUtils
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
        def files = GitUtils.getAffectedFiles(project, getLogger())
        def String combinedFileString = files.split('\n').join(',')
        def Set<String> owners = new HashSet<>()

        if(project.gitlab.observers == null){
            getLogger().lifecycle("There are no observers defined.")
            return;
        }

        project.gitlab.observers.each { observer ->
            def pattern = ~(".*"+observer.filePattern+".*")
            def matcher = combinedFileString =~ pattern
            if (matcher.matches()){
                owners.addAll(observer.owners)
            }
        }
        getLogger().lifecycle("Owners of merge ${owners}")
        project.ext.owners = owners
    }
}
