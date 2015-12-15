'''
Created on Sep 29, 2015
@author: Xbalien
'''

import os
import struct
import zipfile
import StringIO
import hashlib
from permissions import DVM_PERMISSIONS
from optparse import OptionParser
from xml.dom import minidom
from apk import AXMLPrinter
import sys 
reload(sys) 
sys.setdefaultencoding('gb18030')


URI_FILE = r'.content_uri'
EXPORTED_FILE = r'.exposed_cp'
DIR = r"sampleapk/"
RES = r".am_result"
DEX_STRING_TABLE_OFFSET  = 0x38

class APKParser(object):

    def __init__(self, file_name):

        self.file_name = file_name
        fd = open(self.file_name, "rb")
        self.__raw = fd.read()
        self.zip = zipfile.ZipFile(StringIO.StringIO(self.__raw))
        fd.close()

    def parse_apk_info(self):
        self.name = os.path.basename(self.file_name)
        self.size = os.path.getsize(self.file_name)
        self.md5 = self.calc_md5(self.file_name)
        self.sha1 = self.calc_sha1(self.file_name)
        self.sha256 = self.calc_sha256(self.file_name)

    def calc_sha1(self, file_name):
        with open(file_name,'rb') as file_object:
            sha1obj = hashlib.sha1()
            sha1obj.update(file_object.read())
            hash_value = sha1obj.hexdigest()
            return hash_value
 
    def calc_md5(self, file_name):
        with open(file_name,'rb') as file_object:
            md5obj = hashlib.md5()
            md5obj.update(file_object.read())
            hash_value = md5obj.hexdigest()
            return hash_value

    def calc_sha256(self, file_name):
        with open(file_name,'rb') as file_object:
            sha1obj = hashlib.sha256()
            sha1obj.update(file_object.read())
            hash_value = sha1obj.hexdigest()
            return hash_value

    def get_name(self):
        return self.name

    def get_size(self):
        return self.size

    def get_md5(self):
        return self.md5

    def get_sha1(self):
        return self.sha1

    def get_sha256(self):
        return self.sha256



