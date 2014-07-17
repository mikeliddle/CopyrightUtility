//=======================CommentInsertion.cs==========================//
//  Author:    Connor Group Software Development team 2014            //
//  Copyright: Copyright (C) 2014 Connor Group. All rights reserved.  //
//  Email:     itconnorgp.com                                         //
//  Website:   http://www.connorgp.com                                //
//                                                                    //
//  The copyright to the source code and computer program(s)          //
//  herein is the property of Connor Group.The source code            //
//  and program(s) may be used and/or copied only with the            //
//  written permission of Connor Group or in accordance with          //
//  the terms and conditions stipulated in the                        //
//  agreement/contract under which the source code and                //
//  program(s) have been supplied.                                    //
//                                                                    //
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

    public class CommentInsertion
    {
        private static String comment;
        private static String endComment;

        /********************************ProcessFile********************************
          * Method that gets the file content, checks if it is the most
          * updated copyright text, then if it isn't removes and replaces it, 
          * otherwise it doesn't need to process the file, so it doesn't.
          * 
          * @param String filePath
          *         String containing the path of the filePath.
          * @param List<String> licenseText
          *         List containing the text to be inserted, or the copyright text.
          * @param String commentParameter
          *         String containing the character(s) that will be used to comment.
          **/
        public static Boolean ProcessFile(String filePath, String copyrightTextFilePath, String commentParameter, String endCommentParameter, String fileName)
        {
            comment = commentParameter;
            endComment = endCommentParameter;

            List<String> fileContent = new List<String>(File.ReadAllLines(filePath));
            List<String> licenseText = new List<String>(File.ReadAllLines(copyrightTextFilePath));

            //This block of code adds the filename to the header
            String titleLine = "";
            for (int i = 0; i < ((licenseText.get(0).length() - fileName.length()) / 2); i++)
            {
                titleLine = titleLine + "=";
            }
            titleLine = titleLine + fileName;
            for (int i = 0; i < ((licenseText.get(0).length() - fileName.length()) / 2) + ((licenseText.get(0).length() - fileName.length()) % 2); i++)
            {
                titleLine = titleLine + "=";
            }
            licenseText.add(0, titleLine);

            //This makes it so the files will only be updated if they need to be.
            if (needsUpdate(fileContent, licenseText))
            {
                removeExistingHeader(fileContent);
            }
            else
            {
                return false;
            }

            writeFile(fileContent, licenseText, filePath, fileName);
            return true;
        }

        /********************************NeedsUpdate********************************
          * Checks if the header matches the most recent copyright text.
          * 
          * @param List<String> fileContent
          *         List containing the content of the file to be edited
          * @param List<String> licenseText
          *         List containing the content of the copyright file.
          **/
        private static Boolean needsUpdate(List<String> fileContent, List<String> licenseText)
        {
            Boolean needsUpdate = true;

            int commentEndIndex = 0;
            for (int i = 0; fileContent.get(i).startsWith(comment) && i < licenseText.size(); i++)
            {
                //if it is the same as the license text with the comment, it will not need to be updated.
                if (fileContent.get(i).equals(comment + licenseText.get(i) + endComment))
                {
                    //it matches so far, so it will be true.
                    needsUpdate = false;
                }
                else
                {
                    //it isn't 100% correctly updated, so it will need to be updated.
                    return true;
                }
                commentEndIndex = i;
            }

            int j = 1;
            try
            {
                do
                {
                    //if the header extends past the most recent copyright file, then it isn't correct.
                    if (fileContent.get(commentEndIndex + j).startsWith(comment))
                    {
                        return true;
                    }
                    //Could be an empty line, so check for that.
                    else if (isNullOrEmpty(fileContent.get(commentEndIndex + j)))
                    {
                        fileContent.remove(commentEndIndex + j);
                    }
                    ++j;
                } while (fileContent.get(commentEndIndex + j).startsWith(comment) || isNullOrEmpty(fileContent.get(commentEndIndex + j)));
            }
            catch (Exception e)
            {
                //write the log file with the error message.
                return true;
            }
            return needsUpdate;
        }

        /********************************WriteFile********************************
         * inserts the copyright into the file then writes it.
         * 
         * @param List<String> fileContent
         *      Content of the file now without the old copyright.
         * @param List<String> licenseText
         *      Copyright text to insert.
         * @param String filePath
         *      Name of file to be written.
         **/
        private static void writeFile(List<String> fileContent, List<String> licenseText, String filePath, String fileName)
        {
            if (fileContent.get(0).startsWith("<?xml"))
            {
                for (int i = 0; i < licenseText.size(); i++)
                {
                    fileContent.add(i + 1, (comment + licenseText.get(i) + endComment));
                }
            }
            else
            {
                for (int i = 0; i < licenseText.size(); i++)
                {
                    fileContent.add(i, (comment + licenseText.get(i) + endComment));
                }
            }
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
                for (int i = 0; i < fileContent.size(); i++)
                {
                	out.write(fileContent.get(i));
                	out.newLine();
                }
                out.close();
            }
            catch (IOException e)
            {
                System.out.println("Exception ");       
            }
        }

        /********************************RemoveExistingHeader********************************
         * Checks if the header matches the most recent copyright text.
         * 
         * @param List<String> fileContent
         *         List containing the content of the file to be edited
         * @param List<String> licenseText
         *         List containing the content of the copyright file.
         **/
        private static void removeExistingHeader(List<String> fileContent)
        {
            for (int i = 0; i < fileContent.size(); i++)
            {

                //removes the lines that aren't in a multiline comment.
                if (fileContent.get(i).trim().startsWith(comment))
                {
                    for (int j = i; fileContent.get(j).trim().startsWith(comment);) 
                    {
                        fileContent.remove(j);
                    }
                }
                //finds the ending index of the comment.
                else if (fileContent.get(i).trim().startsWith(endComment) || fileContent.get(i).trim().endsWith(endComment))
                {
                    fileContent.remove(i);
                    break;
                }
                else
                {
                    break;
                }
            }
            //Remove empty lines at the beginning of the file
            while (fileContent.size() > 0 && isNullOrEmpty(fileContent.get(0)))
            {
                fileContent.remove(0);
            }
        }
        
        public static boolean isNullOrEmpty(String param) { 
            return param == null || param.trim().length() == 0;
        }
    }

