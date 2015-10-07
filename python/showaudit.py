'''
Created on Sep 7, 2015

@author: Xbalien
'''

import os

class ShowMenifestAudit(object):

    def __init__(self, file_obejct, attacksurface, allow_backup, debuggable):
        self.res = ""
        self.file_obejct = file_obejct
        self.attacksurface = attacksurface
        self.allow_backup = allow_backup
        self.debuggable = debuggable

    def show(self):

        self.res += ("################################ manifest_config ######################################" + '\n')
        if self.allow_backup:
            self.res += ("allow_backup : %s\n" % self.allow_backup)
        else :
            self.res += "allow_backup : true\n" 

        if self.debuggable:
            self.res += ("debuggable : %s\n" % self.debuggable)
        else :
            self.res += "debuggable : false\n"


        for component_type in self.attacksurface:
            self.res += ("################################### %s ###################################" % (component_type.upper()) + '\n')
            for component in self.attacksurface[component_type]:
                self.res += (component + '\n')
       
        self.file_obejct.write(self.res)



class ShowSourceAudit(object):

    def __init__(self, file_obejct, webview, register_receiver, https_res, intent_scheme_res, log_res):
        self.res = ""
        self.file_obejct = file_obejct
        self.webview = webview
        self.register_receiver = register_receiver
        self.https_res = https_res
        self.intent_scheme_res = intent_scheme_res
        self.log_res = log_res


    def show(self):

        self.res += ("################################ webview ######################################" + '\n')
        if self.webview:
            for key in self.webview:
                self.res += (key + '\n') + self.webview[key]
        else:
            self.res += "None\n"

        self.res += ("################################ https ######################################" + '\n')
        if self.https_res:
            for key in self.https_res:
                self.res += (key + '\n') + self.https_res[key]
        else:
            self.res += "None\n"


        self.res += ("################################ intent_scheme ######################################" + '\n')
        if self.intent_scheme_res:
            for key in self.intent_scheme_res:
                self.res += (key + '\n') + self.intent_scheme_res[key]
        else:
            self.res += "None\n"


        self.res += ("################################ logcat ######################################" + '\n')
        if self.log_res:
            for key in self.log_res:
                self.res += (key + '\n') + self.log_res[key]
        else:
            self.res += "None\n"


        self.res += ("################################ register_receiver ######################################" + '\n')
        if self.register_receiver:
            for key in self.register_receiver:
                self.res += (key + '\n') + self.register_receiver[key]
        else:
            self.res += "None\n"


        self.file_obejct.write(self.res)


class ShowReachAPI(object):

    def __init__(self, file_obejct, res):
        self.res = res
        self.file_obejct = file_obejct

    def show(self):
        self.file_obejct.write(self.res)