class ManifestParser(APKParser):
    '''
        This class is parse AndroidManifest.xml
    
        :param filename: specify the apk file name
        :type filename: string
        :Example ManifestParser(file_name)
    
    '''
    def __init__(self, file_name):
        
        APKParser.__init__(self, file_name)
        self.axml = {}
        self.xml = {}


        self.package = ''
        self.allow_backup = ''
        self.debuggable = ''
        self.androidversion = {}
        self.permissions = []
        self.parse_res = {}


        self.__comp_info = {}
        self.__components = []
        self.__providers = {}
        self.__activitys = {}
        self.__receivers = {}
        self.__services = {}
        self.__exported = {'activity':[], 'service':[], 'provider':[], 'receiver':[]}
        self.__exported_detail = {'activity':[], 'service':[], 'provider':[], 'receiver':[]}
        

        for i in self.zip.namelist():
            if i == 'AndroidManifest.xml':
                self.axml[i] = AXMLPrinter(self.zip.read(i))
                try:
                    self.xml[i] = minidom.parseString(self.axml[i].get_buff())
                except:
                    self.xml[i] = None

                if self.xml[i] != None:
                    self.package = self.xml[i].documentElement.getAttribute('package')
                    self.androidversion['Code'] = self.xml[i].documentElement.getAttribute('android:versionCode')
                    self.androidversion['Name'] = self.xml[i].documentElement.getAttribute('android:versionName')
                    self.share_user_id = self.xml[i].documentElement.getAttribute('android:sharedUserId')


                    for item in self.xml[i].getElementsByTagName('uses-permission'):
                        self.permissions.append(str(item.getAttribute('android:name')))

                    self.valid_apk = True


    def analyzer_manifest(self):
        "Parse AndroidManifest.xml all component"               
        self.analyzer_component('activity')
        self.analyzer_component('service')
        self.analyzer_component('provider')
        self.analyzer_component('receiver')
    
        self.__components = self.__activitys.keys()
        self.__components.extend(self.__services.keys())
        self.__components.extend(self.__providers.keys())
        self.__components.extend(self.__receivers.keys())

        self.__format_result()


    def __implicit_exported(self, ilist):
        "implicit_exported test"
        for item in ilist:
            alist = item.getElementsByTagName('action')
            if alist:
                return True
        return False

    def __ifilter(self, ilist):
        "Parse Intent-filter detail"
        ifilter_infos = {}
        for item in ilist:
            action_list = []
            category_list = []
            data_list = []      
            priority = item.getAttribute('android:priority')
            if not priority:
                priority = 0
            
            alist = item.getElementsByTagName('action')
            for a in alist:
                action_list.append(a.getAttribute('android:name'))
            
            clist = item.getElementsByTagName('category')
            for a in clist:
                category_list.append(a.getAttribute('android:name'))
    
            dlist = item.getElementsByTagName('data')
            if dlist:
                for a in dlist:
                    key_list = a.attributes.keys()
                    value_list = []
                    for key in key_list:
                        value_list.append(a.getAttribute(key))
                    for num in range(len(key_list)):
                        dic = []
                        dic.append(key_list[num])
                        dic.append(value_list[num])
                        data_list.append(dic)
            
            ifilter_infos['priority'] = priority
            ifilter_infos['action'] = action_list
            ifilter_infos['category'] = category_list
            ifilter_infos['data'] = data_list

        return ifilter_infos   
    
    def analyzer_component(self, component): 
        '''
            Parse component from AndroidManifest.xml
            
            :param component: specify the type of component to parse
            :type component: string
            :Example analyzer_component('activity')
            
        '''
        
        iflist = []
        c_list = self.xml['AndroidManifest.xml'].getElementsByTagName(component)
        for item in c_list:
            name = item.getAttribute('android:name')
            if name.startswith('.'):
                name = self.package + name
            exported = item.getAttribute('android:exported')
            if not exported:

                son = item.getElementsByTagName('intent-filter')
                #if has intent-filter action
                if self.__implicit_exported(son):
                    exported = 'true'
                else:
                    exported = 'false'
                    
            permission = item.getAttribute('android:permission')
            if not permission:
                application = item.parentNode.getAttribute('android:permission')
                if application:
                    permission = application
                else:
                    permission = 'none'
            #if permission != 'none':
            #    exported = 'false'

            iflist = item.getElementsByTagName('intent-filter')
            if iflist:
                intentfilter = self.__ifilter(iflist)
            else:
                intentfilter = []

            attributes = {}
            attributes['exported'] = exported
            attributes['permission'] = permission
            attributes['intent-filter'] = intentfilter
            
            if component == 'activity':
                exclude_recent = item.getAttribute('android:excludeFromRecents')
                if not exclude_recent:
                    exclude_recent = 'false'
                    attributes['excludeFromRecents'] = exclude_recent
                self.__activitys[name] = attributes
                
            elif component == 'service':
                self.__services[name] = attributes
                
            elif component == 'provider':

                attributes['authorities'] = item.getAttribute('android:authorities')
                if attributes['authorities']:
                    attributes['exported'] = 'true'
                attributes['grantUriPermissions'] = item.getAttribute('android:grantUriPermissions')
                attributes['readPermission'] = item.getAttribute('android:readPermission')
                attributes['writePermission'] = item.getAttribute('android:writePermission')
                if item.getAttribute('android:exported') == None:
                    if attributes['grantUriPermissions'] != None | attributes['readPermission'] != None | attributes['writePermission'] != None:
                        attributes['exported'] = 'false'
                    else :
                        attributes['exported'] = 'true'
                else :
                    attributes['exported'] = item.getAttribute('android:exported')
                self.__providers[name] = attributes
                
            elif component == 'receiver':
                self.__receivers[name] = attributes

    def attacksurface(self):

        self.parse_apk_info()

        self.allow_backup = self.__get_element('application', 'android:allowBackup')
        self.debuggable = self.__get_element('application', 'android:debuggable')
        self.min_sdk = self.__get_element('uses-sdk', 'android:minSdkVersion')
        self.target_sdk = self.__get_element('uses-sdk', 'android:targetSdkVersion')

        for component in self.__activitys:
            if self.__activitys[component]['exported'] == 'true':
                self.__exported['activity'].append(component)
                self.__exported_detail['activity'].append({component : self.__activitys[component]})

        for component in self.__services:
            if self.__services[component]['exported'] == 'true':
                self.__exported['service'].append(component)
                self.__exported_detail['service'].append({component : self.__services[component]})

        for component in self.__receivers:
            if self.__receivers[component]['exported'] == 'true':
                self.__exported['receiver'].append(component)
                self.__exported_detail['receiver'].append({component : self.__receivers[component]})

        for component in self.__providers:
            if self.__providers[component]['exported'] == 'true':
                self.__exported['provider'].append(component)
                self.__exported_detail['provider'].append({component : self.__providers[component]})

        
    def __format_result(self):
        "Format the all components parse result"
        self.__comp_info['activity'] = self.__activitys
        self.__comp_info['service'] = self.__services
        self.__comp_info['provider'] = self.__providers
        self.__comp_info['receiver'] = self.__receivers
        self.parse_res[self.package] = self.__comp_info


    def __get_element(self, tag_name, attribute):
        """
            Return element in xml files which match with the tag name and the specific attribute

            :param tag_name: specify the tag name
            :type tag_name: string
            :param attribute: specify the attribute
            :type attribute: string

            :rtype: string
        """
        for item in self.xml['AndroidManifest.xml'].getElementsByTagName(tag_name) :
            value = item.getAttribute(attribute)

            if len(value) > 0 :
                return value
        return None

    def get_package_name(self):
        """
            Return the name of the package
            :rtype: string
        """
        return self.package

    def get_androidversion_code(self):
        """
            Return the android version code
            :rtype: string
        """
        return self.androidversion["Code"]

    def get_androidversion_name(self):
        """
            Return the android version name
            :rtype: string
        """
        return self.androidversion["Name"]

    def get_share_user_id(self):
        if self.share_user_id:
            return self.share_user_id
        else:
            return "None"

    def get_min_sdk(self):
        return self.min_sdk

    def get_target_sdk(self):
        if self.target_sdk == None:
            self.target_sdk = self.min_sdk
        return self.target_sdk

    def get_allow_backup(self):
        if self.allow_backup == None:
            return 'false'
        else:
            return self.allow_backup

    def get_debuggable(self):
        if self.debuggable == None:
            return 'false'
        else:
            return self.debuggable

    def get_AndroidManifest(self):
        """
            Return the Android Manifest XML file
            :rtype: xml object
        """
        return self.xml["AndroidManifest.xml"]
    
    def get_permissions(self):
        '''
            Return this APK declaration permission
            :rtype a dictionnary
        '''
        return self.permissions

    def get_details_permissions(self) :
        """
            Return permissions with details

            :rtype: list of string
        """
        l = {}

        for i in self.permissions :
            perm = i
            pos = i.rfind(".")

            if pos != -1 :
                perm = i[pos+1:]
            
            try :
                l[ i ] = DVM_PERMISSIONS["MANIFEST_PERMISSION"][ perm ]
            except KeyError :
                l[ i ] = [ "dangerous", "Unknown permission from android reference", "Unknown permission from android reference" ]

        return l

    def get_main_activity(self) :
        """
            Return the name of the main activity

            :rtype: string
        """
        for i in self.xml :
            x = set()
            y = set()
            for item in self.xml[i].getElementsByTagName("activity") :
                for sitem in item.getElementsByTagName( "action" ) :
                    val = sitem.getAttribute( "android:name" )
                    if val == "android.intent.action.MAIN" :
                        x.add( item.getAttribute( "android:name" ) )
                   
                for sitem in item.getElementsByTagName( "category" ) :
                    val = sitem.getAttribute( "android:name" )
                    if val == "android.intent.category.LAUNCHER" :
                        y.add( item.getAttribute( "android:name" ) )
                
        z = x.intersection(y)
        if len(z) > 0 :
            return ('%s' % z.pop())
        return None

    def get_activitys(self):
        '''
            Return this APK declaration activitys name
            :rtype a list
        '''
        return self.__activitys.keys()
    
    def get_activitys_info(self):
        '''
            Return this APK declaration activitys information
            :rtype a dictionnary
        '''
        return self.__activitys
    
    def get_services(self):
        '''
            Return this APK declaration services name
            :rtype a list
        '''
        return self.__services.keys()

    def get_services_info(self):
        '''
            Return this APK declaration services information
            :rtype a dictionnary
        '''
        return self.__services
    
    def get_providers(self):
        '''
            Return this APK declaration providers name
            :rtype a list
        '''
        return self.__providers.keys()

    def get_providers_info(self):
        '''
            Return this APK declaration providers information
            :rtype a dictionnary
        '''
        return self.__providers

    def get_receivers(self):
        '''
            Return this APK declaration receivers name
            :rtype a list
        '''
        return self.__receivers.keys()
    
    def get_receivers_info(self):
        '''
            Return this APK declaration receivers information
            :rtype a dictionnary
        '''
        return self.__receivers
    

    def get_all_components(self):
        '''
            Return this APK declaration all components name
            :rtype a list
        '''
        return self.__components


    def get_all_info(self):
        '''
            Return this APK declaration all components information
            :rtype a dictionnary
        '''
        return self.parse_res
    
    def get_exported(self):
        '''
            Return this APK declaration all exported components
            :rtype a dictionnary
        '''
        return self.__exported

    def get_activity_count(self):
        return len(self.get_activitys())

    def get_service_count(self):
        return len(self.get_services())

    def get_provider_count(self):
        return len(self.get_providers())

    def get_receiver_count(self):
        return len(self.get_receivers())

    def get_exported_activity_count(self):
        return len(self.__exported['activity'])

    def get_exported_service_count(self):
        return len(self.__exported['service'])

    def get_exported_provider_count(self):
        return len(self.__exported['provider'])

    def get_exported_receiver_count(self):
        return len(self.__exported['receiver'])

    def get_exported_detail(self):
        return self.__exported_detail

    def is_declaration_component(self, component):
        '''
            Return declaration state for given component
            :rtype a boolean
        '''
        if component in self.__components:
            return True
        else:
            return False

    def is_exported_component(self, component):
        '''
            Return exported state for given component
            :rtype a boolean
        '''
        for (component_type, names) in self.__exported.items():
            if component in names:
                return True
        return False



