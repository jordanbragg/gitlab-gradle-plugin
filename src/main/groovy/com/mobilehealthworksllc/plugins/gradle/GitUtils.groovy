package com.mobilehealthworksllc.plugins.gradle;

/**
 * Created by jbragg on 9/16/16.
 */
public class GitUtils {

   public static String getBranchName(){
      def getBranchProc = 'git branch'.execute() | 'grep \\*'.execute() | ['awk','{print $2}'].execute()
      getBranchProc.waitFor()
      return getBranchProc.text.trim()
   }

   public static String getAffectedFiles(){
      def branchName = GitUtils.getBranchName()
      def command = "git diff --name-only origin/master...origin/"+branchName
      def affectedFilesProc = command.execute()
      affectedFilesProc.waitFor()
      println "FILES CHANGED IN BRANCH: ${branchName}"
      return affectedFilesProc.text
   }
}
