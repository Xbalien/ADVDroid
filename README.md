# ADVDroid
statically vetting android apps based soot
# run
 1.python ADVDroid/python/apkparser.py -f apk_path -o out_dir
  
  It support some apis for getting the manifest detail, such as:

    manifest_parser.get_all_components()
    manifest_parser.get_exported_detail()
    manifest_parser.get_exported()
    manifest_parser.get_exported_activity_count()
    manifest_parser.get_exported_service_count()
    manifest_parser.get_exported_provider_count()
    manifest_parser.get_exported_receiver_count()
    manifest_parser.get_allow_backup()
    manifest_parser.get_debuggable()
    manifest_parser.get_name()
    manifest_parser.get_size()
    manifest_parser.get_md5()
    manifest_parser.get_sha1()
    manifest_parser.get_sha256()
    manifest_parser.get_androidversion_name()
    manifest_parser.get_androidversion_code()
    manifest_parser.get_package_name()
    manifest_parser.get_permissions()
    manifest_parser.get_min_sdk()
    manifest_parser.get_target_sdk()
    manifest_parser.get_share_user_id()
    manifest_parser.get_main_activity()
    manifest_parser.get_details_permissions()

    find_content_uris(dex_string, manifest_parser.get_package_name())
    find_exposed_cp(manifest_parser)

  The final AndroidManifest.xml vetting result as follow [ADVDroid/out/com.esun.ui.am_result](https://github.com/Xbalien/ADVDroid/blob/dev/out/com.esun.ui.am_result):
  
	APK information ------
	APK name is: 500_lottery_client_for_Android_1.8.9.10044.apk
	APK packageName is: com.esun.ui
	APK androidversion name: 1.8.9
	APK androidversion code: 49
	APK size is: 6807159
	APK md5 is: e6e88458c529bc2f062c8f99970cada0
	APK sha1 is: c94e36376f3f14be2e8c182a9b013595b232a52a
	APK min sdk is: 7
	APK target sdk is: 16

	APK attacksurface ------
	APK share user id: None
	APK allow backup: false
	APK debuggable: false
	APK exposed components:
	{'provider': [], 'receiver': [u'com.esun.pushService.OnBootReceiver', u'com.esun.pushService.PushReceiver'], 'service': [], 'activity': [u'com.esun.ui.wxapi.WXPayEntryActivity', u'com.tencent.tauth.AuthActivity', u'com.esun.rabbit2.ui.activity.SplashActivity', u'com.esun.rabbit2.viewer.TestAct', u'com.esun.ui.wxapi.WXEntryActivity']}
	APK exposed activity count: 5
	APK exposed service count: 0
	APK exposed provider count: 0
	APK exposed receiver count: 2
	APK activity count: 198
	APK service count: 5
	APK provider count: 1
	APK receiver count: 2

	APK permissions ------
	['android.permission.ACCESS_NETWORK_STATE', 'android.permission.CALL_PHONE', 'android.permission.INTERNET', 'android.permission.VIBRATE', 'android.permission.ACCESS_FINE_LOCATION', 'android.permission.ACCESS_COARSE_LOCATION', 'android.permission.READ_PHONE_STATE', 'android.permission.ACCESS_WIFI_STATE', 'android.permission.CHANGE_WIFI_STATE', 'android.permission.WAKE_LOCK', 'android.permission.ACCESS_WIFI_STATE', 'android.permission.RECEIVE_BOOT_COMPLETED', 'android.permission.READ_LOGS', 'android.permission.WRITE_EXTERNAL_STORAGE', 'android.permission.GET_TASKS', 'android.permission.RECEIVE_SMS', 'android.permission.SYSTEM_ALERT_WINDOW']

  
  find_content_uris get the uris in the apk, more details in [ADVDroid/out/com.esun.ui.content_uri](https://github.com/Xbalien/ADVDroid/blob/dev/out/com.esun.ui.content_uri). It is used to [ComponentFuzzer](https://github.com/Xbalien/ComponentFuzzer) fuzzing content provider.

	content://com.esun.ui.messageBox/messages/
	content://com.esun.ui.messageBox/messages//#
	content://com.esun.ui.messageBox/messages
  
  find_exposed_cp get the exposed compontents, It is used to constructing the intent structure.  ADVDroid/src/org/rois/asvdroid/test/TestSoot.java 
  
  
  2.ADVDroid/src/org/rois/asvdroid/test/TestSoot.java is the APK's source code vetting entry
  
  It has two parts:
  
  (1) Construct intent struct (parse exposed component), more details in [ADVDroid/out/com.esun.ui.json](https://github.com/Xbalien/ADVDroid/blob/dev/out/com.esun.ui.json)
  It is used to [ComponentFuzzer](https://github.com/Xbalien/ComponentFuzzer) fuzzing intent.
    
	{
	"com.tencent.tauth.AuthActivity":
		{
		"STRINGS":["","error","shareToQQ","complete","cancel","shareToQzone"],
		"getString":["response","result","action","serial","access_token"]
		},
	"com.esun.rabbit2.ui.activity.SplashActivity":
		{
		"getBooleanExtra":["is_push_broadcast"]
		},
	"com.esun.ui.wxapi.WXEntryActivity":
		{
		"getStringExtra":["_wxapi_sendauth_resp_token","_wxapi_sendauth_resp_url"],
		"STRINGS":["wx_homehalllogin"]
		}
	}

  (2) API Reachability Analysis
  
  It can be used to vetting API misuse and API sink is reachable, more details in [ADVDroid/out/com.esun.ui_result.xml](https://github.com/Xbalien/ADVDroid/blob/dev/out/com.esun.ui_result.xml).  



  