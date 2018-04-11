#!/usr/bin/python3

import os
import sys
import traceback
import shutil
import logging
# import threading
# from threading import Thread
import git
from git import Repo, Actor
import xml.etree.ElementTree as ET
import CopyrightService as Copyright_Service

CONFIG_FILE = "App.config"

NUMBER_OF_THREADS = 1
GIT_PATH = ""
REPOSITORY_FILE = ""
GIT_DIR = ""
GIT_PROTOCOL = ""
GIT_BRANCH_NAME = ""
GIT_MESSAGE = ""
AUTHOR_EMAIL = ""
AUTHOR_NAME = ""
LOG_FILE_PATH = ""
LOG_LEVEL = ""
root_dir = ""


def get_config_data():
    global GIT_BRANCH_NAME, GIT_PATH, GIT_DIR, GIT_PROTOCOL, REPOSITORY_FILE, root_dir, GIT_MESSAGE, AUTHOR_NAME, AUTHOR_EMAIL, LOG_LEVEL, LOG_FILE_PATH, NUMBER_OF_THREADS

    root_dir = os.getcwd()

    root = ET.parse(CONFIG_FILE)
    root = root.getroot()
    for child in root:
        if child.tag == 'gitpath':
            GIT_PATH = child.attrib['value']
        elif child.tag == 'gitdir':
            GIT_DIR = child.attrib['value']
        elif child.tag == 'gitprotocol':
            GIT_PROTOCOL = child.attrib['value']
        elif child.tag == 'gitbranchname':
            GIT_BRANCH_NAME = child.attrib['value']
        elif child.tag == 'repositoryfile':
            REPOSITORY_FILE = child.attrib['value']
        elif child.tag == 'author':
            AUTHOR_NAME = child.attrib['name']
            AUTHOR_EMAIL = child.attrib['email']
        elif child.tag == 'gitmessage':
            GIT_MESSAGE = child.attrib['value']
        elif child.tag == 'logger':
            for grand_child in child:
                if grand_child.tag == 'filepath':
                    LOG_FILE_PATH = grand_child.attrib['value']
                elif grand_child.tag == 'level':
                    LOG_LEVEL = grand_child.attrib['value']
            logging.basicConfig(filename=LOG_FILE_PATH, level=logging.DEBUG)
        elif child.tag == 'threads':
            NUMBER_OF_THREADS = int(child.attrib['num'])


def main():
    get_config_data()

    with open(REPOSITORY_FILE, 'r') as repository_file:
        repositories = repository_file.readlines()
        for repository in repositories:
            try:
                git_work(repository)

                # while(threading.active_count() > NUMBER_OF_THREADS):
                #     continue

                # thread = Thread(target=git_work, kwargs={"repository": repository})
                # thread.start()

            except Exception as ex:
                logging.exception(str(ex))


def git_work(repository):
    global GIT_DIR
    repository = repository.strip('\n')

    logging.debug("fetching repository %s" % repository)

    os.chdir(GIT_DIR)
    fields = repository.split(',')

    try:
        dir_name = os.path.join(GIT_DIR,  fields[0])
        os.mkdir(dir_name)
    except FileExistsError:
        pass  # dir already exists

    dir_name = os.path.join(dir_name, fields[2])
    try:
        os.mkdir(dir_name)
    except FileExistsError:
        # TODO: replace with a pull instead of clone.
        shutil.rmtree(dir_name, ignore_errors=True)
        os.mkdir(dir_name)

    logging.debug("cloning repo: %s" % repository)
    try:
        repo = Repo.clone_from(make_git_url(
            repository), dir_name, branch='master')
        assert repo.__class__ is Repo
        assert Repo.init(dir_name).__class__ is Repo
    except git.exc:
        logging.exception("Cloning repo %s failed" % dir_name)
        return

    if fields[3] is 's':
        pull_request_git_action(repo)
    else:
        force_git_action(repo, dir_name)


def pull_request_git_action(repo):
    pass


def force_git_action(repo, path):
    heads = repo.heads

    for head in heads:
        git = repo.git
        git.checkout(head.name)
        Copyright_Service.main(path, log_file_name=LOG_FILE_PATH)

        author = str(AUTHOR_NAME + "<" + AUTHOR_EMAIL + ">")
        git.commit(a=True, message=GIT_MESSAGE, author=author)
        git.push()


def make_git_url(repository):
    fields = repository.split(',')
    return str(GIT_PROTOCOL + '@' + fields[1] + ':' + fields[0] + '/' + fields[2])


main()
