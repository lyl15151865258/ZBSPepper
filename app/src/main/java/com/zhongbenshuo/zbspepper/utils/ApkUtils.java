package com.zhongbenshuo.zbspepper.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.zhongbenshuo.zbspepper.bean.AppInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiYuliang on 2017/10/9.
 * 软件管理工具
 *
 * @author LiYuliang
 * @version 2017/11/13
 */

public class ApkUtils {

    /**
     * 获取Apk文件的Log图标
     */
    public static Drawable getApkThumbnail(Context context, String apk_path) {
        if (context == null) {
            return null;
        }

        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        /**获取apk的图标 */
        appInfo.sourceDir = apk_path;
        appInfo.publicSourceDir = apk_path;
        if (appInfo != null) {
            Drawable apk_icon = appInfo.loadIcon(pm);
            return apk_icon;
        }

        return null;
    }

    /**
     * 获取Drawable实际占用大小
     */
    public static int getDrawableSize(Drawable drawable) {

        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);

//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.fav_jpg);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int len = baos.toByteArray().length;
        System.out.println("#############>>>>>>>>>" + len);

        return len;
    }

    public static int getBitmapSize(Bitmap bitmap) {
        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int len = baos.toByteArray().length;
        System.out.println("#############>>>>>>>>>" + len);
        return len;
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

    }


    //----------------------------
    //1.压缩功能， 压缩到指定大小
    //2.Drawable --->>> Bitmap
    //3.Bitmap   --->>> byte[]
    //4.Bitmap   --->>> 生成图片


    /**
     * Drawable转Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        //建立对应的Bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);

        return bitmap;
    }


    /**
     * Bitmap转ByteArray
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Bitmap 写入到SD卡
     *
     * @param bitmap
     * @param resPath
     * @return
     */
    public static boolean bitmapToSDCard(Bitmap bitmap, String resPath) {
        if (bitmap == null) {
            return false;
        }
        File resFile = new File(resPath);
        try {
            FileOutputStream fos = new FileOutputStream(resFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Bitmap压缩到指定的千字节数（比方说图片要压缩成32K，则传32）
     *
     * @param srcBitmap
     * @param maxKByteCount 比方说图片要压缩成32K，则传32
     * @return
     */
    public static Bitmap compressBitmap(Bitmap srcBitmap, int maxKByteCount) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            int option = 98;
            while (baos.toByteArray().length / 1024 >= maxKByteCount && option > 0) {
                baos.reset();
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);
                option -= 2;
            }
        } catch (Exception e) {

        }
//        bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(bais, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 压缩图片到指定的文件去————注意，图片尺寸没变，变的只是文件大小（图片的位深度改变了）
     *
     * @param srcBitmap
     * @param maxKByteCount 最大千字节数（比方说图片要压缩成32K，则传32）
     * @param targetPath    目标图片地址
     * @throws IOException
     */
    public static boolean compressBitmap(Bitmap srcBitmap, int maxKByteCount, String targetPath) {
        boolean result = false;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int option = 98;
            while (baos.toByteArray().length / 1024 >= maxKByteCount && option > 0) {
                baos.reset();
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);
                option -= 2;
            }
            byte[] bitmapByte = baos.toByteArray();

            File targetFile = new File(targetPath);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(targetFile);
            fos.write(bitmapByte);

            result = true;

            try {
                fos.close();
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!srcBitmap.isRecycled()) {
                srcBitmap.recycle();
                srcBitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    //----------------------------


    /**
     * 判断指定报名的App是否装过
     *
     * @param context
     * @param packagename
     * @return
     */
    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 获取应用包名
     *
     * @param context
     * @param filePath
     * @return
     */
    public static String getPackageName(Context context, String filePath) {
        String packageName = "";

        if (context == null || TextUtils.isEmpty(filePath)) {
            return packageName;
        }

        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = null;
        if (info != null) {
            appInfo = info.applicationInfo;
            packageName = appInfo.packageName;
        }

        return packageName;
    }


    /**
     * 判断一个Android应用文件是否装过
     *
     * @param context
     * @param filePath
     * @return true装过， 反之未装过
     */
    public static boolean isInstalled(Context context, String filePath) {
        String packageName = getPackageName(context, filePath);
        return isAppInstalled(context, packageName);
    }


    /**
     * 安装Apk文件
     *
     * @param context
     * @param apkFilePath
     */
    public static void installApk(Context context, String apkFilePath) {
        if (context == null) {
            throw new RuntimeException("ApkUtils install apk method and parameter context  == null?");
        }
        File file = new File(apkFilePath);
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获取软件版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当前程序的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            //获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询手机内所有应用
     */
    public static List<PackageInfo> getAllApps(Context context) {
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        return pManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
    }

    /**
     * 查询手机内手动安装的应用
     */
    public static List<PackageInfo> getAllUserApps(Context context) {
        List<PackageInfo> apps = new ArrayList<>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                apps.add(pak);
            }
        }
        return apps;
    }

    /**
     * 查询手机内系统预装应用
     */
    public static List<PackageInfo> getAllSystemApps(Context context) {
        List<PackageInfo> apps = new ArrayList<>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                apps.add(pak);
            }
        }
        return apps;
    }

    /**
     * 查询手机内所有的应用
     */
    public static List<AppInfo> scanApps(Context context) {
        List<AppInfo> appInfoList = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pManager = context.getPackageManager();
        List<ResolveInfo> appList = pManager.queryIntentActivities(intent, 0);
        for (ResolveInfo info : appList) {
            String appName = info.loadLabel(context.getPackageManager()).toString();
            Drawable appIcon = info.loadIcon(context.getPackageManager());
            String packageName = info.activityInfo.packageName;
            appInfoList.add(new AppInfo(appName, appIcon, packageName));
        }
        LogUtils.d("ApkUtils", "本次扫描到" + appInfoList.size() + "个应用");
        return appInfoList;
    }

    /**
     * 查询手机内所有支持分享的应用
     */
    public static List<ResolveInfo> getShareApps(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        PackageManager pManager = context.getPackageManager();
        return pManager.queryIntentActivities(intent, 0);
    }

    /**
     * 通过指定的包名打开应用
     *
     * @param context     上下文
     * @param packageName 指定启动的包名
     */
    public static void openAppByPackageName(Context context, String packageName) {
        if (checkApplication(context, packageName)) {
            Intent localIntent = new Intent("android.intent.action.MAIN", null);
            localIntent.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> appList = context.getPackageManager().queryIntentActivities(localIntent, 0);
            for (int i = 0; i < appList.size(); i++) {
                ResolveInfo resolveInfo = appList.get(i);
                String packageStr = resolveInfo.activityInfo.packageName;
                String className = resolveInfo.activityInfo.name;
                if (packageStr.equals(packageName)) {
                    // 这个就是你想要的那个Activity
                    ComponentName cn = new ComponentName(packageStr, className);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cn);
                    context.startActivity(intent);
                }
            }
        } else {
            Toast.makeText(context, "未安装此应用", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 卸载指定应用的包名
     *
     * @param context     上下文
     * @param packageName 指定的应用包名
     */
    public static void unInstall(Context context, String packageName) {
        if (checkApplication(context, packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(packageURI);
            context.startActivity(intent);
        }
    }

    /**
     * 判断该包名的应用是否安装
     *
     * @param context     上下文
     * @param packageName 应用包名
     * @return 是否安装
     */
    public static boolean checkApplication(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 根据PackageInfo获取APP名称
     */
    public static String getApplicationLabel(Context context, PackageInfo packageInfo) {
        if (packageInfo == null) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        return packageInfo.applicationInfo.loadLabel(pm).toString();
    }

    /**
     * 根据PackageInfo获取APP图标
     */
    public static Drawable getApplicationIcon(Context context, PackageInfo packageInfo) {
        if (packageInfo == null) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        return packageInfo.applicationInfo.loadIcon(pm);
    }

    /**
     * 根据包名获取APP名称
     */
    public static String getApplicationLabel(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
