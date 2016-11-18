package com.mobilehealthworksllc.plugins.gradle.internal.utils

import com.mobilehealthworksllc.plugins.gradle.internal.gitlab.MergeRequests
import org.gradle.api.logging.Logger;

/**
 * Created by jbragg on 9/16/16.
 */
public class GitUtils {

   public static String getBranchName(){
      def getBranchProc = 'git branch'.execute() | 'grep \\*'.execute() | ['awk','{print $2}'].execute()
      getBranchProc.waitFor()
      return getBranchProc.text.trim()
   }

   public static String getAffectedFiles(project, Logger logger){
      def branchName = GitUtils.getBranchName()
      def command
      def isRemote = false
      def remoteBranch = "master"

      if(isBranchInRemote()){
         def mergeRequest = MergeRequests.getCurrentMergeRequest(project)
         remoteBranch = mergeRequest != null && mergeRequest.target_branch != null ? mergeRequest.target_branch : "master"
         command = "git diff --name-only origin/${remoteBranch}...origin/"+branchName
         isRemote = true
      }else{
         logger.lifecycle("BRANCH IS NOT PUSHED, CHECKING AGAINST LOCAL")
         command = "git diff --name-only origin/${remoteBranch}...${branchName}"
      }
      def affectedFilesProc = command.execute()
      affectedFilesProc.waitFor()
      def files = affectedFilesProc.text
      logger.lifecycle("FILES CHANGED IN ${isRemote ? "REMOTE" : "LOCAL"} BRANCH: ${branchName} AGAINST TARGET: ${remoteBranch}")
      logger.lifecycle("${files}")
      return files
   }

   public static boolean isBranchInRemote(){
      def branchName = getBranchName()
      def command = "git branch -a".execute() | "grep remotes/origin/${branchName}\$".execute()
      command.waitFor()
      def text = command.text.trim()
      def isInRemote = text == "remotes/origin/${branchName}"
      return isInRemote
   }

   public static boolean isAllChangesPushed(){
      def isBranchInRemote = isBranchInRemote()
      if(!isBranchInRemote) return false

      def branchName = getBranchName()
      def command = "git diff --name-only origin/${branchName}..${branchName}"
      def affectedFilesProc = command.execute()
      affectedFilesProc.waitFor()
      def files = affectedFilesProc.text
      files != null && files.size() > 0 ? false : true
   }

   public static void pushToOrigin(){
      def branchName = getBranchName()
      def command = "git push origin ${branchName}"
      command.execute().waitFor()
   }

   public static boolean isMasterBranch(String branchName) {
      return branchName != null && !branchName.isEmpty() && "master".equals(branchName)
   }
}
