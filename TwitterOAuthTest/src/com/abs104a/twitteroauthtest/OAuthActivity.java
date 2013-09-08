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
 * PIN�R�[�h��Twitter�̔F�؂��s��Activity
 * @author ABS104a
 *
 */
public class OAuthActivity extends Activity {

	//�A�N�Z�X�g�[�N��
	private static final String TOKEN = "������ConsumerKey�����";
	//�g�[�N���V�[�N���b�g
	private static final String TOKEN_SECRET = "������ConsumerSecret�����";
	//Twitter
	private Twitter mTwitter = null;
	
	//���N�G�X�g�g�[�N��
	private RequestToken mRequestToken = null;
	
	private final Activity mActivity = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth);

		//�u���E�U���J��Intent���΂��{�^���Ƀ��X�i���Z�b�g����.
		((Button)mActivity.findViewById(R.id.button_showURL))
			.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					new OnOShowURLButtonClickTask().execute();
				}
				
			});
		
		//pin���͌�g�[�N�����擾����{�^���Ƀ��X�i���Z�b�g����D
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
	 * �{�^�����^�b�v����ƁCOAuth�̔F��URL���擾���ău���E�U���N������D
	 * ���X�i
	 * @author ABS104a
	 *
	 */
	public class OnOShowURLButtonClickTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			//Twitter�̃C���X�^���X���擾
			mTwitter = TwitterFactory.getSingleton();
			//ConsumerKey���Z�b�g����D
			mTwitter.setOAuthConsumer(TOKEN, TOKEN_SECRET);
			
			try {
				//���N�G�X�g�g�[�N�����擾
				mRequestToken = mTwitter.getOAuthRequestToken();
				//�F��URL���擾
				final String url = mRequestToken.getAuthorizationURL();
				
				//�u���E�U���N�����邽�߂�intent���쐬�D
				final Intent intent = new Intent(Intent.ACTION_VIEW,
	                     Uri.parse(url));
				//intent���N��
				startActivity(intent);
			} catch (TwitterException e) {
				e.printStackTrace();
			}	
			return null;
		}	
	}
	
	/**
	 * OAuth�{�^�����^�b�v�������ɌĂ΂�郊�X�i
	 * EditText����PIN��ǂݎ��g�[�N�����擾����D
	 * @author ABS104a
	 *
	 */
	public class OnOAuthButtonClickTask extends AsyncTask<Void,Void,AccessToken>{

		@Override
		protected AccessToken doInBackground(Void... params) {
			EditText mEditText = (EditText)mActivity.findViewById(R.id.editText);
			try {
				//�A�N�Z�X�g�[�N�����擾
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
			//�g�[�N���̏����o��(�擾�ɐ��������ꍇ)
			Toast.makeText(mActivity, "Success!", Toast.LENGTH_SHORT).show();
			Toast.makeText(mActivity, result.getToken(), Toast.LENGTH_SHORT).show();
			Toast.makeText(mActivity, result.getTokenSecret(), Toast.LENGTH_SHORT).show();
			//Twitter�N���X�ɃA�N�Z�X�g�[�N�����Z�b�g�i�����Twitter�N���X���g����悤�ɂȂ�j
			mTwitter.setOAuthAccessToken(result);
			
			//Tweet�̑��M(�ʏ�͒ʐM�𔺂����ߕʃX���b�h��œ�����
			//Android4.2�ȍ~�ł̓��C���X���b�h��HTTP�����悤�Ƃ���Ɨ�O�f���悤�ł��D
			//<ex>
			//mTwitter.updateStatus("hogehoge");
			
		}	
		
	}

}
