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
        def Set<String> watchers = new HashSet<>()

        if(project.gitlab.observers == null){
            getLogger().lifecycle("There are no observers defined.")
            return;
        }

        project.gitlab.observers.each { observer ->
            if(observer.filePatterns){
                observer.filePatterns.any { pattern ->
                    if (matches(combinedFileString, pattern)){
                        if(observer.owners)
                            owners.addAll(observer.owners)
                        if(observer.watchers)
                            watchers.addAll(observer.watchers)
                        return true
                    }
                    return false
                }
            }else{
                if(matches(combinedFileString, observer.filePattern)){
                    if(observer.owners)
                        owners.addAll(observer.owners)
                    if(observer.watchers)
                        watchers.addAll(observer.watchers)
                }
            }
        }
        getLogger().lifecycle("Owners of merge ${owners}")
        getLogger().lifecycle("Watchers of merge ${watchers}")
        project.ext.owners = owners
        project.ext.watchers = watchers
    }

    def matches(String src, String pattern){
        def patternObject = ~(".*"+pattern+".*")
        def matcher = src =~ patternObject
        return matcher.matches()
    }
}