class DexStringParser(APKParser):

    def __init__(self, file_name, const_list_name):
        APKParser.__init__(self, file_name)
        self.const_list_name = const_list_name
        self.dex_str_list = []
        self.all_providers_uris = set()
        # (key, value) = (provider:name, uris)
        self.providers_uris_map = {}
        self.action_list = []

        for i in self.zip.namelist():
            if i == "classes.dex":
                self.zip.extract(i)
                self.dex_file_object = open("classes.dex", 'rb')   

    def parse_dex_strings(self):

        self.dex_file_object.seek(DEX_STRING_TABLE_OFFSET, 0)
        tmp = self.dex_file_object.read(8)
        strings_count, string_table_off = struct.unpack("II", tmp)
        
        self.dex_file_object.seek(string_table_off, 0)
        dex_str_off_list = []

        count = 0
        while(count < strings_count):
            dex_str_off_list.append(struct.unpack("I", self.dex_file_object.read(4))[0])
            count += 1
        self.dex_str_list = []

        count = 0
        for str_offset in dex_str_off_list:
            self.dex_file_object.seek(str_offset, 0)
            strlen = self.__decode_unsignedLEB128(self.dex_file_object)
            if(strlen == 0):
                continue
            input = self.dex_file_object.read(strlen)
            self.dex_str_list.append(struct.unpack(str(strlen)+"s", input))
            count += 1
        
        
        outputfile = open(self.const_list_name, "w")
        for i in self.dex_str_list:
            outputfile.write("%s\n" % i)
        outputfile.close()

        self.dex_file_object.close()
        

    def parse_all_providers_uris(self):
        if len(self.dex_str_list) == 0: 
            self.parse_dex_strings()

        for i in self.dex_str_list:
            string = ("%s" % i)
            if string.upper().startswith("CONTENT://"):
                self.all_providers_uris.add(i)


    def parse_uris_by_provider(self, provider, authorities):
        for i in self.all_providers_uris:
            string = ("%s" % i)
            if string.find(authorities) != -1:
                if self.providers_uris_map.has_key(provider): 
                    self.providers_uris_map[provider].add(i)
                else :
                    new_item = set()
                    self.providers_uris_map[provider] = new_item

    def __decode_unsignedLEB128(self, file):
        
        result = ord(file.read(1))
        if result > 0x7f:            
            next = ord(file.read(1))
            result = (result & 0x7f) | (next & 0x7f) << 7
            if(next > 0x7f):
                next = ord(file.read(1))
                result = result | (next & 0x7f) << 14
                if(next > 0x7f):
                    next = ord(file.read(1))
                    result = result | (next & 0x7f) << 21
                    if(next > 0x7f):
                        next = ord(file.read(1))
                        result = result | next << 28  
        return result
        
    def get_all_providers_uris(self):
        return self.all_providers_uris

    def get_uris_by_provider(self, provider):
        if self.providers_uris_map.has_key(provider):
            return self.providers_uris_map[provider]
        else :
            print "No have this provider %s" , provider

    def get_dex_string_list(self):
        return self.dex_str_list


