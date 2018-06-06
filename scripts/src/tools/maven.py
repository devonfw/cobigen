import os
import sys
import subprocess

from _winapi import CREATE_NEW_CONSOLE
from asyncio.subprocess import PIPE
from lxml import etree
from typing import List, Tuple

from tools.config import Config
from tools.github import GitHub
from tools.logger import log_info, log_error


class Maven:
    '''
    Class providing maven operations
    '''

    def __init__(self, config: Config, github: GitHub) -> None:
        self.__config = config
        self.github_repo = github
        self.mavenNS = "{http://maven.apache.org/POM/4.0.0}"

    def __run_maven_and_handle_error(self, execpath: str, command: str) -> None:
        returncode = self.run_maven_process(execpath, command)
        if returncode == 1:
            log_error("Maven execution failed, please see create_release.py.log for logs located at current directory.")
            sys.exit()

    def set_version(self, version: str) -> List[str]:
        log_info("Setting version to " + version + " with maven/tycho")
        changed_files = list()

        # For dev_eclipseplugin branch
        if self.__config.branch_to_be_released == self.__config.branch_eclipseplugin:
            self.__run_maven_and_handle_error(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse"),
                                              "mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion="+version)
            changed_files.append(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse", "pom.xml"))
            changed_files.append(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse", "META-INF", "MANIFEST.MF"))
            self.__run_maven_and_handle_error(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse-test"),
                                              "mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion="+version)
            changed_files.append(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse-test", "pom.xml"))
            changed_files.append(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse-test", "META-INF", "MANIFEST.MF"))
            self.__run_maven_and_handle_error(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse-feature"),
                                              "mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion="+version)
            changed_files.append(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse-feature", "pom.xml"))
            changed_files.append(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse-feature", "feature.xml"))
            self.__run_maven_and_handle_error(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse-updatesite"),
                                              "mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion="+version)
            changed_files.append(os.path.join(self.__config.build_folder_abs, "cobigen-eclipse-updatesite", "pom.xml"))
        else:
            toplevel_pom_path = os.path.join(self.__config.build_folder_abs, "pom.xml")
            # For dev_mavenplugin branch
            if self.__config.branch_to_be_released == self.__config.branch_mavenplugin:
                pom = etree.parse(toplevel_pom_path)
                for mapping in pom.findall("//" + self.mavenNS + "properties"):
                    log_info("Processing " + toplevel_pom_path + " ...")
                    version_node = mapping.find(self.mavenNS + "cobigen.maven.version")
                    if self.__check_and_write_pom(pom, version_node, version, toplevel_pom_path):
                        changed_files.append(toplevel_pom_path)
            # For dev_core branch
            elif self.__config.branch_to_be_released == self.__config.branch_core:
                pom = etree.parse(toplevel_pom_path)
                for mapping in pom.findall("//" + self.mavenNS + "properties"):
                    log_info("Processing " + toplevel_pom_path + " ...")
                    version_node = mapping.find(self.mavenNS + "cobigencore.version")
                    if self.__check_and_write_pom(pom, version_node, version, toplevel_pom_path):
                        changed_files.append(toplevel_pom_path)
            # others
            else:
                for dirpath, dnames, fnames in os.walk(self.__config.build_folder_abs):
                    self.__ignore_folders_on_pom_search(dnames)
                    if "pom.xml" in fnames:
                        fpath = os.path.join(dirpath, "pom.xml")
                        log_info("Processing " + fpath + " ...")
                        with open(fpath) as file:
                            pom = etree.parse(file)
                            version_node = pom.find(self.mavenNS + "version")
                            if self.__check_and_write_pom(pom, version_node, version, fpath):
                                changed_files.append(fpath)
        return changed_files

    def __ignore_folders_on_pom_search(self, dnames):
        # do not traverse unnecessary stuff
        if ".settings" in dnames:
            dnames.remove(".settings")
        if "target" in dnames:
            dnames.remove("target")
        if "bin" in dnames:
            dnames.remove("bin")

    def __check_and_write_pom(self, pom, version_node, version: str, pom_path: str) -> bool:
        if version_node is not None and version_node.text != version and version_node.text != "dev-SNAPSHOT":
            version_node.text = version
            pom.write(pom_path)
            return True
        return False

    def upgrade_snapshot_dependencies(self) -> Tuple[str, List[str]]:
        log_info('Upgrading all SNAPSHOT dependencies in POM files in ' + self.__config.build_folder_abs)

        core_version_in_eclipse_pom = ""
        changed_files = list()
        for dirpath, dnames, fnames in os.walk(self.__config.build_folder_abs):
            self.__ignore_folders_on_pom_search(dnames)

            upgraded_deps = list()
            if "pom.xml" in fnames:
                fpath = os.path.join(dirpath, "pom.xml")
                log_info("Processing " + fpath + " ...")
                with open(fpath) as file:
                    pom = etree.parse(file)
                    for mapping in pom.findall("/"+self.mavenNS+"project/"+self.mavenNS+"dependencies/"+self.mavenNS+"dependency"):
                        version_node = mapping.find(self.mavenNS+"version")
                        artifact_id_node = mapping.find(self.mavenNS+"artifactId")
                        group_id_node = mapping.find(self.mavenNS+"groupId")
                        if (group_id_node.text == self.__config.groupid_cobigen or group_id_node.text == "${project.groupId}") and version_node.text.endswith("-SNAPSHOT"):
                            new_version = version_node.text.split("-")
                            log_info("Upgrading " + group_id_node.text + ":" + artifact_id_node.text + " to release version ("+new_version+") ...")
                            version_node.text = str(new_version[0])
                            upgraded_deps.append((artifact_id_node.text, version_node.text, new_version))
                            pom.write(fpath)
                            changed_files.append(fpath)
                            if artifact_id_node.text == self.__config.artifactid_core:
                                core_version_in_eclipse_pom = version_node.text
                                cobigen_core_milestone = self.github_repo.find_cobigen_core_milestone(core_version_in_eclipse_pom)
                                if cobigen_core_milestone["state"] != "closed":
                                    log_info("Core version " + core_version_in_eclipse_pom +
                                             " is not yet released. This should be released before releasing cobigen-eclipse")
                                    sys.exit()
                        else:
                            continue
            # do not iterate through other files besides the pom.xml
            fnames.clear()

        if len(upgraded_deps) > 0 and self.__config.branch_to_be_released == self.__config.branch_eclipseplugin:
            file_path = os.path.join(self.__config.build_folder_abs, "cobigen-eclipse/.classpath")
            log_info("Processing " + file_path + " ...")
            with open(file_path, 'w') as file:
                contents = file.read()
                for (artifact_id_node, old_version, new_version) in upgraded_deps:
                    contents = contents.replace(artifact_id_node + "-" + old_version, artifact_id_node + "-" + new_version)
                file.write(contents)
                changed_files.append(file_path)

            file_path = os.path.join(self.__config.build_folder_abs, "cobigen-eclipse/META-INF/MANIFEST.MF")
            log_info("Processing " + file_path + " ...")
            with open(os.path.join(self.__config.build_folder_abs, ), 'w') as file:
                contents = file.read()
                for (artifact_id_node, old_version, new_version) in upgraded_deps:
                    contents = contents.replace(artifact_id_node + "-" + old_version, artifact_id_node + "-" + new_version)
                file.write(contents)
                changed_files.append(file_path)

        return (core_version_in_eclipse_pom, changed_files)

    def run_maven_process(self, execpath: str, command: str) -> int:
        maven_process = subprocess.Popen(command.split(), shell=True, stdout=PIPE, stderr=PIPE,
                                         universal_newlines=True, bufsize=1, cwd=execpath, env=os.environ)

        for line in maven_process.stdout:
            log_info(line.strip())
        maven_process.stdout.close()
        for line in maven_process.stderr:
            log_error(line.strip())
        maven_process.stderr.close()
        return_code = maven_process.wait()

        return return_code
