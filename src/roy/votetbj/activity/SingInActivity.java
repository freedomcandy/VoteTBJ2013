package roy.votetbj.activity;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zxing.activity.CaptureActivity;

import roy.thatsvoteing.tools.Content;
import roy.thatsvoteing.tools.OpenSponers;
import roy.thatsvoteing.tools.RoyActivity;
import roy.thatsvoteing.tools.SponersAdapter;
import roy.votetbj.activity.CategoryListActivity.CateListAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;

public class SingInActivity extends RoyActivity implements OpenSponers {
	private EditText ed_name, ed_email;
	private Gallery gallery_Singn;
	private Button btn_vote;

   private SponersAdapter sa ;
	
	private ArrayList<String> imageURL = new ArrayList<String>();
	private ArrayList<String> imageLink = new ArrayList<String>();
	
	private Handler cateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 30001:
				sa = new SponersAdapter(SingInActivity.this, imageURL,SingInActivity.this);
				gallery_Singn.setAdapter(sa);
				
				break;
			}
			super.handleMessage(msg);
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFullScreen(true);
		Content.whichScreen = getHeight(SingInActivity.this);
		switch (Content.whichScreen) {
		case 5:
			setContentView(R.layout.activity_singin1280x720);
			break;
		default:
			setContentView(R.layout.activity_singin);
			break;
		}
		
			initController();

	}

	private void initController() {
		ed_name = (EditText) findViewById(R.id.ed_name);
		ed_email = (EditText) findViewById(R.id.ed_email);

		btn_vote = (Button) findViewById(R.id.btn_vote);
		btn_vote.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (ed_name.getText().toString().length() > 0
						&& ed_email.getText().toString().length() > 0) {
					if (isEmail(ed_email.getText().toString())) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								getRestul();

							}
						}).start();
					} else {
						showToast("Email is error");
					}
				} else {
					showToast("Email or Name is Null");
				}

			}
		});

		
		
		
		
		if(isInternet()){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						getSponers();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}).start();
		}
		
		
		
		gallery_Singn = (Gallery) findViewById(R.id.gallery);
		
	}
	
	
	private void getSponers() throws JSONException{
		String str = HttpGet(Content.baseURL);
		JSONObject allJSON = new JSONObject(str);
		JSONArray sponsors = allJSON.getJSONArray("sponsors");
		if(sponsors.length()>0){
			for(int i =0 ;i< sponsors.length();i++){
				String imageurl = "http://awards.thatsmags.com/uploads/"+sponsors.getJSONObject(i).getString("logo");
				Log.e("Roy", imageurl);
				imageURL.add(imageurl);
				imageLink.add(sponsors.getJSONObject(i).getString("link"));
				
			}
		}
		
	cateHandler.sendEmptyMessage(30001);

	}
	
	
	
	

	private void getRestul() {
		String email = ed_email.getText().toString();
		String result = HttpGet(Content.baseURL + "/login/"
				+ URLEncoder.encode(email));
		Log.e("Roy", result);
		try {
			parseJSON(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseJSON(String json) throws JSONException {
		JSONObject all = new JSONObject(json);
		Content.userID = all.getString("id");
		WriteSharedPreferences(Content.userID);
		startActivity(SingInActivity.this, CategoryListActivity.class, true);
	}

	public void WriteSharedPreferences(String id) {
		SharedPreferences settings = getSharedPreferences("Test", MODE_APPEND); // 首先获取一个
																				// SharedPreferences
																				// 对象
		settings.edit().putString("name", id).commit();
	}

	@Override
	public void openBrowns(int postion) {
		intentBrowser(imageLink.get(postion));
		
	}

	
	public void intentBrowser(String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		startActivity(intent);
	}
	
	
	

}