def start_apk_parse(apk_path):
    dex_string = DexStringParser(apk_path, DIR + URI_FILE)
    dex_string.parse_all_providers_uris()

    manifest_parser = ManifestParser(apk_path)
    manifest_parser.analyzer_manifest();
    manifest_parser.attacksurface()

    return dex_string, manifest_parser

def find_content_uris(dex_string, package_name):
    with open(DIR + package_name + URI_FILE,'wb+') as file_object:
        uris = dex_string.get_all_providers_uris()
        for uri in uris:
            uri_string = ("%s\n" % uri)
            file_object.write(uri_string)

def find_exposed_cp(manifest_parser):
    package_name = manifest_parser.get_package_name()
    with open(DIR + package_name + EXPORTED_FILE,'wb+') as file_object:
        exported_cps = manifest_parser.get_exported()
        #file_object.write("%s\n" % exported_cps)
        for key in exported_cps.iterkeys():
            for exported_cp in exported_cps[key]:
                file_object.write(exported_cp + '\n')

def show_manifest_detail(manifest_parser):
    print "APK information ------"
    print "APK name is: " , manifest_parser.get_name()
    print "APK packageName is: " , manifest_parser.get_package_name()
    print "APK androidversion name: " , manifest_parser.get_androidversion_name()
    print "APK androidversion code: " , manifest_parser.get_androidversion_code()
    print "APK size is: " , manifest_parser.get_size()
    print "APK md5 is: " ,  manifest_parser.get_md5()
    print "APK sha1 is: " , manifest_parser.get_sha1()
    print "APK min sdk is: " , manifest_parser.get_min_sdk()
    print "APK target sdk is: " , manifest_parser.get_target_sdk()
    print "\n"
    print "APK attacksurface ------"
    print "APK share user id: " , manifest_parser.get_share_user_id()
    print "APK allow backup: " , manifest_parser.get_allow_backup()
    print "APK debuggable: " , manifest_parser.get_debuggable()
    print "APK exposed components:\n" , manifest_parser.get_exported()
    print "\n"
    print "APK permissions ------"
    print manifest_parser.get_permissions()
    #print "APK details permissions:\n" , manifest_parser.get_details_permissions()

