package com.brokeh.runtasticpro;

import java.util.ArrayList;
import java.util.List;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
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

        findAndHookMethod("at.runtastic.server.comm.resources.data.user.SubscriptionData", lpparam.classLoader, "getPlanName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("RuntasticPro: plan name = " + param.getResult());
                param.setResult("gold");
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

        /*findAndHookMethod("com.runtastic.android.modules.statistics.modules.filter.comparison.view.ComparisonTimeFrameChipView", lpparam.classLoader, "f", boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = true;
            }
        });*/

        /*findAndHookMethod("com.runtastic.android.network.newsfeed.data.model.UserData", lpparam.classLoader, "isPremium", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });

        findAndHookConstructor("f.a.a.a.e.d.b.a.a.g.b", lpparam.classLoader, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("RuntasticPro: Setting premium to true");
                param.args[0] = true;
            }
        });

        findAndHookConstructor("f.a.a.a.e.d.b.c.b.a", lpparam.classLoader, int.class, boolean.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("RuntasticPro: Setting premium params to true");
                param.args[1] = true;
                param.args[2] = true;
            }
        });*/

        /*try {
            findAndHookMethod("f.a.a.a.e.d.b.a.a.c", lpparam.classLoader, "invokeSuspend", Object.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object obj = XposedHelpers.getObjectField(param.thisObject, "f721f");
                    XposedHelpers.setBooleanField(obj, "i", true);
                }
            });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("RuntasticPro: NoSuchMethodError!");
        } catch (XposedHelpers.ClassNotFoundError e) {
            XposedBridge.log("RuntasticPro: ClassNotFoundError!");
        }*/


        /*try {
            findAndHookMethod("f.a.a.a.e.d.b.a.a.a", lpparam.classLoader, "invoke", Object.class, Object.class, Object.class, Object.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("----- RTS stack start -----");
                    for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                        XposedBridge.log(" RTS stack: " + ste.toString());
                    }
                    XposedBridge.log("----- RTS stack end -----");
                }
            });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("RuntasticPro: NoSuchMethodError!");
        } catch (XposedHelpers.ClassNotFoundError e) {
            XposedBridge.log("RuntasticPro: ClassNotFoundError!");
        }*/
    }
}
