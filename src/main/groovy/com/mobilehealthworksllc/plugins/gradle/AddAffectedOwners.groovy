package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.gitlab.MergeRequests
import com.mobilehealthworksllc.plugins.gradle.internal.gitlab.Notes
import com.mobilehealthworksllc.plugins.gradle.internal.utils.CommonUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

/**
 * Created by jbragg on 9/15/16.
 */
class AddAffectedOwners extends DefaultTask {
    AddAffectedOwners() {
        setGroup("Gitlab")
        dependsOn.add('listObservers')
    }

    @TaskAction
    def addOwners() {
        getLogger().lifecycle("${project.ext.owners}")
        def mergeRequest = MergeRequests.getCurrentMergeRequest(project)
        if (mergeRequest != null) {
            Notes.addNote(project,
                    "" + mergeRequest.id,
                    CommonUtils.generateOwnerString(project) + "\n\n" + CommonUtils.generateWatchersString(project),
                    { noteResponse ->
                        if (noteResponse.status == 201) {
                            getLogger().lifecycle("Successfully added owners to review ${mergeRequest.iid}")
                        } else {
                            throw new TaskExecutionException(this, new Exception("Failed to create merge request with status ${noteResponse.status}"))
                        }
                    })
        }
    }
}
