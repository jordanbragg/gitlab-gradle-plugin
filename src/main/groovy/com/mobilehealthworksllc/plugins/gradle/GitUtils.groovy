package com.mobilehealthworksllc.plugins.gradle

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

   public static String getAffectedFiles(Logger logger){
      def branchName = GitUtils.getBranchName()
      def command
      def isRemote = false

      if(isBranchInRemote()){
         command = "git diff --name-only origin/master...origin/"+branchName
         isRemote = true
      }else{
         logger.lifecycle("BRANCH IS NOT PUSHED, CHECKING AGAINST LOCAL")
         command = "git diff --name-only origin/master...${branchName}"
      }
      def affectedFilesProc = command.execute()
      affectedFilesProc.waitFor()
      def files = affectedFilesProc.text
      logger.lifecycle("FILES CHANGED IN ${isRemote ? "REMOTE" : "LOCAL"} BRANCH: ${branchName}")
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
}
