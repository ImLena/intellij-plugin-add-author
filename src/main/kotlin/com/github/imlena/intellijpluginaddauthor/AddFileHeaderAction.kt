package com.github.imlena.intellijpluginaddauthor

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ScriptRunnerUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDocumentManager
import java.nio.charset.Charset


class AddFileHeaderAction : AnAction() {

    /**
     * This function execute git command to get authors of the code in current file
     * and add file header with names.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        val document: Document = editor!!.document

        val project: Project? = e.project

        val psiFile = PsiDocumentManager.getInstance(project!!).getPsiFile(document)
        val vFile = psiFile!!.originalFile.virtualFile
        val fullPath = vFile.path
        val pathToProject: String = project.basePath ?: ""

        val pathFromRepositoryRoot: String = fullPath.substring(pathToProject.length + 1)

        val cmd = "git shortlog -s -n $pathFromRepositoryRoot"

        /**
         * Execute command in command line.
         */
        val generalCommandLine = GeneralCommandLine(cmd)
        generalCommandLine.charset = Charset.forName("UTF-8")
        generalCommandLine.setWorkDirectory(pathToProject)

        val commandLineOutputStr = ScriptRunnerUtil.getProcessOutput(generalCommandLine)

        /**
         * Add file header.
         */
        CommandProcessor.getInstance().runUndoTransparentAction {
            ApplicationManager.getApplication().runWriteAction { document.insertString(0,  "/**\n" +
                    " * @author $commandLineOutputStr\n" +
                    " */\n") } //TODO parse output
        }

    }

    override fun isDumbAware(): Boolean {
        return false
    }

}