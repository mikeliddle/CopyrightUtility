//========================Initialization.cs========================//
//  Author:    Connor Group Software Development team 2014            //
//  Copyright: Copyright (C) 2014 Connor Group. All rights reserved.  //
//  Email:     itconnorgp.com                                        //
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
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
//import org.apache.commons.io.FilenameUtils;

public class Initialization
{
    private List<String> filesToProcess;
    private List<String> comments;
    private List<String> endComments;
    private List<String> extensions;
    private List<String> directories;
    private String copyrightFilePath;
    private String logPath;
    private List<String> fileNames;
    private List<String> failedFiles;
    private List<String> updatedFiles;
    private List<String> noNeedFiles;
    private List<String> configFile;


    /********************************Initialization********************************
     * This Constructor sets the local variables equeal to the required @parameters.
     * @param List<String> comments
     *      A list of the comment characters for the file extensions
     * @param List<String> extensions
     *      A list of the extensions that will be affected by the service
     * @param List<String> directories
     *      A list of all directories to search.
     * @param String copyrightTextFileName
     *      The path to the copyright file.
     **/
    public Initialization()
    {
        try
        {
            /**  Let's hope  this works with linux. :)
             * 
             **/
            //configFile.AddRange(("app.txt"));

            int copyrightIndex = -1;
            int logIndex = -1;
            int directoryBeginIndex = -1;
            int directoryEndIndex = -1;
            int extensionBeginIndex = -1;
            int extensionEndIndex = -1;
                copyrightIndex = configFile.indexOf("copyrightPathBegin") + 1;
                logIndex = configFile.indexOf("logPathBegin") + 1;
                directoryBeginIndex = configFile.indexOf("DirectoryBegin");
                directoryEndIndex = configFile.indexOf("DirectoryEnd");
                extensionBeginIndex = configFile.indexOf("ExtensionBegin");
                extensionEndIndex = configFile.indexOf("ExtensionEnd");

            ParseConfigFile(copyrightIndex, logIndex, directoryBeginIndex, directoryEndIndex, extensionBeginIndex, extensionEndIndex);

            /**  Secondary Method, but also not supported by linux
            XmlDocument configDoc = new XmlDocument();
            configDoc.Load("app.config");

            XmlNodeList copyrightPath = configDoc.GetElementsByTagName("copyrightfile");
            XmlNodeList logFilePath = configDoc.GetElementsByTagName("log");
            XmlNodeList directoryList = configDoc.GetElementsByTagName("directory");
            XmlNodeList semiParsedExtensions = configDoc.GetElementsByTagName("extension");

            copyrightFilePath = copyrightPath[0].Attributes[0].Value.ToString();
            logPath = logFilePath[0].Attributes[0].Value.ToString();

            foreach (XmlNode directory in directoryList)
            {
                directories.Add(directory.Attributes[0].Value.ToString());
            }

            foreach (XmlNode extension in semiParsedExtensions)
            {
                comments.Add(extension.Attributes[0].Value.ToString());
                endComments.Add(extension.Attributes[1].Value.ToString());
                extensions.Add(extension.Attributes[2].Value.ToString());
            }
             **/

            /**  Preferred windows method, but must use other method for Linux
            XDocument document = XDocument.Load("app.config");
            LogPath = document.Descendants("log").Select(attachedFileElement => attachedFileElement.Attribute("filepath").Value).ToList();
            comments = document.Descendants("extension").Select(attachedFileElement => attachedFileElement.Attribute("comment").Value).ToList();
            endComments = document.Descendants("extension").Select(attachedFileElement => attachedFileElement.Attribute("endComment").Value).ToList();
            extensions = document.Descendants("extension").Select(attachedFileElement => attachedFileElement.Attribute("file").Value).ToList();
            directories = document.Descendants("directory").Select(attachedFileElement => attachedFileElement.Attribute("path").Value).ToList();
            copyrightTextFilePath = document.Descendants("copyrightfile").Select(attachedFileElement => attachedFileElement.Attribute("filepath").Value).ToList();
             **/
        }
        catch (Exception exc)
        {
            //write the log file with the error message.
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(logPath)));
				out.write("INVALID CONFIGURATION FILE!");
	            out.close();
			} catch (IOException e) {	}
            return;
        }
        try
        {
            for (int directoryIndex = 0; directoryIndex < directories.size(); directoryIndex++)
            {
                //Uses the config file defined extensions to grab all files in the selected directory with the expected file extensions.
                getFilesAndMakeChanges(directories.get(directoryIndex));
                extensionIndex = 0;
            }
        } catch (Exception ex)
        {
            return;
        }
        writeLogFile();
    }

    private void ParseConfigFile(int copyrightIndex, int logIndex, int directoryBeginIndex, int directoryEndIndex, int extensionBeginIndex, int extensionEndIndex)
    {
        String temp = configFile.get(copyrightIndex);
        int beginValue = temp.indexOf("=") + 1;
        copyrightFilePath = temp.substring(beginValue);

        temp = configFile.get(logIndex);
        beginValue = temp.indexOf("=") + 1;
        logPath = temp.substring(beginValue);

        for (int i = directoryBeginIndex + 1; i < directoryEndIndex; i++)
        {
            temp = configFile.get(i);
            directories.add(temp.substring(temp.indexOf("=") + 1));
        }

        for (int i = extensionBeginIndex + 1; i < extensionEndIndex; i++)
        {
            try
            {
                temp = configFile.get(i);
                temp = temp.substring(temp.indexOf("=") + 1);
                comments.add(temp.substring(0, temp.indexOf(" ") + 1));

                temp = temp.substring(temp.indexOf("=") + 1);
                endComments.add(temp.substring(0, temp.indexOf(" ") + 1));

                temp = temp.substring(temp.indexOf("=") + 1);
                extensions.add(temp);

            } catch (Exception e)
            { /*do nothing*/ }
        }
        
    }

    /********************************Process Files********************************
     * This method takes the files, and determines the proper comment style to use
     * then processes it with the proper style of commenting and insertion.
     * 
     * @param int extensionIndex
     *      Specifies which extension will be used for processing.
     * @param int indexOfFile
     *      Specifies which file will be processed.
     **/
    private void processFiles(int indexOfFile, String fileName)
    {
        String filePath;
        //used to figure out if the next style of commenting is needed or not.
        if (!filesToProcess.get(indexOfFile).equals("next"))
        {
            filePath = filesToProcess.get(indexOfFile);
            try
            {
                //Here's where extension Index is crucial, if it is xml style, the commenting is completely different since endtags are required.
                if(CommentInsertion.ProcessFile(filePath, copyrightFilePath, comments.get(extensionIndex), endComments.get(extensionIndex), fileName))
                {
                    //adds the name of the file successfully processed to the future log file.
                    updatedFiles.add(filePath);
                }
                else
                {
                    noNeedFiles.add(filePath);
                }                
            }
            catch (Exception ex)
            {
                //adds the name of the file it failed to process to the log file.
                failedFiles.add(fileName);
                return;
            }
        }
        else
        {
            //The extension has now switched to a new type, requiring a separate caller.
            ++extensionIndex;
        }

    }

    private int extensionIndex = 0;

    /********************************getFilesAndMakeChanges********************************
     * Retrieves the files from the directory and subdirectories and makes the changes 
     * to them by selecting the Directory, then grabbing the files that need to be
     * processed, then calling the processing method to process them.
     **/
    public void getFilesAndMakeChanges(String directoryPath)
    {
        for (int i = 0; i < extensions.size(); i++)
        {
        	File folder = new File(directoryPath);
        	File[] listOfFiles = folder.listFiles();

        	for (File file : listOfFiles) {
        	    if (file.isFile()) {
        	    	filesToProcess.add(file.getAbsolutePath());
        	    }
        	}
        	
            //filesToProcess.AddRange(Directory.GetFiles(directoryPath, extensions.get(i), SearchOption.AllDirectories));
            //Specifies a new type of comment might be needed, and also that the extensions will be changing
            filesToProcess.add("next");
        }

        for (int j = 0; j < filesToProcess.size(); j++)
        {
            try
            {
                fileNames.add(Paths.get(filesToProcess.get(j)).getFileName().toString());
            }
            catch (Exception e)
            {
                fileNames.add("no File");              
            }
        }

        //Gets the license text from the copyright text file.
        List<String> licenseText;
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(copyrightFilePath));
			licenseText.add(reader.readLine());
		} catch (Exception e) {	}
        
        /*******************************************************************************************
         * Verify this will work, make sure to get full file content!
         *******************************************************************************************/
        
        //for loop that processes all the files.
        for (int i = 0; i < filesToProcess.size(); i++)
        {
            //Puts the copyright in the document by checking which comment type it is, then calling a class to process the file.
            processFiles(i, fileNames.get(i));
        }

    }

    /********************************WriteLogFile********************************
     * Writes the log file so a user can see which files were updated and 
     * which weren't.
     **/
    public void writeLogFile()
    {
        int sizeoflog = failedFiles.size() + updatedFiles.size() + noNeedFiles.size() + 3;
        String[] log = new String[sizeoflog];

        //add the failed file names into the log array String
        log[0] = "Files failed to update:\n\n";
        for (int i = 0; i < failedFiles.size(); i++)
        {
            log[i + 1] = failedFiles.get(i);
        }

        //add the updated file names into the log array String
        log[failedFiles.size() + 1] = "\n\n\nSuccessfully updated files: \n\n";
        for (int i = 0; i < updatedFiles.size(); i++)
        {
            log[i + failedFiles.size() + 2] = updatedFiles.get(i);
        }

        //add the unedited file names into the log array String
        log[failedFiles.size() + updatedFiles.size() + 2] = "\n\n\nunedited files: \n\n";
        for (int i = 0; i < noNeedFiles.size(); i++)
        {
            log[i + failedFiles.size() + updatedFiles.size() + 3] = noNeedFiles.get(i);
        }

        //write the log document.
        
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(logPath));
            for (int i = 0; i < log.length; i++)
            {
            	out.write(log[i]);
            	out.newLine();
            	// code above replaces this line: File.WriteAllLines(logPath, log);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();        
                out.write( dateFormat.format(date));
            }
            out.close();
        }
        catch (IOException e)
        {
            System.out.println("Exception ");       
        }
        
        

    }
}
