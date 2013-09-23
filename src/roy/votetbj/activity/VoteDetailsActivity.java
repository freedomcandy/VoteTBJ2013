package roy.votetbj.activity;

import org.json.JSONException;
import org.json.JSONObject;

import roy.thatsvoteing.tools.Content;
import roy.thatsvoteing.tools.RoyActivity;
import roy.thatsvoteing.tools.VoteListAdapter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VoteDetailsActivity extends RoyActivity implements OnClickListener {
	private TextView txt_votecate, txt_votename, txt_votedesc, txt_address,
			txt_phone, txt_website;
	private ImageView im_voteimage;
	private ImageButton imb_getvote, imb_getprevious, imb_back, imb_next;

	private String address, phone, web;

	private Bitmap bit;

	private String voteResult;

	private Handler cateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 30001:
				txt_votecate.setText(Content.categoryName);
				txt_votename.setText(Content.voteName.get(Content.whichVote));
				txt_votedesc.setText(Content.voteDescription
						.get(Content.whichVote));
				txt_address.setText(address);
				txt_phone.setText(phone);
				txt_website.setText(web);

				im_voteimage.setBackgroundDrawable(convertBitmap2Drawable(bit));

				break;
			case 30002:
				Toast.makeText(VoteDetailsActivity.this, voteResult,
						Toast.LENGTH_LONG).show();
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
			setContentView(R.layout.activity_vtoedetails1280x720);
			break;
		default:
			setContentView(R.layout.activity_vtoedetails);
			break;
		}

		initcontroller();
		if (isInternet()) {
			getResult();
		}
	}

	private void getResult() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = HttpGet(Content.baseURL + "/category/"
						+ Content.cid + "/item/" + Content.vid);
				try {
					parseJSON(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

	private void initcontroller() {
		txt_votecate = (TextView) findViewById(R.id.txtdetailname);

		txt_votename = (TextView) findViewById(R.id.votename);
		txt_votedesc = (TextView) findViewById(R.id.votedesc);
		txt_address = (TextView) findViewById(R.id.address);
		txt_phone = (TextView) findViewById(R.id.phone);
		txt_website = (TextView) findViewById(R.id.web);

		im_voteimage = (ImageView) findViewById(R.id.voteimagez);

		imb_getvote = (ImageButton) findViewById(R.id.getvotes);
		imb_getvote.setOnClickListener(this);

		imb_getprevious = (ImageButton) findViewById(R.id.getprevious);
		imb_getprevious.setOnClickListener(this);

		imb_back = (ImageButton) findViewById(R.id.getback);
		imb_back.setOnClickListener(this);

		imb_next = (ImageButton) findViewById(R.id.getnext);
		imb_next.setOnClickListener(this);

		if (Content.whichVote == 0) {
			imb_getprevious.setVisibility(View.INVISIBLE);
		}
		if (Content.whichVote == Content.voteid.size() - 1) {
			imb_next.setVisibility(View.INVISIBLE);
		}

	}

	private void parseJSON(String json) throws JSONException {
		JSONObject allObject = new JSONObject(json);
		JSONObject venueObject = allObject.getJSONObject("venue");
		JSONObject venueData = venueObject.getJSONObject("venuedata");
		address = venueData.getString("enaddress");
		phone = venueData.getString("phone");
		web = venueData.getString("website");

		bit = getHttpBitmap(Content.votePic.get(Content.whichVote));

		Content.whichScreen = getHeight(VoteDetailsActivity.this);
		Log.e("Roy", "" + Content.whichScreen);

		if (bit == null) {
			bit = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.ic_launcher);
		} else {
			switch (Content.whichScreen) {
			case 2:
				bit = sBitmap(bit, 380, 340);
				break;
			case 5:
				bit = sBitmap(bit, 600, 540);
				break;
			case 6:
				bit = sBitmap(bit, 1600, 1200);
				break;
			default:
				bit = sBitmap(bit, 600, 540);
				break;
			}

		}

		cateHandler.sendEmptyMessage(30001);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.getvotes:
			new Thread(new Runnable() {

				@Override
				public void run() {
					getResultformVote();

				}
			}).start();
			break;
		case R.id.getprevious:
			if (Content.whichVote > 0) {
				Content.whichVote--;
				Content.vid = Content.voteid.get(Content.whichVote);
				getResult();
				imb_next.setVisibility(View.VISIBLE);
			}
			if (Content.whichVote == 0) {
				imb_getprevious.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.getback:
			finish();
			break;
		case R.id.getnext:
			if (Content.whichVote < Content.voteid.size() - 1) {
				Content.whichVote++;
				Content.vid = Content.voteid.get(Content.whichVote);
				getResult();
				imb_getprevious.setVisibility(View.VISIBLE);
			}
			if (Content.whichVote == Content.voteid.size() - 1) {
				imb_next.setVisibility(View.INVISIBLE);
			}
			break;
		}

	}

	private void getResultformVote() {
		String url = "http://awards.thatsmags.com/mobile/vote/"
				+ Content.userID + "/category/" + Content.cid + "/item/"
				+ Content.vid;
		voteResult = HttpGet(url);

		cateHandler.sendEmptyMessage(30002);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 1, 0, "LogOut");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId())// 得到被点击的item的itemId
		{
		case 1: // 对应的ID就是在add方法中所设定的Id
			WriteSharedPreferences("");
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

}
