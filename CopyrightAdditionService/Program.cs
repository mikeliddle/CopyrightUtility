//=============================Program.cs=============================//
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
using System.Linq;
using System.Xml.Linq;
[assembly: log4net.Config.XmlConfigurator(Watch = true)]

namespace DebugCopyrightAdditionService
{
    public static class Program
    {
        private static XDocument _document;
        private static List<string> _comments;
        private static List<string> _endComments;
        private static List<string> _extensions;
        private static List<string> _directories;
        private static string _copyrightFilePath;
        private static readonly log4net.ILog Log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        static void Main(string[] args)
        {
            ParseXmlDoc();
            var i = new Initialization(_comments, _endComments, _extensions, _directories, _copyrightFilePath);
            i.mainHandler();
            i.WriteLogFile();
        }

        /// <summary>
        /// parses the XML _document so the program may use the input.
        /// </summary>
        /// 
        static void ParseXmlDoc()
        {
            try
            {
                _document = XDocument.Load("App.config");
                _comments = _document.Descendants("extension").Select(attachedFileElement => attachedFileElement.Attribute("comment").Value).ToList();
                _endComments = _document.Descendants("extension").Select(attachedFileElement => attachedFileElement.Attribute("endComment").Value).ToList();
                _extensions = _document.Descendants("extension").Select(attachedFileElement => attachedFileElement.Attribute("file").Value).ToList();
                _directories = _document.Descendants("directory").Select(attachedFileElement => attachedFileElement.Attribute("path").Value).ToList();
                _copyrightFilePath = _document.Descendants("copyrightfile").Select(attachedFileElement => attachedFileElement.Attribute("filepath").Value).ToList()[0].Trim();
            }
            catch (Exception exc)
            {
                //write the Log file with the error message.
                Log.Fatal("Missing Config File or Required Field!", exc);
            }
        }
    }
}
