package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class Utils {
	
	public static String getPreferences(String key, Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String userName = sharedPreferences.getString(key, "");
		return userName;
	}

	public static int getPreferencesInt(String key, Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		int int_value = sharedPreferences.getInt(key, 0);
		return int_value;
	}

	public static boolean savePreferences(String key, String value,
			Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
		return true;
	}

	public static boolean savePreferencesInt(String key, int value,
										  Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
		return true;
	}





	public static byte[] getImageBytes(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	public static Bitmap getImage(byte[] image) {
		return BitmapFactory.decodeByteArray(image, 0, image.length);
	}

	public static byte[] getBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}
		return byteBuffer.toByteArray();
	}








	public void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size=1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
	}



	public static void showAlertDialog(Context context, String title,
									   String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title != null)
			builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton("OK", null);
		builder.show();
	}

	/**
	 *
	 * @param c
	 * @return
	 */

	public static String getClassName(Class c) {
		String className = c.getName();
		int firstChar;
		firstChar = className.lastIndexOf('.') + 1;
		if (firstChar > 0) {
			className = className.substring(firstChar);
		}
		return className;
	}
	public static Bitmap resizeMapIcons(Context context, String iconName,int width, int height){
		Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
		return resizedBitmap;
	}

}
