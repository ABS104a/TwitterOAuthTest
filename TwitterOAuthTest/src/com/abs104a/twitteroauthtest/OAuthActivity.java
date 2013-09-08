package com.abs104a.twitteroauthtest;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * PINコードでTwitterの認証を行うActivity
 * @author ABS104a
 *
 */
public class OAuthActivity extends Activity {

	//アクセストークン
	private static final String TOKEN = "ここにConsumerKeyを入力";
	//トークンシークレット
	private static final String TOKEN_SECRET = "ここにConsumerSecretを入力";
	//Twitter
	private Twitter mTwitter = null;
	
	//リクエストトークン
	private RequestToken mRequestToken = null;
	
	private final Activity mActivity = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth);

		//ブラウザを開くIntentを飛ばすボタンにリスナをセットする.
		((Button)mActivity.findViewById(R.id.button_showURL))
			.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					new OnOShowURLButtonClickTask().execute();
				}
				
			});
		
		//pin入力後トークンを取得するボタンにリスナをセットする．
		((Button)mActivity.findViewById(R.id.button))
			.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					new OnOAuthButtonClickTask().execute();
				}
				
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.oauth, menu);
		return true;
	}
	
	/**
	 * ボタンをタップすると，OAuthの認証URLを取得してブラウザを起動する．
	 * リスナ
	 * @author ABS104a
	 *
	 */
	public class OnOShowURLButtonClickTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			//Twitterのインスタンスを取得
			mTwitter = TwitterFactory.getSingleton();
			//ConsumerKeyをセットする．
			mTwitter.setOAuthConsumer(TOKEN, TOKEN_SECRET);
			
			try {
				//リクエストトークンを取得
				mRequestToken = mTwitter.getOAuthRequestToken();
				//認証URLを取得
				final String url = mRequestToken.getAuthorizationURL();
				
				//ブラウザを起動するためのintentを作成．
				final Intent intent = new Intent(Intent.ACTION_VIEW,
	                     Uri.parse(url));
				//intentを起動
				startActivity(intent);
			} catch (TwitterException e) {
				e.printStackTrace();
			}	
			return null;
		}	
	}
	
	/**
	 * OAuthボタンをタップした時に呼ばれるリスナ
	 * EditTextからPINを読み取りトークンを取得する．
	 * @author ABS104a
	 *
	 */
	public class OnOAuthButtonClickTask extends AsyncTask<Void,Void,AccessToken>{

		@Override
		protected AccessToken doInBackground(Void... params) {
			EditText mEditText = (EditText)mActivity.findViewById(R.id.editText);
			try {
				//アクセストークンを取得
				return mTwitter.getOAuthAccessToken(mRequestToken,mEditText.getText().toString());
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(AccessToken result) {
			super.onPostExecute(result);
			if(result == null) return;
			//トークンの書き出し(取得に成功した場合)
			Toast.makeText(mActivity, "Success!", Toast.LENGTH_SHORT).show();
			Toast.makeText(mActivity, result.getToken(), Toast.LENGTH_SHORT).show();
			Toast.makeText(mActivity, result.getTokenSecret(), Toast.LENGTH_SHORT).show();
			//Twitterクラスにアクセストークンをセット（これでTwitterクラスが使えるようになる）
			mTwitter.setOAuthAccessToken(result);
			
			//Tweetの送信(通常は通信を伴うため別スレッド上で動かす
			//Android4.2以降ではメインスレッドでHTTP投げようとすると例外吐くようです．
			//<ex>
			//mTwitter.updateStatus("hogehoge");
			
		}	
		
	}

}
