package com.brokeh.runtasticpro;

import java.util.ArrayList;
import java.util.List;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class RuntasticProModule implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.runtastic.android"))
            return;

        findAndHookMethod("com.runtastic.android.RuntasticConfiguration", lpparam.classLoader, "isPro", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });

        findAndHookMethod("com.runtastic.android.common.ProjectConfiguration", lpparam.classLoader, "handleUsersMeResponse", "at.runtastic.server.comm.resources.data.user.MeResponse", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Object userInfo = XposedHelpers.getObjectField(param.args[0], "userInfo");
                Object userData = XposedHelpers.getObjectField(userInfo, "userData");
                List<Object> subscriptions = (List<Object>)XposedHelpers.getObjectField(userData, "subscriptions");
                if (subscriptions == null) {
                    subscriptions = new ArrayList<Object>();
                    XposedHelpers.setObjectField(userData, "subscriptions", subscriptions);
                }

                Object goldSubscription = XposedHelpers.newInstance(XposedHelpers.findClass("at.runtastic.server.comm.resources.data.user.SubscriptionData", lpparam.classLoader));
                XposedHelpers.callMethod(goldSubscription, "setActive", true);
                XposedHelpers.callMethod(goldSubscription, "setAppName", "runtastic");
                XposedHelpers.callMethod(goldSubscription, "setExtData", "");
                XposedHelpers.callMethod(goldSubscription, "setId", 1);
                XposedHelpers.callMethod(goldSubscription, "setPaidContractSince", 1L);
                XposedHelpers.callMethod(goldSubscription, "setPaymentProvider", "xposed");
                XposedHelpers.callMethod(goldSubscription, "setPaymentProviderText", "Xposed");
                XposedHelpers.callMethod(goldSubscription, "setPlanName", "gold");
                XposedHelpers.callMethod(goldSubscription, "setStatus", "");
                //XposedHelpers.callMethod(goldSubscription, "setSubscriptionOffer", true);
                XposedHelpers.callMethod(goldSubscription, "setUpdatedAt", 2L);
                XposedHelpers.callMethod(goldSubscription, "setValidFrom", 1L);
                XposedHelpers.callMethod(goldSubscription, "setValidTo", Long.MAX_VALUE - 2);

                subscriptions.add(goldSubscription);
            }
        });
    }
}
