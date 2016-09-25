package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.utils.GitUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by jbragg on 9/15/16.
 */
class GetAffectedFilesTask extends DefaultTask {
    GetAffectedFilesTask(){
        setGroup("Gitlab")
    }

    @TaskAction
    def getAffectedFiles(){
        GitUtils.getAffectedFiles(getLogger())
    }
}
