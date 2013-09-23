package roy.votetbj.activity;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import roy.thatsvoteing.tools.Content;
import roy.thatsvoteing.tools.RoyActivity;

public class WelcomActivity extends RoyActivity {
	private GridView gd_welcome;
	private Handler welcomeHandler = new Handler();

	private int[] images = { R.drawable.sponsorone, R.drawable.sponsortwo,
			R.drawable.sponsorthree };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFullScreen(true);
		setContentView(R.layout.activity_welcome);
		initcontroller();
		Content.whichScreen = getHeight(WelcomActivity.this);
		Log.e("Roy", ""+Content.whichScreen);
		welcomeHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				String isLogin = ReadSharedPreferences();
				if (isLogin.equals("")) {
					startActivity(WelcomActivity.this, SingInActivity.class,
							true);
					images = null;
					
				} else {
					Content.userID = isLogin;
					startActivity(WelcomActivity.this, CategoryListActivity.class,
							true);
					images = null;
				}

			}
		}, 2000);
	}

	private void initcontroller() {
		gd_welcome = (GridView) findViewById(R.id.welcomegrid);
		gd_welcome.setAdapter(new WelcomeAdapter());
	}

	class WelcomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return images.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int postion, View view, ViewGroup arg2) {
			view = getLayoutInflater().inflate(R.layout.grid_item, null);
			ImageView im = (ImageView) view.findViewById(R.id.welcome_image);
			Bitmap bit = readBitMap(WelcomActivity.this, images[postion]);
			im.setBackgroundDrawable(convertBitmap2Drawable(bit));
			return view;
		}

	}

	public String ReadSharedPreferences() {
		SharedPreferences settings = getSharedPreferences("Test", MODE_APPEND); // 获取一个
																				// SharedPreferences
																				// 对象
		// 取出保存的NAME，取出改字段名的值，不存在则创建默认为空
		String name = settings.getString("name", ""); // 取出保存的 NAME
		return name;
	}

}
