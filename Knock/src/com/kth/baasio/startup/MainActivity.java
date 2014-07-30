
package com.kth.baasio.startup;

import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.kakao.template.loginbase.SampleLoginActivity;
import com.kakao.template.loginbase.SampleSignupActivity;
import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.callback.BaasioSignInCallback;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.user.BaasioUser;
import com.kth.baasio.exception.BaasioException;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;







import java.util.UUID;

public class MainActivity extends Activity {

	private String TAG = "MainActivity";
	private TextView lblEmail;

	private UUID savedUuid;
	private com.kakao.widget.LoginButton kakaoLoginButton;
	private final SessionCallback mySessionCallback = new MySessionStatusCallback();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		lblEmail = (TextView) findViewById(R.id.lblEmail);
		kakaoLoginButton = (com.kakao.widget.LoginButton) findViewById(R.id.com_kakao_login);
		
		kakaoLoginButton.setLoginSessionCallback(mySessionCallback);

		LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
		authButton.setOnErrorListener(new OnErrorListener() {

			@Override
			public void onError(FacebookException error) {
				Log.i(TAG, "Error " + error.getMessage());
			}
		});
		
		
		// set permission list, Don't forget to add email
		authButton.setReadPermissions(Arrays.asList("email"));
		// session state call back event
		authButton.setSessionStatusCallback(new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state, Exception exception) {

				if (session.isOpened()) {
					
					BaasioUser.signUpViaFacebookInBackground(getApplicationContext(), session.getAccessToken(), new BaasioSignInCallback() {
						
						@Override
						public void onException(BaasioException e) {
							// 실패
							Log.i(TAG,"페북 가입 실패");
						}
						
						@Override
						public void onResponse(BaasioUser response) {
							// 성공
							String name = response.getUsername();
							Log.i(TAG,"페북 가입 성공");
						}
					});
					
					Log.i(TAG,"Access Token"+ session.getAccessToken());
					Request.executeMeRequestAsync(session,
							new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,Response response) {
							if (user != null) { 
								Log.i(TAG,"User ID "+ user.getId());
								Log.i(TAG,"Email "+ user.asMap().get("email"));
								lblEmail.setText(user.asMap().get("email").toString());
							}
						}
					});
				}
			}
		});
		
		
	}
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        // 세션을 초기화 한다
	        if(com.kakao.Session.initializeSession(this, mySessionCallback)){
	            // 1. 세션을 갱신 중이면, 프로그레스바를 보이거나 버튼을 숨기는 등의 액션을 취한다
	        	kakaoLoginButton.setVisibility(View.GONE);
	        } else if (com.kakao.Session.getCurrentSession().isOpened()){
	            // 2. 세션이 오픈된된 상태이면, 다음 activity로 이동한다.
	            onSessionOpened();
	        }
	            // 3. else 로그인 창이 보인다.
	    }
	
	 private class MySessionStatusCallback implements SessionCallback {
	        @Override
	        public void onSessionOpened() {
	            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
	            MainActivity.this.onSessionOpened();
	        }

	        @Override
	        public void onSessionClosed(final KakaoException exception) {
	            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈을 못했으니 다시 로그인 버튼 노출.
	            kakaoLoginButton.setVisibility(View.VISIBLE);
	        }
	    }
	  protected void onSessionOpened(){
	        final Intent intent = new Intent(MainActivity.this, SampleSignupActivity.class);
	        startActivity(intent);
	        finish();
	    }
	
	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	     super.onActivityResult(requestCode, resultCode, data);
	     Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	 }
	 
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
