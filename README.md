# gitlab-gradle-plugin

## Description

This plug-in utilizes the [Gitlab API](https://docs.gitlab.com/ce/api/) to provide Gitlab features via Gradle. By applying this plugin and providing the configuration you can do anything from viewing project(s), viewing merge request(s), and creating merge requests and notes. Another feature of this plugin is the ability to define observers so that when a merge request is created, it will automatically @ tag everyone who listed themselves as an owner for the given file pattern.

## Usage

To use the Gradle Gitlab plugin, include the following your buildscript:

```apply plugin: 'com.mobilehealthworksllc.gitlab'```

To add it to your dependencies:

```classpath "com.mobilehealthworksllc:gitlab-gradle-plugin:1.0"```

##Tasks

|Task|Description|Dependencies|
|----|-----------|------------|
|listProjects|This task will list all the projects you can see on the given gitlab instance in pretty print JSON|projectName, baseUri, gitlab_token|
|getProjectId|This task will return the project ID of the given project. This is useful for populating the configuration which makes many of these other tasks work.|projectName, baseUri, gitlab_token|
|viewProject|View the current project in pretty print JSON.|projectId, baseUri, gitlab_token|
|viewMergeRequests|View all the open merge requests for the project in pretty print JSON|projectId, baseUri, gitlab_token|
|showAffectedFiles|Show all changes in your branch as apposed to origin/master. If branch is not pushed to remote origin, it will present the changes in your local branch against master.|git|
|listObservers|List affected owners/observers defined in the gradle configuration. This uses showAffected files to determine the changed files and runs them against the defined file patterns in the gradle config. This prints an array of gitlab user names.|showAffectedFiles, observers configured|
|createMergeRequest|Creates a merge request against master with your current remote branch. This task will prompt you if you have changes locally that are not on remote origin. After creating the merge request, it will return the URL to browse to and will also add a comment tagging any configured owners/observers that were affected.|listObservers, projectId, baseUri, gitlab_token|

##Example

```
gitlab {
    projectId = "123"
    projectName = "test"
    baseUri = "https://gitlab.mysite.com"
    observers {
        "gradle files" {
            filePattern = ".*[.]gradle"
            owners = [ 'jbragg' ]   
        }
        "tests" {
            filePattern = ".*/tests/"
            owners = [ 'jbragg' ]   
        }
    }
}
```

You can get your project id by running ```gradle getProjectId```. This requires you to provide the projectName and the baseUri.

```
gitlab {
    projectName = "test"
    baseUri = "https://gitlab.mysite.com"
}
```
