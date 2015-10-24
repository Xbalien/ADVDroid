# ADVDroid
statically vetting android apps based soot
# run
 1.python ADVDroid/python/apkparser.py -f apk_path
  
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
  
  find_content_uris get the uris in the apk, more details in [ADVDroid/python/sampleapk/com.wisorg.fzdx.content_uri](https://github.com/Xbalien/ADVDroid/blob/master/python/sampleapk/com.wisorg.fzdx.content_uri). It is used to [ComponentFuzzer](https://github.com/Xbalien/ComponentFuzzer) fuzzing content provider.

    content://com.wisorg.fzdx/favorites?notify=false
    content://telephony/carriers/preferapn
    content://com.android.contacts/
    content://
    content://com.wisorg.fzdx/favorites?notify=true
  
  find_exposed_cp get the exposed compontents, It is used to constructing the intent structure.  ADVDroid/src/org/rois/asvdroid/test/TestSoot.java 
  
  2.ADVDroid/src/org/rois/asvdroid/test/TestSoot.java is the APK's source code vetting entry
  
  It has two parts:
  
  (1) Construct intent struct (parse exposed component), more details in [ADVDroid/out/com.wisorg.fzdx.json](https://github.com/Xbalien/ADVDroid/blob/master/out/com.wisorg.fzdx.json)
  It is used to [ComponentFuzzer](https://github.com/Xbalien/ComponentFuzzer) fuzzing intent.
    
    {
    "com.wisorg.identity.view.LoginActivity":{"getStringExtra":["com.wisorg.sso.PACKAGE_NAME","com.wisorg.sso.APP_NAME"],"getIntExtra":["com.wisorg.sso.SDK_VERSION"]},
    "com.wisorg.fzdx.activity.SplashActivity":{"getSerializableExtra":["EXTRA_NOTICE"],"getBooleanExtra":["EXTRA_BACGROUND"]},
    "com.wisorg.fzdx.activity.news.NewsAggregationMainActivity":{"getStringExtra":["openUrl"]},"com.wisorg.fzdx.activity.notice.NoticeSubscribeListActivity":{"getStringExtra":["openUrl"]},
    "com.wisorg.jslibrary.HybirdInstallActivity":{"getStringExtra":["EXTRA_INSTALL_NAME"]},
    "com.wisorg.fzdx.receiver.AlarmReceiver":{"getSerializableExtra":["EXTRA_PUSH_MESSAGE"],"getLongExtra":["extra_download_id"]},
    "com.wisorg.fzdx.activity.ControlActvity":{"getSerializable":["EXTRA_NOTICE"]}
    }
  
  (2) API Reachability Analysis
  
  It can be used to vetting API misuse and API sink is reachable, more details in [ADVDroid/out/com.wisorg.fzdx_result.xml](https://github.com/Xbalien/ADVDroid/blob/master/out/com.wisorg.fzdx_result.xml).  



  