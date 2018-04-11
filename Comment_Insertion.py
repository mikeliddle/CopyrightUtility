#!/usr/bin/python3
import ntpath
import logging


class Comment_Insertion(object):
    def __init__(self, copyright_text, file_type, file_path):
        logging.debug("starting comment insertion")
        self.copyright_text = copyright_text
        self.file_type = file_type
        self.file_path = file_path
        self.file_name = ntpath.basename(file_path)

    def process_file(self):
        logging.debug("starting process file: %s", self.file_name)

        file_content = ""
        with open(self.file_path, 'r') as file:
            file_content = file.readlines()

        title_line = self.make_title()
        license_text = list(self.copyright_text)
        license_text.insert(0, title_line)

        if self.needs_update(file_content, license_text):
            self.remove_existing_header(file_content)
        else:
            logging.debug("file was not changed")
            return False

        self.write_file(file_content, license_text)
        logging.debug("file was changed")
        return True

    def make_title(self):
        logging.debug("making title line")

        title_line = ""
        midway = (len(self.copyright_text[0]) - len(self.file_name)) // 2

        for i in range(0, midway):
            title_line += '='

        title_line += self.file_name

        for i in range(midway + 1, len(self.copyright_text[0]) - len(self.file_name)):
            title_line += '='

        title_line += '\n'

        return title_line

    def needs_update(self, file_content, license_text):
        _needs_update = True
        comment = self.file_type.beginComment
        end_comment = self.file_type.endComment

        comment_end_index = 0
        for i in range(0, len(license_text)):
            if file_content[i].startswith(comment):
                if file_content[i] is comment + license_text[i].strip('\n') + end_comment + '\n':
                    _needs_update = False
                else:
                    logging.debug("header needs update")
                    return True
                comment_end_index = i
            else:
                break

        j = 1
        while file_content[comment_end_index + j].startswith(comment) or file_content[comment_end_index + j] is "":
            if file_content[comment_end_index + j].startswith(comment):
                logging.debug("header needs update")
                return True
            if file_content[comment_end_index + j] is "":
                file_content.pop(comment_end_index + j)
        if _needs_update:
            logging.debug("header needs update")
        else:
            logging.debug("header does not need to be updated")

        return _needs_update

    def remove_existing_header(self, file_content):
        logging.debug("removing existing header")

        for i in range(0, len(file_content)):
            while len(file_content) > 0 and file_content[0] is "":
                file_content.pop(0)
            if i >= len(file_content):
                break
            if file_content[i].startswith("#!"):
                j = i + 1
                while file_content[j].startswith(self.file_type.beginComment):
                    file_content.pop(j)
            elif file_content[i].startswith(self.file_type.beginComment):
                j = i
                while file_content[j].startswith(self.file_type.beginComment):
                    file_content.pop(j)
            elif file_content[i].startswith(self.file_type.endComment) or file_content[i].endswith(self.file_type.endComment):
                file_content.pop(i)
                break
            else:
                break

    def write_file(self, file_content, license_text):
        logging.debug("writing out file")

        comment = self.file_type.beginComment
        end_comment = self.file_type.endComment

        if file_content[0].startswith("<?") or file_content[0].startswith("#!"):
            for i in range(0, len(license_text)):
                file_content.insert(
                    i + 1, comment + license_text[i].strip('\n') + end_comment + '\n')
        else:
            for i in range(0, len(license_text)):
                file_content.insert(
                    i, comment + license_text[i].strip('\n') + end_comment + '\n')

        with open(self.file_path, 'w') as source_file:
            source_file.writelines(file_content)
