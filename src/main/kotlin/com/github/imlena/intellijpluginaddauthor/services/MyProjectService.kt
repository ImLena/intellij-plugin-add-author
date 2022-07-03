package com.github.imlena.intellijpluginaddauthor.services

import com.intellij.openapi.project.Project
import com.github.imlena.intellijpluginaddauthor.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
