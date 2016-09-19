package com.mobilehealthworksllc.plugins.gradle

import com.mobilehealthworksllc.plugins.gradle.internal.Gitlab
import com.mobilehealthworksllc.plugins.gradle.internal.Observer
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

        project.task('viewProject', type: ViewProjectTask)

        project.task('viewMergeRequests', type: ViewMergeRequestsTask)

        project.task('showAffectedFiles', type: GetAffectedFilesTask)

        project.task('listAffectedOwners', type: ListOwnersTask)

        project.task('createMergeRequest', type: CreateMergeRequestTask)
    }
}