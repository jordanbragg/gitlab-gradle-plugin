package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.model.Gitlab
import com.mobilehealthworksllc.plugins.gradle.internal.model.Observer
import org.gradle.api.Plugin
import org.gradle.api.Project

class GitlabPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create("gitlab",Gitlab)
        project.gitlab.extensions."observers" = project.container(Observer) {
            def groupExtension =
                    project.gitlab.observers.extensions.create("$it", Observer, "$it".toString())
            groupExtension
        }
        project.task("listProjects", type: ListProjectsTask)

        project.task("getProjectId", type: GetProjectIdTask)

        project.task('viewProject', type: ViewProjectTask)

        project.task('viewMergeRequests', type: ViewMergeRequestsTask)

        project.task('createMergeRequest', type: CreateMergeRequestTask)

        project.task('showAffectedFiles', type: GetAffectedFilesTask)

        project.task('listObservers', type: ListOwnersTask)

        project.task('addObservers', type: AddAffectedOwners)
    }
}