package roy.thatsvoteing.tools;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import roy.ancylist.cache.ImageLoader;
import roy.votetbj.activity.R;
import roy.votetbj.activity.VoteDetailsActivity;
import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VoteListAdapter extends BaseAdapter{

	public CallToast  ct;
	
	
	private static final String TAG = "LoaderAdapter";
	private boolean mBusy = false;
	private String voteResult;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}
	
	
	


	private ImageLoader mImageLoader;

	private Activity activity;
	private Context mContext;
	private ArrayList<String> ids;
	private ArrayList<String> urlArrays;
	private ArrayList<String> titles;
	private ArrayList<String> desc;

	private ArrayList<View> tempView = new ArrayList<View>();

	public VoteListAdapter(Activity mactivity, Context context,
			ArrayList<String> id, ArrayList<String> url,
			ArrayList<String> title, ArrayList<String> descs,CallToast cts) {
		this.mContext = context;
		urlArrays = url;
		this.activity = mactivity;
		this.ids = id;
		this.titles = title;
		this.desc = descs;
		this.ct = cts;
		mImageLoader = new ImageLoader(context);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	@Override
	public int getCount() {
		return urlArrays.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);

			viewHolder = new ViewHolder();
			viewHolder.mVoteName = (TextView) convertView
					.findViewById(R.id.votename);
			viewHolder.mVoteImage = (ImageView) convertView
					.findViewById(R.id.vote_image);
			viewHolder.mVoteDes = (TextView) convertView
					.findViewById(R.id.votedes);
			viewHolder.mVote = (ImageButton) convertView
					.findViewById(R.id.getvote);
			viewHolder.mVote.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final ScaleAnimation animation =new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, 
							Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
							animation.setDuration(500);//设置动画持续时间
					v.startAnimation(animation);
//					final TranslateAnimation animation = new TranslateAnimation(0, 150,0, 0); 
//					animation.setDuration(2000);//设置动画持续时间 
//					animation.setRepeatCount(2);//设置重复次数 
//					v.startAnimation(animation);
					
				Content.vid = ids.get(position);
				  new Thread(new Runnable() {
					
					@Override
					public void run() {
						getResult();
						
					}
				}).start();

				}
			});

			convertView.setClickable(true);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					tempView.add(v);
					for (int i = 0; i < tempView.size(); i++) {
						tempView.get(i).setBackgroundColor(color.transparent);
					}
					v.setBackgroundColor(Color.rgb(65, 233, 243));
//					Log.e("Roy", "click:" + position);

					Content.whichVote = position;
					Content.vid = ids.get(position);
					Intent it = new Intent();
					it.setClass(activity, VoteDetailsActivity.class);
					activity.startActivity(it);
					activity.finish();

				}
			});

			convertView.setBackgroundColor(Color.TRANSPARENT);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String url = "";
		url = urlArrays.get(position);
//		Log.e("Roy", url);

		// viewHolder.mVoteImage.setImageResource(R.drawable.ic_launcher);

		if (!mBusy) {
			mImageLoader.DisplayImage(url, viewHolder.mVoteImage, false);
			viewHolder.mVoteName.setText(titles.get(position));
			viewHolder.mVoteDes.setText(desc.get(position));
//			Log.e("Roy", "Busy");
		} else {
			mImageLoader.DisplayImage(url, viewHolder.mVoteImage, false);
			viewHolder.mVoteName.setText(titles.get(position));
			viewHolder.mVoteDes.setText(desc.get(position));
//			Log.e("Roy", "mBusy");
		}
		return convertView;
	}
	
	
	private void getResult(){
		String url = "http://awards.thatsmags.com/mobile/vote/"
				+ Content.userID + "/category/" + Content.cid
				+ "/item/" + Content.vid;
		voteResult = HttpGet(url);
		ct.getVoteResult(voteResult);
	
		
	}

	
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
			}
			client.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	static class ViewHolder {
		TextView mVoteName;
		ImageView mVoteImage;
		TextView mVoteDes;
		ImageButton mVote;
	}




	
}
