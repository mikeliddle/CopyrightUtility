//========================CommentInsertion.cs=========================//
//  Author:    Connor Group Software Development team 2014(Mike)      //
//  Copyright: Copyright (C) 2014 Connor Group. All rights reserved.  //
//  Email:     mike.liddle@connorgp.com                               //
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
using System.Collections.Generic;
using System.IO;

namespace DebugCopyrightAdditionService
{
    public static class CommentInsertion
    {
        private static string _comment;
        private static string _endComment;
        private static readonly log4net.ILog Log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        /// <summary>
        ///     Method that gets the file content, checks if it is the most
        ///     updated copyright text, then if it isn't removes and replaces it,
        ///     otherwise it doesn't need to process the file, so it doesn't.
        /// </summary>
        /// <param name="filePath"> String containing the path of the filePath.</param>
        /// <param name="copyrightTextFilePath"> String containing the path of the copyrightTextFile.</param>
        /// <param name="endCommentParameter"> String Containing the EndComment to be used.</param>
        /// <param name="fileName"> String containing the Name of the file.</param>
        /// <param name="commentParameter"> String containing the character(s) that will be used to _comment.</param>
        /// <returns>
        ///     True if it updated the file, or false if it did not.
        /// </returns>
        public static bool ProcessFile(string filePath, string copyrightTextFilePath, string commentParameter,
            string endCommentParameter, string fileName)
        {
            _comment = commentParameter;
            _endComment = endCommentParameter;

            var fileContent = new List<string>(File.ReadAllLines(filePath));
            var licenseText = new List<string>(File.ReadAllLines(copyrightTextFilePath));

            //This block of code adds the filename to the header text to be inserted
            var titleLine = "";
            for (var i = 0; i < ((licenseText[0].Length - fileName.Length)/2); i++)
            {
                titleLine = string.Concat(titleLine, @"=");
            }
            titleLine = string.Concat(titleLine, fileName);
            for (var i = 0;
                i < ((licenseText[0].Length - fileName.Length)/2) + ((licenseText[0].Length - fileName.Length)%2);
                i++)
            {
                titleLine = string.Concat(titleLine, @"=");
            }
            licenseText.Insert(0, titleLine);

            //This makes it so the files will only be updated if they need to be.
            if (NeedsUpdate(fileContent, licenseText))
            {
                RemoveExistingHeader(fileContent);
            }
            else
            {
                return false;
            }

            WriteFile(fileContent, licenseText, filePath);
            return true;
        }

        /// <summary>
        ///     Checks if the header matches the most recent copyright text.
        /// </summary>
        /// <param name="fileContent"> List containing the content of the file to be edited </param>
        /// <param name="licenseText"> List containing the content of the copyright file. </param>
        /// <return>
        ///     True if the header does not match the text file, false
        ///     if they are the same.
        /// </return>
        private static bool NeedsUpdate(IList<string> fileContent, IReadOnlyList<string> licenseText)
        {
            var needsUpdate = true;

            var commentEndIndex = 0;
            for (var i = 0; fileContent[i].StartsWith(_comment) && i < licenseText.Count; i++)
            {
                //if it is the same as the license text with the _comment, it will not need to be updated.
                if (fileContent[i].Equals(_comment + licenseText[i] + _endComment))
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

            var j = 1;
            try
            {
                do
                {
                    //if the header extends past the most recent copyright file, then it isn't correct.
                    if (fileContent[commentEndIndex + j].StartsWith(_comment))
                    {
                        return true;
                    }
                        //Could be an empty line, so check for that.
                    if (string.IsNullOrEmpty(fileContent[commentEndIndex + j]))
                    {
                        fileContent.RemoveAt(commentEndIndex + j);
                    }
                    ++j;
                } while (fileContent[commentEndIndex + j].StartsWith(_comment) ||
                         string.IsNullOrEmpty(fileContent[commentEndIndex + j]));
            }
            catch (Exception e)
            {
                //write the Log file with the error message.
                Log.Fatal("error in verifying the copyright!", e);
                return true;
            }
            return needsUpdate;
        }

        /// <summary>
        ///     inserts the copyright into the file then writes it.
        /// </summary>
        /// <param name="fileContent">Content of the file now without the old copyright.</param>
        /// <param name="licenseText"> Copyright text to insert.</param>
        /// <param name="filePath">Name of file to be written.</param>
        private static void WriteFile(List<string> fileContent, IReadOnlyList<string> licenseText, string filePath)
        {
            if (fileContent[0].StartsWith("<?") || fileContent[0].StartsWith("#!"))
            {
                for (var i = 0; i < licenseText.Count; i++)
                {
                    fileContent.Insert(i + 1, (_comment + licenseText[i] + _endComment));
                }
            }
            else
            {
                for (var i = 0; i < licenseText.Count; i++)
                {
                    fileContent.Insert(i, (_comment + licenseText[i] + _endComment));
                }
            }
            File.WriteAllLines(filePath, fileContent.ToArray());
        }

        /// <summary>
        ///     removes the existing header content of the file.
        /// </summary>
        /// <param name="fileContent"> List containing the content of the file to be edited</param>
        private static void RemoveExistingHeader(IList<string> fileContent)
        {
            //remove empty lines from the beginning of the file.
            for (var i = 0; i < fileContent.Count; i++)
            {
                while (fileContent.Count > 0 && string.IsNullOrEmpty(fileContent[0]))
                {
                    fileContent.RemoveAt(0);
                }
                if (fileContent[i].Trim().StartsWith("#!"))
                {
                    for (var j = i + 1; fileContent[j].Trim().StartsWith(_comment); )
                    {
                        fileContent.RemoveAt(j);
                    }
                }
                //removes the lines of the previous header.
                else if (fileContent[i].Trim().StartsWith(_comment))
                {
                    for (var j = i; fileContent[j].Trim().StartsWith(_comment);)
                    {
                        fileContent.RemoveAt(j);
                    }
                }
                    //finds the ending index of the _comment.
                else if (fileContent[i].Trim().StartsWith(_endComment) || fileContent[i].Trim().EndsWith(_endComment))
                {
                    fileContent.RemoveAt(i);
                    break;
                }
                else
                {
                    break;
                }
            }
        }
    }
}
