package com.chen.crash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException������,��������Uncaught�쳣��ʱ��,�и������ӹܳ���,����¼���ʹ��󱨸�.
 * 
 * @author user
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

    // ϵͳĬ�ϵ�UncaughtException������
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // CrashHandlerʵ��
    private static CrashHandler INSTANCE = new CrashHandler();
    // �����Context����
    private Context mContext;
    // �����洢�豸��Ϣ���쳣��Ϣ
    private Map<String, String> infos = new HashMap<String, String>();

    // ���ڸ�ʽ������,��Ϊ��־�ļ�����һ����
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /** ��ֻ֤��һ��CrashHandlerʵ�� */
    private CrashHandler() {
    }

    /** ��ȡCrashHandlerʵ�� ,����ģʽ */
    public static CrashHandler getInstance() {
            return INSTANCE;
    }

    /**
     * ��ʼ��
     *
     * @param context
     */
    public void init(Context context) {
            mContext = context;

            // ��ȡϵͳĬ�ϵ�UncaughtException������
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            // ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
            Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * ��UncaughtException����ʱ��ת��ú���������
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
            if (!handleException(ex) && mDefaultHandler != null) {
                    // ����û�û�д�������ϵͳĬ�ϵ��쳣������������
                    mDefaultHandler.uncaughtException(thread, ex);
            } else {
                    try {
                            Thread.sleep(3000);
                    } catch (InterruptedException e) {
                            e.printStackTrace();
                    }
                    // �˳�����
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
            }
    }

    /**
     * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����.
     *
     * @param ex
     * @return true:��������˸��쳣��Ϣ;���򷵻�false.
     */
    private boolean handleException(Throwable ex) {
            if (ex == null) {
                    return false;
            }
            ex.printStackTrace();

            // collect device info
            collectDeviceInfo(mContext);

            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sb.append(key + "=" + value + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                    cause.printStackTrace(printWriter);
                    cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append(result);

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.fromParts("mailto",
                           "514858501@qq.com", null));
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getPackageName()
                            + "sorry");
            if (!util.startActivity(intent, false, mContext)) {
                    // save the error log
                    final String path = saveCrashInfo2File(sb);
                    // show toast
                    new Thread() {
                            @Override
                            public void run() {
                                    Looper.prepare();
                                    Toast.makeText(
                                                    mContext,
                                                    mContext.getPackageName()
                                                                    + "sorry" + path,
                                                    Toast.LENGTH_LONG).show();
                                    Looper.loop();
                            }
                    }.start();
            }

            return true;
    }

    /**
     * �ռ��豸������Ϣ
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                    try {
                            field.setAccessible(true);
                            infos.put(field.getName(), field.get(null).toString());
                            Log.d(field.getName(), field.get(null).toString());
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
            }

            infos.put("versionName", util.getVersion(ctx));
            infos.put("versionCode", util.getVersionCode(ctx));
    }

    /**
     * ���������Ϣ���ļ���
     *
     * @param ex
     * @return �����ļ�����,���ڽ��ļ����͵�������
     */
    private String saveCrashInfo2File(StringBuffer sb) {

            String path = util.preparePath(mContext) + "crash/";
            try {
                    long timestamp = System.currentTimeMillis();
                    String time = formatter.format(new Date());
                    String fileName = "crash-" + time + "-" + timestamp + ".log";
                    File dir = new File(path);
                    if (!dir.exists()) {
                            dir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(path + fileName);
                    fos.write(sb.toString().getBytes());
                    fos.close();
                    return path + fileName;
            } catch (Exception e) {
                    e.printStackTrace();
            }
            return "";
    }
}
