package com.example.tangzhifeng.paperairplane.detailedpager.guoke;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.example.tangzhifeng.paperairplane.R;
import com.example.tangzhifeng.paperairplane.data.guoke.GuoKe;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GuokeDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.web_view)
    WebView webView;
    GuoKeDetailPresenter mGuokePresenter;
    GuoKeDetailPresenter.NoAdWebViewClient mNoadvWebViewClient;
    @InjectView(R.id.image_top)
    SimpleDraweeView imageTop;
    @InjectView(R.id.ProgressBar_load)
    ProgressBar ProgressBarLoad;
    GuoKe mGuoKe;
    @InjectView(R.id.share_id)
    FloatingActionButton shareId;
    @InjectView(R.id.floatingActionButton_back)
    FloatingActionButton floatingActionButtonBack;
    @InjectView(R.id.coord)
    CoordinatorLayout coord;
    @InjectView(R.id.scrollView_id)
    ScrollView scrollViewId;

    /**
     * 整个Activity的根视图
     */
    private View decorView;
    /**
     * 手指按下的坐标
     */
    private float Xdown, Ydown;

    /**
     * 手机屏幕的宽度和高度
     */
    private float screenWidth, screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        Transition transition = getWindow().getSharedElementEnterTransition();
//        getWindow().setEnterTransition(transition);
//        getWindow().setExitTransition(new Slide());
        setContentView(R.layout.activity_guoke_datail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        initMyViewGroup();
        measureScreen();
        mGuokePresenter = new GuoKeDetailPresenter();
        Intent intent = this.getIntent();
        LoadDetail(intent);
        floatingActionButtonBack.setOnClickListener(this);
        shareId.setOnClickListener(this);

    }

    private void initMyViewGroup(){
         coord = new CoordinatorLayout(coord.getContext()){
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                boolean intercepted = false;
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                switch (ev.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        intercepted = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (decorView.isInTouchMode())
                        {
                            intercepted = true;
                        }
                        else {
                            intercepted = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        intercepted = false;
                        break;
                }
                return intercepted;
            }

             @Override
             public boolean onTouchEvent(MotionEvent ev) {
                 mGuokePresenter.SildingTouchEvent(decorView, ev, Xdown,Ydown, screenWidth, GuokeDetailActivity.this);
                 return super.onTouchEvent(ev);
             }
         };

    }
    private void measureScreen() {
        decorView = getWindow().getDecorView();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    private void LoadDetail(Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
        mGuoKe = (GuoKe) bundle.getSerializable("guoke");
        mGuokePresenter.LoadWeb(
            mGuokePresenter.GetWebUrl(mGuoKe), webView, ProgressBarLoad);
        imageTop.setImageURI(mGuokePresenter.GetDetailTopIcon(mGuoKe));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Xdown = ev.getX();
                Ydown = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float xMove1 = ev.getX() - Xdown;
                float ymove1 = ev.getY() - Ydown;
                if (xMove1 > ymove1&&(ymove1>Ydown-20||ymove1<Ydown+20)) {
                    mGuokePresenter.SildingTouchEvent(decorView, ev, Xdown,Ydown, screenWidth, GuokeDetailActivity.this);
                }
                break;
            case MotionEvent.ACTION_UP:
                float xMove = ev.getX() - Xdown;
                float ymove = ev.getY() - Ydown;
                if (xMove > ymove) {
                    mGuokePresenter.SildingTouchEvent(decorView, ev, Xdown,Ydown, screenWidth, GuokeDetailActivity.this);
//                    return true;
                }

        }
        return super.dispatchTouchEvent(ev);
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mGuokePresenter.SildingTouchEvent(decorView, event, Xdown, screenWidth, GuokeDetailActivity.this);
//
//        return super.onTouchEvent(event);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO 自动生成的方法存根
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {           //当webview不是处于第一页面时，返回上一个页面
                webView.goBack();
                return true;
            } else {                              //当webview处于第一页面时,直接退出程序
//                System.exit(0);
            }


        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton_back:
                finish();
                break;
            case R.id.share_id:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");// setType("audio/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "share");
                intent.putExtra(Intent.EXTRA_TEXT, "此处是要分享的内容");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}