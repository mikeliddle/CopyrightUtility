#!/usr/bin/python3

import xml.etree.ElementTree as ET
import os
import glob

CONFIG_FILE = "/home/mliddle2/workspace/copyrightutility/CopyrightAdditionService/CopyrightService/App.config"

_file_types = list()
_directories = list()
_copyrightFilePath = ""


class File_Type(object):
    def __init__(self, extension, beginComment, endComment):
        self.extension = extension
        self.beginComment = beginComment
        self.endComment = endComment


def get_files_and_make_changes(directory):
    os.chdir(directory)
    files_to_process = list()
    file_names = list()

    for file_type in _file_types:
        files_to_process += glob.glob(file_type.extension)
        files_to_process.append("next")

    for file_obj in files_to_process:
        file_names.append(file_obj)

    print(file_names)

    pass


def driver():
    for directory in _directories:
        get_files_and_make_changes(directory)
        _extensionIndex = 0


def main():
    tree = ET.parse(CONFIG_FILE)
    root = tree.getroot()

    for child in root:
        if child.tag == "copyrightfile":
            _copyrightFilePath = child.attrib['filepath']
        elif child.tag == "directorylist":
            for directory in child:
                _directories.append(directory.attrib['path'])
        elif child.tag == "filetypes":
            for extension in child:
                obj = extension.attrib
                _file_types.append(
                    File_Type(obj['file'], obj['comment'], obj['endComment']))

    driver()


main()
