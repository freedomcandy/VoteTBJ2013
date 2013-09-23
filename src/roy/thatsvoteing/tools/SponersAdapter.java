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

public class SponersAdapter extends BaseAdapter{

	public OpenSponers  os;//´ò¿ªÔÞÖúµÄÍøÕ¾
	
	
	private static final String TAG = "LoaderAdapter";
	private boolean mBusy = false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}
	
	
	


	private ImageLoader mImageLoader;

	private Context mContext;
	private ArrayList<String> urlArrays;

	private ArrayList<View> tempView = new ArrayList<View>();

	public SponersAdapter(Context context,ArrayList<String> url,OpenSponers oss) {
		this.mContext = context;
		urlArrays = url;
		this.os = oss;
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
					R.layout.sponers_item, null);

			viewHolder = new ViewHolder();
			viewHolder.mSponersImage = (ImageView) convertView.findViewById(R.id.sponers_image);

			convertView.setClickable(true);

			convertView.setBackgroundColor(Color.TRANSPARENT);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String url = "";
		url = urlArrays.get(position);
   
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				os.openBrowns(position);
				
			}
		});
		

		if (!mBusy) {
			mImageLoader.DisplayImage(url, viewHolder.mSponersImage, false);
		} else {
			mImageLoader.DisplayImage(url, viewHolder.mSponersImage, false);
		}
		return convertView;
	}
	
	

	
	
	
	
	static class ViewHolder {
		ImageView mSponersImage;
	}




	
}
