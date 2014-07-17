//=========================Initialization.cs==========================//
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
    public class Initialization
    {
        private List<string> _filesToProcess;
        private readonly List<string> _comments = new List<string>();
        private readonly List<string> _endComments = new List<string>();
        private readonly List<string> _extensions = new List<string>();
        private readonly List<string> _directories = new List<string>();
        private readonly string _copyrightFilePath;
        private List<string> _fileNames;
        private readonly List<string> _failedFiles = new List<string>();
        private readonly List<string> _updatedFiles = new List<string>();
        private readonly List<string> _noNeedFiles = new List<string>();
        private int _extensionIndex;
        private static readonly log4net.ILog Log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        /// <summary>
        ///     This Constructor sets the local variables equeal to the required parameters.
        /// </summary>
        /// <param name="comments">A list of the comment characters for the file _extensions.</param>
        /// <param name="endComments">A list of the end comment characters for the file _extensions.</param>
        /// <param name="extensions">A list of the _extensions that will be affected by the service.</param>
        /// <param name="directories">A list of all _directories to search.</param>
        /// <param name="copyrightFilePath">The path to the copyright file.</param>
        public Initialization(List<string> comments, List<string> endComments, List<string> extensions,
            List<string> directories, string copyrightFilePath)
        {
            _comments = comments;
            _endComments = endComments;
            _extensions = extensions;
            _directories = directories;
            _copyrightFilePath = copyrightFilePath;
        }

        /// <summary>
        ///     Handles the running of the service in its iteratory state.
        /// </summary>
        public void mainHandler()
        {
            try
            {
                foreach (var directory in _directories)
                {
                    //Uses the config file defined _extensions to grab all files in the selected directory with the expected file _extensions.
                    GetFilesAndMakeChanges(directory.Trim());
                    _extensionIndex = 0;
                }
            }
            catch (Exception ex)
            {
                Log.Fatal("Invalid Directory!" + _directories, ex);
                //Log comment with Log4Net
            }
        }

        /// <summary>
        ///     This method takes the files, and determines the proper comment style to use
        ///     then processes it with the proper style of commenting and insertion.
        /// </summary>
        /// <param name="indexOfFile">Specifies which file will be processed.</param>
        /// <param name="fileName">Specifies the text to add to the file.</param>
        private void ProcessFiles(int indexOfFile, string fileName)
        {
            //used to figure out if the next style of commenting is needed or not.
            if (!_filesToProcess[indexOfFile].Equals("next"))
            {
                var filePath = _filesToProcess[indexOfFile].Trim();
                try
                {
                    //Here's where extension Index is crucial, if it is xml style, the commenting is completely different since endtags are required.
                    if (CommentInsertion.ProcessFile(filePath.Trim(), _copyrightFilePath.Trim(),
                        _comments[_extensionIndex].Trim(), _endComments[_extensionIndex].Trim(), fileName.Trim()))
                    {
                        //adds the name of the file successfully processed to the future Log file.
                        _updatedFiles.Add(filePath);
                    }
                    else
                    {
                        _noNeedFiles.Add(filePath);
                    }
                }
                catch (Exception ex)
                {
                    //adds the name of the file it failed to process to the Log file.
                    _failedFiles.Add(fileName);
                    Log.Fatal("File Failed to update!" + fileName, ex);
                }
            }
            else
            {
                //The extension has now switched to a new type, requiring a separate caller.
                ++_extensionIndex;
            }
        }

        /// <summary>
        ///     Retrieves the files from the directory and subdirectories and makes the changes
        ///     to them by selecting the Directory, then grabbing the files that need to be
        ///     processed, then calling the processing method to process them.
        /// </summary>
        /// <param name="directoryPath">String containing the path of the directory to modify.</param>
        private void GetFilesAndMakeChanges(string directoryPath)
        {
            Directory.SetCurrentDirectory(directoryPath);
            _filesToProcess = new List<string>();
            _fileNames = new List<string>();
            foreach (var extension in _extensions)
            {
                _filesToProcess.AddRange(Directory.GetFiles(directoryPath, extension.Trim(),
                    SearchOption.AllDirectories));
                //Specifies a new type of comment might be needed, and also that the _extensions will be changing
                _filesToProcess.Add("next");
            }

            foreach (var file in _filesToProcess)
            {
                try
                {
                    _fileNames.Add(Path.GetFileName(file));
                }
                catch (Exception e)
                {
                    _fileNames.Add("no File");
                    e.Data.Clear();
                }
            }

            //Gets the license text from the copyright text file.
            var licenseText = new List<string>();
            licenseText.AddRange(File.ReadAllLines(_copyrightFilePath));

            //for loop that processes all the files.
            for (var i = 0; i < _filesToProcess.Count; i++)
            {
                //Puts the copyright in the document by checking which comment type it is, then calling a class to process the file.
                ProcessFiles(i, _fileNames[i]);
            }
        }


        //download resharper, install trial
        /// <summary>
        ///     Writes the Log file so a user can see which files were updated and
        ///     which weren't.
        /// </summary>
        public void WriteLogFile()
        {
            var sizeoflog = _failedFiles.Count + _updatedFiles.Count + _noNeedFiles.Count + 3;
            var logFileContent = new string[sizeoflog];

            //add the failed file names into the Log array string
            logFileContent[0] = "Files failed to update:\n\n";
            Log.Fatal("Files failed to update:\n\n");
            foreach (var file in _failedFiles)
            {
                Log.Fatal(file);
            }

            //add the updated file names into the Log array string
            Log.Fatal("\n\n\nSuccessfully updated files: \n\n");
            foreach (var file in _updatedFiles)
            {
                Log.Fatal(file);
            }

            //add the unedited file names into the Log array string
            Log.Fatal("\n\n\nunedited files: \n\n");
            foreach (var file in _noNeedFiles)
            {
                Log.Fatal(file);
            }
        }
    }
}