def dump_manifest_detail(manifest_parser):
    package_name = manifest_parser.get_package_name()
    res = "\nAPK information ------"
    res += "\nAPK name is: " + str(manifest_parser.get_name())
    res += "\nAPK packageName is: " + str(manifest_parser.get_package_name())
    res += "\nAPK androidversion name: " + str(manifest_parser.get_androidversion_name())
    res += "\nAPK androidversion code: " + str(manifest_parser.get_androidversion_code())
    res += "\nAPK size is: " + str(manifest_parser.get_size())
    res += "\nAPK md5 is: " + str(manifest_parser.get_md5())
    res += "\nAPK sha1 is: " + str(manifest_parser.get_sha1())
    res += "\nAPK min sdk is: " + str(manifest_parser.get_min_sdk())
    res += "\nAPK target sdk is: " + str(manifest_parser.get_target_sdk())
    res += "\n"
    res += "\nAPK attacksurface ------"
    res += "\nAPK share user id: " + str(manifest_parser.get_share_user_id())
    res += "\nAPK allow backup: " + str(manifest_parser.get_allow_backup())
    res += "\nAPK debuggable: " + str(manifest_parser.get_debuggable())
    res += "\nAPK exposed components:\n" + str(manifest_parser.get_exported())
    res += "\nAPK exposed activity count: " + str(manifest_parser.get_exported_activity_count())
    res += "\nAPK exposed service count: " + str(manifest_parser.get_exported_service_count())
    res += "\nAPK exposed provider count: " + str(manifest_parser.get_exported_provider_count())
    res += "\nAPK exposed receiver count: " + str(manifest_parser.get_exported_receiver_count())
    res += "\nAPK activity count: " + str(manifest_parser.get_activity_count())
    res += "\nAPK service count: " + str(manifest_parser.get_service_count())
    res += "\nAPK provider count: " + str(manifest_parser.get_provider_count())
    res += "\nAPK receiver count: " + str(manifest_parser.get_receiver_count())
    res += "\n"
    res += "\nAPK permissions ------"
    res += "\n" + str(manifest_parser.get_permissions())
    res += "\n" + str(manifest_parser.get_details_permissions())
    with open(DIR + package_name + RES,'wb+') as file_object:
        file_object.write(res)

    def get_activity_count(self):
        return len(self.get_activitys())

    def get_service_count(self):
        return len(self.get_services())

    def get_provider_count(self):
        return len(self.get_providers())

    def get_receiver_count(self):
        return len(self.get_receivers())

	
def start(apk_path, out):
    if out:
        global DIR
        DIR = out
    dex_string, manifest_parser = start_apk_parse(apk_path)
    print "package name:", manifest_parser.get_package_name()
    find_content_uris(dex_string, manifest_parser.get_package_name())
    find_exposed_cp(manifest_parser)
    a = dex_string.get_all_providers_uris()
    #show_manifest_detail(manifest_parser)
    dump_manifest_detail(manifest_parser)


if __name__ == '__main__':

    usage = 'usage: %prog -f apk_path -o out_dir'
    parser = OptionParser(usage = usage)
    parser.add_option('-f', '--apk_path', dest = 'apk_path', help = 'apk name to static audit')
    parser.add_option('-o', '--out_dir', dest = 'out_dir', help = 'out_dir to dump result')
    (options, args) = parser.parse_args()

    if options.apk_path == None:
        parser.error('incorrect arguments')

    print 'start apk parser analysis ...'
    apk_path = options.apk_path
    out_dir = options.out_dir
    print "apk path: ", apk_path
    print 'out_dir: ', out_dir
    start(apk_path, out_dir)
    print "end..."