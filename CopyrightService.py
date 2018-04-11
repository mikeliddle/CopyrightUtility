#!/usr/bin/python3

import xml.etree.ElementTree as ET
import os
import glob
import logging
# import threading
from Comment_Insertion import Comment_Insertion

ROOT_DIR = "/home/mliddle2/workspace/copyrightutility/"
CONFIG_FILE = ROOT_DIR + "App.config"

file_types = list()
directories = list()
files_to_process = list()
file_names = list()
copyright_file_path = ""
extension_index = 0
license_text = list()
updated_files = list()
no_need_files = list()


class File_Type(object):
    def __init__(self, extension, beginComment, endComment):
        self.extension = extension
        self.beginComment = beginComment
        self.endComment = endComment


def process_file(index_of_file, file_names):
    global extension_index

    if files_to_process[index_of_file] is "next":
        extension_index += 1
        logging.debug("moving to next file type")
    else:
        file_path = files_to_process[index_of_file]

        logging.info("processing file %s" % file_path)

        insertion_obj = Comment_Insertion(
            license_text, file_types[extension_index], file_path)

        if insertion_obj.process_file():
            updated_files.append(file_path)
        else:
            no_need_files.append(file_path)


def get_files_and_make_changes(directory):
    global files_to_process, file_names

    os.chdir(directory)
    logging.debug(
        "getting all files to process from directory: %s" % directory)

    for file_type in file_types:
        files_to_process += glob.iglob(str("**/" +
                                           file_type.extension), recursive=True)
        files_to_process.append("next")

    for i in range(0, len(files_to_process)):
        process_file(i, files_to_process[i])


def driver():
    global extension_index

    for directory in directories:
        get_files_and_make_changes(directory)
        extension_index = 0


def main(_directory, log_file_name="log.txt"):
    global copyright_file_path, file_types, directories, license_text, files_to_process, file_names, extension_index, updated_files, no_need_files

    logging.basicConfig(filename=log_file_name, level=logging.DEBUG)

    logging.debug("started copyright utility")

    file_types = list()
    directories = list()
    files_to_process = list()
    file_names = list()
    copyright_file_path = ""
    extension_index = 0
    license_text = list()
    updated_files = list()
    no_need_files = list()

    directories.append(_directory)

    tree = ET.parse(CONFIG_FILE)
    root = tree.getroot()
    try:
        for child in root:
            if child.tag == "copyrightfile":
                copyright_file_path = child.attrib['filepath']

                try:
                    with open(copyright_file_path, 'r') as copyright_file:
                        license_text = copyright_file.readlines()
                except Exception as ex:
                    logging.exception(str(ex))
                    raise ex

                logging.debug("copyright file: %s" % copyright_file_path)

            elif child.tag == "filetypes":
                for extension in child:
                    obj = extension.attrib
                    file_types.append(
                        File_Type(obj['file'], obj['comment'], obj['endComment']))

                logging.debug("retreived file extension information.")
    except Exception as ex:
        logging.exception("Configuration Error!")

    driver()
