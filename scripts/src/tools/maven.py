from tools.config import Config
from tools import user_interface
from lxml import etree
import os
import sys
from tools.user_interface import print_info
from tools.github import GitHub


class Maven:
    '''
    Class providing maven operations
    '''

    def __init__(self, config: Config, github: GitHub) -> None:
        self.__config = config
        self.github_repo = github
        self.mavenNS = "{http://maven.apache.org/POM/4.0.0}"

    def add_snapshot_in_version(self, xml_version_node, pom, target_version: str, pom_path):
        '''This method is responsible for changing version number in pom.xml to next release version with SNAPSHOT'''
        user_interface.print_info('Set next release version (' + target_version + ') in ' + pom_path)

        target_snapshot_version = target_version + "-SNAPSHOT"
        xml_version_node.text = str(target_snapshot_version)
        pom.write(pom_path)

    def remove_snapshot_in_version(self, xml_version_node, pom, release_version: str, pom_path):
        '''This method is responsible for changing version number in pom.xml to release version'''
        user_interface.print_info('Set next release version (' + release_version + ') in ' + pom_path)

        xml_version_node.text = str(release_version)
        pom.write(pom_path)

    def call_add_remove_snapshot_method(self, xml_version_node, pom, bool_add_snapshot, version_to_change, file_path):
        if bool_add_snapshot:
            self.add_snapshot_in_version(xml_version_node, pom, version_to_change, file_path)
        else:
            self.remove_snapshot_in_version(xml_version_node, pom, version_to_change, file_path)

    # This method is responsible for adding SNAPSHOT version if not already added
    def add_remove_snapshot_version_in_pom(self, bool_add_snapshot, version_to_change: str):
        user_interface.print_info("Checking out branch for adding SNAPSHOT version: " + self.__config.branch_to_be_released + ".")

        pom = etree.parse("pom.xml")

        # For dev_mavenplugin branch
        if self.__config.branch_to_be_released == "dev_mavenplugin":
            for mapping in pom.findall("//" + self.mavenNS + "properties"):
                version_node = mapping.find(self.mavenNS + "cobigen.maven.version")
                if(version_node):
                    self.call_add_remove_snapshot_method(version_node, pom, bool_add_snapshot, version_to_change, None)

        # For dev_core branch
        elif self.__config.branch_to_be_released == "dev_core":
            for mapping in pom.findall("//" + self.mavenNS + "properties"):
                version_node = mapping.find(self.mavenNS + "cobigencore.version")
                if(version_node):
                    self.call_add_remove_snapshot_method(version_node, pom, bool_add_snapshot, version_to_change, None)
        # For dev_eclipseplugin branch
        elif self.__config.branch_to_be_released == "dev_eclipseplugin":
            for dname, dirs, files in os.walk("."):
                for fname in files:
                    fpath = os.path.join(dname, fname)
                    if "pom.xml" in fname and "."+os.sep+"cobigen" in fpath:
                        with open(fpath) as file:
                            pom = etree.parse(file)
                            version_node = pom.find(self.mavenNS + "version")
                            self.call_add_remove_snapshot_method(version_node, pom, bool_add_snapshot, version_to_change, fpath)
                    else:
                        continue

        # For dev_htmlmerger, dev_jssenchaplugin branch
        else:
            version_node = pom.find(self.mavenNS + "version")
            self.call_add_remove_snapshot_method(version_node, pom, bool_add_snapshot, version_to_change, None)

        user_interface.print_info("Current working directory changed to: "+os.getcwd())

    def upgrade_snapshot_dependencies(self) -> str:
        print_info('Upgrading all SNAPSHOT dependencies in POM files.')
        os.chdir(os.path.join(self.__config.root_path, self.__config.build_folder))
        for dname, dirs, files in os.walk("."):
            for fname in files:
                fpath = os.path.join(dname, fname)
                if "pom.xml" in fname:
                    with open(fpath) as file:
                        pom = etree.parse(file)
                        for mapping in pom.findall("//"+self.mavenNS+"dependency"):
                            name = mapping.find(self.mavenNS+"version")
                            artifactId = pom.find(self.mavenNS+"artifactId")
                            try:
                                if "-SNAPSHOT" in name.text:
                                    new_version = name.text.split("-")
                                    name.text = str(new_version[0])
                                    pom.write(fpath)
                                    if artifactId == "cobigen-core":
                                        core_version_in_eclipse_pom = name.text
                                        cobigen_core_milestone = self.github_repo.find_cobigen_core_milestone(core_version_in_eclipse_pom)
                                        if cobigen_core_milestone["state"] != "closed":
                                            print_info("Core version " + core_version_in_eclipse_pom +
                                                       " is not yet released. This should be released before releasing cobigen-eclipse")
                                            sys.exit()
                                else:
                                    continue
                            except:
                                continue
        return core_version_in_eclipse_pom
