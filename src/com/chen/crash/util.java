package com.chen.crash;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

public class util {

	static public boolean startActivity(Intent intent, boolean showToast,
			Context context) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (showToast)
				try {
					AlertDialog dlg = new AlertDialog.Builder(context)
							.setMessage(e.toString())
							.setPositiveButton("ok",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).create();
					dlg.show();
				} catch (Exception ee) {
					ee.printStackTrace();
					Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
							.show();
				}
			return false;
		}
	}

	static public String getVersion(Context context) {
		String version = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null)
				version = pi.versionName == null ? String
						.valueOf(pi.versionCode) : pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}

	static public String getVersionCode(Context context) {
		String version = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null)
				version = String.valueOf(pi.versionCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}

	static public String preparePath(Context context) {
		String defaultPath = "/data/data/" + context.getPackageName();
		try {
			defaultPath = context.getFilesDir().getPath();
		} catch (Exception e) {
		}

		String downloadPath = defaultPath + "/";

		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
			downloadPath = Environment.getExternalStorageDirectory()
					+ "/simpleHome/";

		java.io.File myFilePath = new java.io.File(downloadPath);
		try {
			if (myFilePath.isDirectory())
				;// folder exist
			else
				myFilePath.mkdir();// create folder

			File path = new File(downloadPath + "snap/");
			if (path.isDirectory())
				;// folder exist
			else
				path.mkdir();// create folder

			path = new File(downloadPath + "apk/");
			if (path.isDirectory())
				;// folder exist
			else
				path.mkdir();// create folder

			path = new File(downloadPath + "cache/");
			if (path.isDirectory())
				;// folder exist
			else
				path.mkdir();// create folder

			path = new File(downloadPath + "bookmark/");
			if (path.isDirectory())
				;// folder exist
			else
				path.mkdir();// create folder
		} catch (Exception e) {
			downloadPath = defaultPath + "/";
		}

		return downloadPath;
	}

}
