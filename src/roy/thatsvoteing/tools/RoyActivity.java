package roy.thatsvoteing.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class RoyActivity extends Activity {
	public Intent intent;
	public Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = new Intent();
		bundle = new Bundle();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);// ��ջ
		// super.onAttachedToWindow();
	}

	// ��ȡMac��ַ
	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	// ��ȡ�ֻ�����
	public String getPhoneNumber() {
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getLine1Number();
	}

	// �����ַ�������ĸ��Ӣ�
	public String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	// ListViewչ������
	public void ListAnim(ListView list) {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(40);
		set.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(150);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		list.setLayoutAnimation(controller);
	}

	// ��ʾ��ʾ
	public void showToast(String str) {
		Toast msg = Toast.makeText(this, str, 200);
		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2,
				msg.getYOffset() / 2);
		msg.show();
	}

	// �Ƿ���Ҫȫ��
	public void isFullScreen(boolean full) {
		if (full) {
			Window window = getWindow();
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// ����ȫ��
		} else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
	}

	// ͨ��Uri�õ��ļ�����ʵ·��
	public String getAbsoluteImagePath(Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	// �ж�ϵͳ�汾�ķ���
	public static int getSDKVersionNumber() {
		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		return sdkVersion;
	}

	// Bitmap �� Drawable
	public Drawable convertBitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		// ��ΪBtimapDrawable��Drawable�����࣬����ֱ��ʹ��bd���󼴿ɡ�
		return bd;
	}

	// ��ȡ���ص�ͼƬ
	public Bitmap getLocalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ��ȡ�����ϵ�ͼƬ
	public Bitmap getHttpBitmap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setConnectTimeout(0);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	// ����Ի���
	public void showAlertDialog(String title, String message, String btn1,
			String btn2) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setPositiveButton(btn1, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setNegativeButton(btn2, null).create().show();
	}

	// Activity����ת
	public void startActivity(Activity activity, Class<?> cls, boolean finish) {
		if (finish) {
			intent.setClass(activity, cls);
			startActivity(intent);
			finish();
		} else {
			intent.setClass(activity, cls);
			startActivity(intent);
		}

	}

	// �ж��ֻ��Ƿ�����
	public boolean isInternet() {
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	public static Bitmap pBitmap(Bitmap b1, Bitmap b2) {
		Canvas c = new Canvas();
		c.setBitmap(b1);
		c.drawBitmap(b2, new Rect(0, 0, b2.getWidth(), b2.getHeight()),
				new Rect(0, 0, b1.getWidth(), b1.getHeight()), null);
		return b1;
	}

	public static Bitmap overlays(Bitmap bmp1, Bitmap bmp2, Bitmap bmp3,
			Bitmap bmp4) {
		Bitmap bmOverlay = Bitmap.createBitmap(415, 415, bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, 0, 0, null);
		canvas.drawBitmap(bmp2, 207, 0, null);
		canvas.drawBitmap(bmp3, 0, 207, null);
		canvas.drawBitmap(bmp4, 207, 207, null);
		return bmOverlay;
	}

	public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
				bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(bmp2, 0, 0, null);
		return bmOverlay;
	}

	// ������Ƭ
	public static void saveBitmap(Bitmap b, String bitName) throws IOException {
		File f = new File("/sdcard/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		b.compress(Bitmap.CompressFormat.PNG, 100, fos);// ��ͼƬ����ѹ������
		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ��������ε���ײ
	public static final boolean Peng_IsIntersectingRect(int ax, int ay, int aw,
			int ah, int bx, int by, int bw, int bh) {
		if (by + bh < ay || by > ay + ah || bx + bw < ax || bx > ax + aw)
			return false;
		return true;
	}

	/* ��;�����ײ */
	public static final boolean Peng_IsPointInRect(int px, int py, int x,
			int y, int w, int h) {
		if (px < x || px > x + w || py < y || py > y + h)
			return false;
		return true;
	}

	/**
	 * ͼƬȥɫ,���ػҶ�ͼƬ
	 * 
	 * @param bmpOriginal
	 *            �����ͼƬ
	 * @return ȥɫ���ͼƬ
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * ȥɫͬʱ��Բ��
	 * 
	 * @param bmpOriginal
	 *            ԭͼ
	 * @param pixels
	 *            Բ�ǻ���
	 * @return �޸ĺ��ͼƬ
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
		return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	}

	/**
	 * ��ͼƬ���Բ��
	 * 
	 * @param bitmap
	 *            ��Ҫ�޸ĵ�ͼƬ
	 * @param pixels
	 *            Բ�ǵĻ���
	 * @return Բ��ͼƬ
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * ʹԲ�ǹ���֧��BitampDrawable
	 * 
	 * @param bitmapDrawable
	 * @param pixels
	 * @return
	 */
	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
			int pixels) {
		Bitmap bitmap = bitmapDrawable.getBitmap();
		bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
		return bitmapDrawable;
	}

	// ��ȡͼƬ
	public static Bitmap cBitmap(Bitmap b, int x, int y, int w, int h) {
		Bitmap temp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas c = new Canvas();
		c.setBitmap(temp);
		c.drawBitmap(b, new Rect(x, y, x + w, y + h), new Rect(0, 0, w, h),
				null);
		return temp;
	}

	// ����������
	public Bitmap sBitmap(Bitmap b, int w, int h) {
		int width = b.getWidth();
		int height = b.getHeight();
		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);// ����
		return Bitmap.createBitmap(b, 0, 0, width, height, matrix, true);
	}

	// ��ת�Ƕ�
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				System.out.println("OOM");
			}
		}
		return b;
	}
	
	
	/**

     * ����ʡ�ڴ�ķ�ʽ��ȡ������Դ��ͼƬ

     * @param context

     * @param resId

     * @return

     */

    public  Bitmap readBitMap(Context context, int resId){ 

        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        opt.inPurgeable = true;

        opt.inInputShareable = true;

        // ��ȡ��ԴͼƬ

        InputStream is = context.getResources().openRawResource(resId);

        return BitmapFactory.decodeStream(is, null, opt);

        }

	// ��ȡ�ֻ���Ļ�������
	public int getHeight(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		System.out.println("height:" + display.getHeight() + "~~" + "width:"
				+ display.getWidth());
		int height = display.getHeight();
		// Log.e("height:", "" + display.getHeight());
		// Log.e("width:", "" + display.getWidth());
		int a = 0;
		switch (height) {
		case 480:
			// 320x480
			a = 1;
			break;
		case 800:
			// 480x800
			a = 2;
			break;
		case 854:
			// 480x854
			a = 3;
			break;
		case 960:
			// 540x960
			a = 4;
			break;
		case 1280:
			// 720x1280
			a = 5;
			break;
		case 1920:
			//1080x1920
			a = 6;
			break;
		}

		return a;
	}

	// MD5����
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes("utf-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * httpget����
	 * 
	 * @param url
	 * @return xml
	 */
	public String HttpGet(String url) {
		String any_result;
		HttpClient client = new DefaultHttpClient();
		HttpGet gets = new HttpGet(url);
		try {
			HttpResponse response = client.execute(gets);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				any_result = EntityUtils.toString(response.getEntity());
				// System.out.print("any_result:" + any_result);
				return any_result;
			} else {
				showToast("Connnection error! Check your network,please.");
			}
			client.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** ����ͼ�� **/
	public static final void DRAW_BITMAP(Bitmap bit, int x, int y, int frameX,
			int frameY, int dx, int dy, Canvas canvas, Paint paint) {
		/**
		 * Bitmap bit:bitmap���� int x:bitmap��X���� int y��bitmap��Y���� int
		 * frameX��bitmap����֡ frameY:bitmap����֡ dx:xƫ���� dy:yƫ����
		 */
		canvas.save();
		// ���û����������򣨴�С��ÿ֡�Ĵ�С��
		canvas.clipRect(x, y, bit.getWidth() / dx + x, bit.getHeight() / dy + y);
		// ����λͼ
		canvas.drawBitmap(bit, -frameX * bit.getWidth() / dx + x,
				-frameY * bit.getHeight() / dy + y, paint);
		canvas.restore();
	}

	/** �������ײ **/
	public static boolean isCollisionPolygon(int[] xPoints, int[] yPoints,
			int posx, int posy) {
		int i, j;
		int y;
		int miny, maxy;
		int x1, y1;
		int x2, y2;
		int ind1, ind2;
		int ints;
		int[] polyInts;
		int nPoints = xPoints.length;
		if (nPoints < 3) {
			return false;
		}
		polyInts = new int[nPoints];
		miny = yPoints[0];
		maxy = yPoints[0];
		for (i = 1; i < nPoints; i++) {
			if (yPoints[i] < miny) {
				miny = yPoints[i];
			} else if (yPoints[i] > maxy) {
				maxy = yPoints[i];
			}
		}
		for (y = miny; y <= maxy; y++) {
			ints = 0;
			for (i = 0; i < nPoints; i++) {
				if (i == 0) {
					ind1 = nPoints - 1;
					ind2 = 0;
				} else {
					ind1 = i - 1;
					ind2 = i;
				}
				y1 = yPoints[ind1];
				y2 = yPoints[ind2];
				if (y1 < y2) {
					x1 = xPoints[ind1];
					x2 = xPoints[ind2];
				} else if (y1 > y2) {
					y2 = yPoints[ind1];
					y1 = yPoints[ind2];
					x2 = xPoints[ind1];
					x1 = xPoints[ind2];
				} else {
					continue;
				}
				if ((y >= y1) && (y < y2)) {
					polyInts[ints++] = (y - y1) * (x2 - x1) / (y2 - y1) + x1;
				} else if ((y == maxy) && (y > y1) && (y <= y2)) {
					polyInts[ints++] = (y - y1) * (x2 - x1) / (y2 - y1) + x1;
				}
			}
			for (i = polyInts.length; --i >= 0;) {
				for (j = 0; j < i; j++) {
					if (polyInts[j] > polyInts[j + 1]) {
						int T = polyInts[j];
						polyInts[j] = polyInts[j + 1];
						polyInts[j + 1] = T;
					}
				}
			}
			for (i = 0; i < ints; i += 2) {
				if (posy == y) {
					if (posx >= polyInts[i] && posx <= polyInts[i + 1]) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// ��google��������
	public void intentGoogle(String serchString) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, serchString);
		startActivity(intent);
	}

	// �����ҳ
	public void intentBrowser(String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse("http://" + url);
		intent.setData(content_url);
		startActivity(intent);
	}

	// ��ʾ��ͼ
	public void intentMap(String lat, String log) {
		Uri uri = Uri.parse("geo:" + lat + "," + log);
		Intent it = new Intent("android.intent.action.VIEW", uri);
		startActivity(it);
	}

	// ��ʾ��ͼ(�ӱ�ע)
	public void intentMap(String lat, String log, String title) {
		Uri uri = Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q=" + lat
				+ "," + log + "(" + title + ")");
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				& Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");
		startActivity(intent);
	}

	// ����绰
	public void intentCall(String telphone) {
		Uri uri = Uri.parse("tel:" + telphone);
		Intent it = new Intent(Intent.ACTION_DIAL, uri);
		startActivity(it);
	}

	// ���÷����ŵĳ���
	public void intentSMS(String smsText) {
		Intent it = new Intent(Intent.ACTION_VIEW);
		it.putExtra("sms_body", smsText);
		it.setType("vnd.android-dir/mms-sms");
		startActivity(it);
	}

	// �����ʼ�����
	public void intentEmail(String who) {
		Uri uri = Uri.parse("mailto:" + who);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		startActivity(it);
	}

	// post��������
	public void getPost(String username, String phonenumber, String email) {
		String uriAPI = "http://thatsmags.com/shanghai/appinfo";
		/* ����HTTP Post���� */
		HttpPost httpRequest = new HttpPost(uriAPI);
		// Post�������ͱ���������NameValuePair[]���д���
		// ������ ����˻�ȡ�ķ���Ϊrequest.getParameter("name")
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("phone", phonenumber));
		params.add(new BasicNameValuePair("email", email));
		try {
			// ����HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// ȡ��HTTP response
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			// ��״̬��Ϊ200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// ȡ����Ӧ�ִ�
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				if (strResult.equals("1")) {
					showToast("sumbit success");
				} else {
					showToast("sumbit failed");
				}
				// Log.e("post", strResult);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// �ж��Ƿ���Email��ַ
	public boolean isEmail(String strEmail) {
		// String strPattern =
		// "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		String strPattern = "^[0-9a-zA-Z\\-\\_\\.]+@[0-9a-zA-Z\\-\\_]+[\\.]{1}[0-9a-zA-Z]+[\\.]?[0-9a-zA-Z]+$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	// �ж��Ƿ�Ϊ�绰����
	public boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		/*
		 * �ɽ��ܵĵ绰��ʽ��: ^\\(? : ����ʹ�� "(" ��Ϊ��ͷ (\\d{3}): �������������� \\)? : ����ʹ��")"����
		 * [- ]? : ��������ʽ�����ʹ�þ�ѡ���Ե� "-". (\\d{3}) : �ٽ������������� [- ]? : ����ʹ�þ�ѡ���Ե�
		 * "-" ����. (\\d{5})$: ��������ֽ���. ���ԱȽ��������ָ�ʽ: (123)456-7890, 123-456-7890,
		 * 1234567890, (123)-456-7890
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		/*
		 * �ɽ��ܵĵ绰��ʽ��: ^\\(? : ����ʹ�� "(" ��Ϊ��ͷ (\\d{3}): �������������� \\)? : ����ʹ��")"����
		 * [- ]? : ��������ʽ�����ʹ�þ�ѡ���Ե� "-". (\\d{4}) : �ٽ������ĸ����� [- ]? : ����ʹ�þ�ѡ���Ե�
		 * "-" ����. (\\d{4})$: ���ĸ����ֽ���. ���ԱȽ��������ָ�ʽ: (02)3456-7890, 02-3456-7890,
		 * 0234567890, (02)-3456-7890
		 */
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber; /* ����Pattern */
		Pattern pattern = Pattern.compile(expression);
		/* ��Pattern �Բ�������Matcher��Regular expression */
		Matcher matcher = pattern.matcher(inputStr);
		/* ����Pattern2 */
		Pattern pattern2 = Pattern.compile(expression2);
		/* ��Pattern2 �Բ�������Matcher2��Regular expression */
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}

	// ���ַ���ת����Bitmap����
	public Bitmap stringtoBitmap(String string) {
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public void showNotification(int resour, String msg, int id) {
		NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(resour, msg,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// Intent intent = new Intent(this, BaseActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("info", msg);
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, id,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(this, msg, msg, contentIntent);
		notiManager.notify(id, notification);
	}

	// ��Bitmapת�����ַ���
	public String bitmaptoString(Bitmap bitmap) {
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	// ����Ƿ�ʹ��WIFI״̬��
	public boolean isWIFIType() {
		ConnectivityManager manager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = manager.getActiveNetworkInfo();
		String types = netinfo.getTypeName();
		Log.e("type", types);
		if (types.equals("WIFI")) {
			return true;
		} else {
			return false;
		}

	}

	// ��ȡShardPreferences
	public String ReadSharedPreferences() {
		SharedPreferences settings = getSharedPreferences("Test", MODE_APPEND); // ��ȡһ��
																				// SharedPreferences
																				// ����
		// ȡ�������NAME��ȡ�����ֶ�����ֵ���������򴴽�Ĭ��Ϊ��
		String name = settings.getString("name", ""); // ȡ������� NAME
		String password = settings.getString("sex", ""); // ȡ������� PASSWORD
		return name + password;
	}

	// д��ShardPreferences
	public void WriteSharedPreferences() {
		SharedPreferences settings = getSharedPreferences("Test", MODE_APPEND); // ���Ȼ�ȡһ��
																				// SharedPreferences
																				// ����
		settings.edit().putString("name", "cnm").putString("sex", "guess")
				.commit();
	}

	// ������ͼƬ���ظ�Bitmap
	public Bitmap returnBitMap(String url) {

		URL myFileUrl = null;

		Bitmap bitmap = null;

		try {

			myFileUrl = new URL(url);

		} catch (MalformedURLException e) {

			e.printStackTrace();

		}

		try {

			HttpURLConnection conn = (HttpURLConnection) myFileUrl

			.openConnection();

			conn.setDoInput(true);

			conn.connect();

			InputStream is = conn.getInputStream();

			bitmap = BitmapFactory.decodeStream(is);

			is.close();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return bitmap;

	}

	public void shareApps() {
		// ʵ����һ��Intent���󣬲�������Intent��ActionΪACTION_SEND
		Intent intent = new Intent(Intent.ACTION_SEND);
		// ����MIME��������
		intent.setType("text/plain");
		// ��������
		// intent.putExtra(Intent.EXTRA_SUBJECT,
		// DisplayConstants.SHARE_SUBJECT);
		// ��������
		intent.putExtra(Intent.EXTRA_TEXT, "CNM");
		// ����Activity�������ò˵�����
		// startActivity(Intent.createChooser(intent,
		// DisplayConstants.SHARE_TITLE));
		startActivity(intent);
	}

}
