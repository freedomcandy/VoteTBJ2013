package roy.votetbj.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.zxing.activity.CaptureActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import roy.thatsvoteing.tools.CallToast;
import roy.thatsvoteing.tools.Content;
import roy.thatsvoteing.tools.RoyActivity;
import roy.thatsvoteing.tools.VoteListAdapter;
import roy.votetbj.activity.CategoryListActivity.CateListAdapter;

public class VoteListActivity extends RoyActivity implements CallToast {
	private TextView txt_categoryname;
	private ListView list_vote;

	private String voteMessage;

	private ArrayList<String> cids = new ArrayList<String>();
	private ArrayList<String> vid = new ArrayList<String>();
	private VoteListAdapter vla;

	private Handler cateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 30001:
				vla = new VoteListAdapter(VoteListActivity.this,
						VoteListActivity.this, Content.voteid, Content.votePic,
						Content.voteName, Content.voteDescription,
						VoteListActivity.this);
				list_vote.setAdapter(vla);
				ListAnim(list_vote);

				break;

			case 30002:
				showToast(voteMessage);
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFullScreen(true);
		switch (Content.whichScreen) {
		case 5:
			setContentView(R.layout.activity_votelist1280x720);
			break;
		default:
			setContentView(R.layout.activity_votelist);
			break;
		}

		initController();
		if (isInternet()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					String result = HttpGet(Content.baseURL + "/category/"
							+ Content.cid);
					if (result.length() > 0) {
						try {
							parseJSON(result);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}).start();
		}
	}

	private void initController() {
		txt_categoryname = (TextView) findViewById(R.id.listtitlename);
		txt_categoryname.setText(Content.categoryName);
		list_vote = (ListView) findViewById(R.id.votelist);
	}

	private void parseJSON(String json) throws JSONException {

		// if (Content.voteName.size() > 0) {
		// for (int i = 0; i < Content.voteName.size(); i++) {
		// Content.voteName.remove(i);
		// Content.votePic.remove(i);
		// Content.voteDescription.remove(i);
		// Content.voteid.remove(i);
		// }
		// }

		Content.voteName = null;
		Content.voteName = new ArrayList<String>();
		Content.votePic = null;
		Content.votePic = new ArrayList<String>();
		Content.voteDescription = null;
		Content.voteDescription = new ArrayList<String>();
		Content.voteid = null;
		Content.voteid = new ArrayList<String>();

		JSONObject allJSON = new JSONObject(json);
		JSONArray items = allJSON.getJSONArray("items");
		if (items.length() > 0) {
			for (int i = 0; i < items.length(); i++) {
				Content.voteid.add(items.getJSONObject(i).getString("id"));
				cids.add(items.getJSONObject(i).getString("cat_id"));
				vid.add(items.getJSONObject(i).getString("vid"));
				JSONObject venueObject = items.getJSONObject(i).getJSONObject(
						"venue");
				Content.voteName.add(venueObject.getString("envenue"));
				String pic = "http://www.thatsmags.com/uploads/"
						+ venueObject.getString("coverpic");
				Content.votePic.add(pic);
				Content.voteDescription.add(venueObject
						.getString("description"));
			}
		}
		cateHandler.sendEmptyMessage(30001);
	}

	@Override
	public void getVoteResult(String voteResult) {
		voteMessage = voteResult;
		cateHandler.sendEmptyMessage(30002);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 1, 0, "LogOut");
		menu.add(1, 2, 0, "QRScaner");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId())// 得到被点击的item的itemId
		{
		case 1: // 对应的ID就是在add方法中所设定的Id
			WriteSharedPreferences("");
			break;
		case 2:
			Intent openCameraIntent = new Intent(VoteListActivity.this,CaptureActivity.class);
			startActivityForResult(openCameraIntent, 0);
			break;
		}
		return true;
	}

	public void WriteSharedPreferences(String id) {
		SharedPreferences settings = getSharedPreferences("Test", MODE_APPEND); // 首先获取一个
																				// SharedPreferences
																				// 对象
		settings.edit().putString("name", id).commit();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//处理扫描结果（在界面上显示）
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			Log.e("Roy", scanResult);
			intentBrowser(scanResult);
		}
	}
	
	public void intentBrowser(String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		startActivity(intent);
	}
	
}
