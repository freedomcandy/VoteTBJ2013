package roy.votetbj.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import roy.thatsvoteing.tools.Content;
import roy.thatsvoteing.tools.RoyActivity;

public class CategoryListActivity extends RoyActivity {
	private ListView ls_categoryList;
	private ArrayList<String> ids = new ArrayList<String>();
	private ArrayList<String> cids = new ArrayList<String>();
	private ArrayList<String> titles = new ArrayList<String>();
	private ArrayList<String> descriptions = new ArrayList<String>();

	private CateListAdapter ca;

	private Handler cateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 30001:
				ca = new CateListAdapter();
				ls_categoryList.setAdapter(ca);
				ListAnim(ls_categoryList);
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFullScreen(true);
		setContentView(R.layout.activity_category);
		initController();
		if (isInternet()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					String result = HttpGet(Content.baseURL);
					try {
						parseJSON(result);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private void initController() {
		ls_categoryList = (ListView) findViewById(R.id.categorylist);

		ls_categoryList.setFocusable(false);

		ls_categoryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int which,
					long arg3) {
				Content.cid = ids.get(which);
				Content.categoryName = titles.get(which);
				startActivity(CategoryListActivity.this,
						VoteListActivity.class, false);

			}
		});

	}

	private void parseJSON(String json) throws JSONException {
		JSONObject allJSON = new JSONObject(json);
		JSONArray categories = allJSON.getJSONArray("categories");
		if (categories.length() > 0) {
			for (int i = 0; i < categories.length(); i++) {
				ids.add(categories.getJSONObject(i).getString("id"));
				cids.add(categories.getJSONObject(i).getString("main_id"));
				titles.add(categories.getJSONObject(i).getString("title")
						.toUpperCase());
				descriptions.add(categories.getJSONObject(i).getString(
						"description"));
			}
		}

		cateHandler.sendEmptyMessage(30001);
	}

	class CateListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ids.size();
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
			view = getLayoutInflater().inflate(R.layout.category_list_item,
					null);
			TextView txt_name = (TextView) view.findViewById(R.id.cname);
			txt_name.setText(titles.get(postion));

			TextView txt_description = (TextView) view
					.findViewById(R.id.cdescription);
			txt_description.setText(descriptions.get(postion));

			return view;
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 1, 0, "LogOut");
		menu.add(1, 2, 0, "QRScaner");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId())// �õ��������item��itemId
		{
		case 1: // ��Ӧ��ID������add���������趨��Id
			WriteSharedPreferences("");
			break;
		case 2:
			Intent openCameraIntent = new Intent(CategoryListActivity.this,CaptureActivity.class);
			startActivityForResult(openCameraIntent, 0);
			break;
		}
		return true;
	}
	
	public void WriteSharedPreferences(String id) {
		SharedPreferences settings = getSharedPreferences("Test", MODE_APPEND); // ���Ȼ�ȡһ��
																				// SharedPreferences
																				// ����
		settings.edit().putString("name", id).commit();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//����ɨ�������ڽ�������ʾ��
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
