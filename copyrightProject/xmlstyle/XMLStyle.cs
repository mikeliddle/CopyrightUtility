//============================XMLStyle.cs=============================//
//  Author:    Connor Group Software Development team 2014            //
//  Copyright: Copyright (C) 2014 Connor Group. All rights reserved.  //
//  Email:     it@connorgp.com                                        //
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
using System;
using System.IO;
using System.Collections.Generic;

namespace CopyrightAdditionService.Model
{

    public static class XmlStyle
    {
        private static int beginIndex;

        /********************************ProcessFile********************************
          * Method that gets the file content, checks if it is the most
          * updated copyright text, then if it isn't removes and replaces it, 
          * otherwise it doesn't need to process the file, so it doesn't.
          * 
          * @param string fileName
          *         string containing the path of the fileName.
          * @param List<string> licenseText
          *         list containing the text to be inserted, or the copyright text.
          **/
        public static void ProcessFile(string fileName, List<string> licenseText)
        {
            List<string> fileContent = new List<string>(File.ReadAllLines(fileName));

            if (fileContent[0].StartsWith(@"<!--"))
            {
                beginIndex = 0;
            }
            else
            {
                beginIndex = 1;
            }

            //if the code doesn't need to be updated, it won't be.
            if (needsUpdate(fileContent, licenseText))
            {
                removeExistingHeader(fileContent);
            }
            else
            {
                return;
            }

            writeFile(fileContent, licenseText, fileName);
        }

        /********************************WriteFile********************************
         * inserts the copyright into the file then writes it.
         * 
         * @param List<string> fileContent
         *      Content of the file now without the old copyright.
         * @param List<string> licenseText
         *      Copyright text to insert.
         * @param string fileName
         *      Name of file to be written.
         **/
        private static void writeFile(List<string> fileContent, List<string> licenseText, string fileName)
        {
            fileContent.Insert(beginIndex, "");
            fileContent.Insert(beginIndex, @"-->");

            fileContent.InsertRange(beginIndex, licenseText);

            fileContent.Insert(beginIndex, @"<!--");

            File.WriteAllLines(fileName, fileContent.ToArray());
        }

        /********************************NeedsUpdate********************************
         * Checks if the header matches the most recent copyright text.
         * 
         * @param List<string> fileContent
         *         List containing the content of the file to be edited
         * @param List<string> licenseText
         *         List containing the content of the copyright file.
         **/
        private static bool needsUpdate(List<string> fileContent, List<string> licenseText)
        {
            bool needsUpdate = true;

            int commentEndIndex = 0;
            for (int i = beginIndex; fileContent[i].StartsWith(@"<!--") && i < licenseText.Count; i++)
            {
                //if it is the same as, or contains the license text, it will not need to be updated.
                if (fileContent[i].Equals(licenseText[i]))
                {
                    //it matches so far, so it will be true.
                    needsUpdate = false;
                }
                //important for first line only.
                else if ((fileContent[i].Equals("<!--" + licenseText[i])))
                {
                    //it matches so far, so it will be true.
                    needsUpdate = false;
                }
                else
                {
                    //it isn't 100% correctly updated, so it will be updated.
                    return true;
                }
                commentEndIndex = i;
            }

            return needsUpdate;
        }

        /********************************RemoveExistingHeader********************************
          * Checks if the header matches the most recent copyright text.
          * 
          * @param List<string> fileContent
          *         List containing the content of the file to be edited
          * @param List<string> licenseText
          *         List containing the content of the copyright file.
          **/
        private static void removeExistingHeader(List<string> fileContent)
        {
            //default case, there is no comment, will only remove empty lines.
            int commentStartIndex = -1, commentEndIndex = -1;

            for (int i = 1; i < fileContent.Count; i++)
            {
                //finds the start index of the comment.
                if (fileContent[i].Trim().StartsWith(@"<!--"))
                {
                    commentStartIndex = i;
                }
                //Finds the end index of the comment.
                if (fileContent[i].Trim().EndsWith(@"-->"))
                {
                    commentEndIndex = i;
                    break;
                }
            }

            //if there is a comment, delete it.
            if (commentStartIndex != -1 && commentEndIndex != -1)
            {
                for (int i = commentStartIndex; i <= commentEndIndex; i++)
                {
                    fileContent.RemoveAt(commentStartIndex);
                }
            }

            //Remove empty lines at the beginning of the file
            while (fileContent.Count > 0 && string.IsNullOrEmpty(fileContent[1]))
            {
                fileContent.RemoveAt(1);
            }
        }
    }
}
