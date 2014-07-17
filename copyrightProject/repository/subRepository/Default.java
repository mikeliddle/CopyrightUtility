//============================Default.java============================//
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
package com.mycila.maven.plugin.license;
/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-08-01
 */
public final class Default {

    public static final String[] INCLUDE = new String[]{"**"};

    public static final String[] EXCLUDES = {
        // Miscellaneous typical temporary files
        "**/*~",
        "**/#*#",
        "**/.#*",
        "**/%*%",
        "**/._*",
        "**/.repository/**",

        // CVS
        "**/CVS",
        "**/CVS/**",
        "**/.cvsignore",

        // RCS
        "**/RCS",
        "**/RCS/**",

        // SCCS
        "**/SCCS",
        "**/SCCS/**",

        // Visual SourceSafe
        "**/vssver.scc",

        // Subversion
        "**/.svn",
        "**/.svn/**",

        // Arch
        "**/.arch-ids",
        "**/.arch-ids/**",

        //Bazaar
        "**/.bzr",
        "**/.bzr/**",

        //SurroundSCM
        "**/.MySCMServerInfo",

        // Mac
        "**/.DS_Store",

        // Serena Dimensions Version 10
        "**/.metadata",
        "**/.metadata/**",

        // Mercurial
        "**/.hg",
        "**/.hg/**",
        "**/.hgignore",

        // git
        "**/.git",
        "**/.git/**",
        "**/.gitignore",
        "**/.gitmodules",

        // BitKeeper
        "**/BitKeeper",
        "**/BitKeeper/**",
        "**/ChangeSet",
        "**/ChangeSet/**",

        // darcs
        "**/_darcs",
        "**/_darcs/**",
        "**/.darcsrepo",
        "**/.darcsrepo/**",
        "**/-darcs-backup*",
        "**/.darcs-temp-mail",

        // maven project's temporary files
        "**/target/**",
        "**/test-output/**",
        "**/release.properties",
        "**/dependency-reduced-pom.xml",
        "**/pom.xml.releaseBackup",

        // code coverage tools
        "**/cobertura.ser",
        "**/.clover/**",

        // eclipse project files
        "**/.classpath",
        "**/.project",
        "**/.settings/**",

        // IDEA projet files
        "**/*.iml",
        "**/*.ipr",
        "**/*.iws",
        ".idea/**",

        // descriptors
        "**/MANIFEST.MF",

        // binary files - images
        "**/*.jpg",
        "**/*.png",
        "**/*.gif",
        "**/*.ico",
        "**/*.bmp",
        "**/*.tiff",
        "**/*.tif",
        "**/*.cr2",
        "**/*.xcf",

        // binary files - programs
        "**/*.class",
        "**/*.exe",
        "**/*.dll",
        "**/*.so",

        // checksum files
        "**/*.md5",
        "**/*.sha1",

        // binary files - archives
        "**/*.jar",
        "**/*.zip",
        "**/*.rar",
        "**/*.tar",
        "**/*.tar.gz",
        "**/*.tar.bz2",
        "**/*.gz",

        // binary files - documents
        "**/*.xls",

        // ServiceLoader files
        "**/META-INF/services/**",

        // Markdown files
        "**/*.md",

        // Office documents
        "**/*.xls",
        "**/*.doc",
        "**/*.odt",
        "**/*.ods",
        "**/*.pdf",

        // Travis
        "**/.travis.yml",

        // flash
        "**/*.swf",

        // json files
        "**/*.json"
    };

}
