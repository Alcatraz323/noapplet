package io.alcatraz.noapplet;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressWarnings("FieldCanBeLocal")
public class ModuleMain implements IXposedHookLoadPackage {
    private String targetPackage = "com.tencent.mobileqq";
    private String activityCls = "com.tencent.mobileqq.activity.SplashActivity";
    private String launchManagerCls = "com.tencent.mobileqq.mini.launch.AppBrandLaunchManager";
    private Activity activityContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (loadPackageParam.packageName.equals(targetPackage)) {
            XposedHelpers.findAndHookMethod(activityCls, loadPackageParam.classLoader, "doOnCreate", Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Log.e("AlcQQ", "got context");
                            activityContext = (Activity) param.thisObject;
                            super.afterHookedMethod(param);
                        }
                    });
            XposedBridge.hookAllMethods(XposedHelpers.findClass(launchManagerCls, loadPackageParam.classLoader), "startMiniApp",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Log.e("AlcQQ", "manager hook");
                            Object cfg_bean = param.args[1];
                            if (cfg_bean != null) {
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
                                            "io.alcatraz.noapplet.XposedPopUpActivity");

                                    intent.setComponent(noapplet);
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_INFO, data);
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_NAME, app_name.toString());
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_DESCRIPTION, description.toString());
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_ICON_URL, iconUrl.toString());
                                    intent.putExtra(XposedAppletActivity.EXTRA_XPOSED_MINI_APP_URL, url.toString());
                                    activityContext.startActivity(intent);
                                }
                            }
                            super.afterHookedMethod(param);
                        }
                    });
        }
    }
}
