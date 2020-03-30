package io.alcatraz.noapplet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

@SuppressWarnings("FieldCanBeLocal")
public class ModuleMain implements IXposedHookLoadPackage {
    private String targetPackage = "com.tencent.mobileqq";
    private String intentCls = "android.content.Intent";
    private String activityCls = "com.tencent.mobileqq.mini.appbrand.ui.AppBrandUI";
    private String configCls = "com.tencent.mobileqq.mini.apkg.MiniAppConfig";
    private String brandProxyCls = "com.tencent.mobileqq.mini.launch.AppBrandProxy";
    private Activity activityContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Log.e("AlcQQ", "handle pkg");
        if (loadPackageParam.packageName.equals(targetPackage)) {
            Log.e("AlcQQ", "matched pkg");
            findAndHookMethod(activityCls, loadPackageParam.classLoader, "doOnCreate", Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Log.e("AlcQQ", "got context");
                            activityContext = (Activity) param.thisObject;
                            super.afterHookedMethod(param);
                        }
                    });

//            findAndHookMethod(intentCls, loadPackageParam.classLoader, "putExtra", String.class,
//                    Parcelable.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            String key = (String) param.args[0];    //Extra key
//                            Object pack = param.args[1];            //Parcelable data
//
//                            if (key != null && key.equals("key_mini_app_config")) {
//                                Log.e("AlcQQ","key");
//                                if (pack != null) {
//                                    Object info_bean = XposedHelpers.getObjectField(pack,"config");
//                                    Object app_name = XposedHelpers.getObjectField(info_bean,"name");
//                                    Object iconUrl = XposedHelpers.getObjectField(info_bean,"iconUrl");
//                                    Object description = XposedHelpers.getObjectField(info_bean, "desc");
//                                    Object pageinfo = XposedHelpers.getObjectField(info_bean,"firstPage");
//                                    Object url = XposedHelpers.getObjectField(pageinfo,"pagePath");
//                                    Object info_obj = XposedHelpers.callMethod(pack, "toString");
//                                    if (info_obj != null) {
//                                        final String data = info_obj.toString();
//
//                                        Intent intent = new Intent();
//                                        ComponentName noapplet
//                                                = new ComponentName("io.alcatraz.noapplet",
//                                                "io.alcatraz.noapplet.XposedAppletActivity");
//                                        intent.setComponent(noapplet);
//                                        intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_INFO, data);
//                                        intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_NAME, app_name.toString());
//                                        intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_DESCRIPTION,description.toString());
//                                        intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_ICON_URL,iconUrl.toString());
//                                        intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_URL,url.toString());
//                                        activityContext.startActivity(intent);
//                                    }
//                                }
//                            }
//                            super.afterHookedMethod(param);
//                        }
//                    });
//            findAndHookMethod(configCls, loadPackageParam.classLoader, "writeToParcel", Parcel.class,
//                    int.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            Object info_bean = param.thisObject;
//                            Log.e("AlcQQ",info_bean.toString());
//                            super.afterHookedMethod(param);
//                        }
//                    });
            findAndHookMethod(brandProxyCls, loadPackageParam.classLoader, "onAppForeground", String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Log.e("AlcQQ", "foreground");
                            Object cfg_bean = XposedHelpers.getObjectField(param.thisObject, "mMiniConfig");
                            if(cfg_bean!=null) {
                                Object info_bean = XposedHelpers.getObjectField(cfg_bean, "config");
                                if (info_bean != null) {
                                    final String data = info_bean.toString();
                                    Object app_name = XposedHelpers.getObjectField(info_bean, "name");
                                    Object iconUrl = XposedHelpers.getObjectField(info_bean, "iconUrl");
                                    Object description = XposedHelpers.getObjectField(info_bean, "desc");
                                    Object pageinfo = XposedHelpers.getObjectField(info_bean, "firstPage");
                                    Object url = XposedHelpers.getObjectField(pageinfo, "pagePath");

                                    final Intent intent = new Intent();
                                    ComponentName noapplet
                                            = new ComponentName("io.alcatraz.noapplet",
                                            "io.alcatraz.noapplet.XposedAppletActivity");
                                    intent.setComponent(noapplet);
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_INFO, data);
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_NAME, app_name.toString());
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_DESCRIPTION, description.toString());
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_ICON_URL, iconUrl.toString());
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_URL, url.toString());

                                    new AlertDialog.Builder(activityContext)
                                            .setTitle("NoApplet")
                                            .setMessage("是否尝试解析当前小程序")
                                            .setNegativeButton("否", null)
                                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    activityContext.startActivity(intent);
                                                }
                                            }).show();
                                }
                            }
                            super.beforeHookedMethod(param);
                        }
                    });
        }
    }
}
