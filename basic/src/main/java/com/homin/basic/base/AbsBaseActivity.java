package com.homin.basic.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anthony.statuslayout.StatusLayout;
import com.homin.basic.R;
import com.homin.basic.widgets.statusbar.StatusBarUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by Anthony on 2016/4/24.
 * Class Note:
 * 1 all activities implement from this class
 * <p>
 * todo add Umeng analysis
 */
public abstract class AbsBaseActivity extends AppCompatActivity {

    protected static String TAG_LOG = null;// Log tag

    protected Context mContext = null;//context

//    private ActivityComponent mActivityComponent;//dagger2 ActivityComponent

    //    protected Subscription mSubscription;
    protected CompositeSubscription mSubscriptions;

    private Unbinder mUnbinder;

    private StatusLayout mStatusLayout;//global status to define progress/error/content/empty

//    @Inject
//    ToastUtils toastUtils;

//    @Inject
//    EventPosterHelper eventPosterHelper;


//    @Inject
//    DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init(savedInstanceState);

    }

    private void init(Bundle savedInstanceState) {
        mContext = this;
//inject Dagger2 here
//        injectDagger(activityComponent());
//set timber tag
        TAG_LOG = this.getClass().getSimpleName();
//        Timber.tag(TAG_LOG);
//save activities stack
        BaseAppManager.getInstance().addActivity(this);
//rxjava subscriptions,use it like  --->       mSubscriptions.add(subscription)
        mSubscriptions = new CompositeSubscription();

//        if (getLayoutId() != 0)
//            setContentView(getLayoutId());
//set content view support di
        if (getStatusLayoutView() != null) {
            setContentView(getStatusLayoutView());
        }
        //设置状态栏颜色
        setStatusBarColor(getResources().getColor(R.color.app_primary));
//bind this after setContentView
        mUnbinder = ButterKnife.bind(this);

//register EventBus
//        eventPosterHelper.getBus().register(this);
//sample        eventPosterHelper.postEventSafely(xxx);

//init views and events
        initViewsAndEvents(savedInstanceState);
    }


//    public ActivityComponent activityComponent() {
//        if (mActivityComponent == null) {
//            mActivityComponent = DaggerActivityComponent.builder()
//                    .activityModule(new ActivityModule(this))
//                    .applicationComponent(MyApplication.get(this).getAppComponent())
//                    .build();
//        }
//        return mActivityComponent;
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        mSubscriptions.clear();
    }

    @Override
    public void finish() {
        super.finish();
        BaseAppManager.getInstance().removeActivity(this);
    }


    /**
     * use {@link StatusLayout} to  global status of progress/error/content/empty
     *
     * @return
     */
    protected View getStatusLayoutView() {
        mStatusLayout = (StatusLayout) LayoutInflater.from(this).inflate(R.layout.lib_activity_show_status, null);
        if (getContentViewID() != 0) {
//            FrameLayout statusFrameLayout = (FrameLayout) findViewById(R.id.status_frame_content);
            View contentView = LayoutInflater.from(this).inflate(getContentViewID(), null);
            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mStatusLayout.addView(contentView, params);
        }
        return mStatusLayout;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     * <p>
     * using dagger2 in base class：https://github.com/google/dagger/issues/73
     */
//    protected void injectDagger(ActivityComponent activityComponent) {
//        activityComponent.inject(this);
//    }

    /**
     * init views and events here
     */
    protected abstract void initViewsAndEvents(Bundle savedInstanceState);

    /**
     * bind layout resource file
     */
    protected abstract int getContentViewID();


//    protected void showToast(String content) {
//        ToastUtils.getInstance().showToast(content);
//    }


//    protected void showLog(String logInfo) {
//        Timber.i(logInfo);
//    }

    protected void showContent() {
        showContent(null);
    }

    /**
     * Hide all other states and show content
     */
    protected void showContent(List<Integer> skipIds) {
        if (skipIds == null || skipIds.size() == 0) {
            mStatusLayout.showContent();
        } else {
            mStatusLayout.showContent(skipIds);
        }

    }

    /**
     * Hide content and show the progress bar
     */
    protected void showLoading(List<Integer> skipIds) {
        if (skipIds == null || skipIds.size() == 0) {
            mStatusLayout.showLoading();
        } else {
            mStatusLayout.showLoading(skipIds);
        }

    }

    /**
     * Show empty view when there are not data to show
     */
    protected void showEmpty(Drawable emptyDrawable, String emptyTitle, String emptyMessage, List<Integer> skipIds) {
//        if (emptyDrawable == null) {
//            emptyDrawable = new IconDrawable(this, Iconify.IconValue.zmdi_shopping_basket)
//                    .colorRes(android.R.color.white);
//        }
        if (TextUtils.isEmpty(emptyTitle)) {
            emptyMessage = "oops";
        }
        if (TextUtils.isEmpty(emptyMessage)) {
            emptyMessage = "nothing to show";
        }
        if (skipIds == null || skipIds.size() == 0) {
            mStatusLayout.showEmpty(emptyDrawable, emptyTitle, emptyMessage);
        } else {
            mStatusLayout.showEmpty(emptyDrawable, emptyTitle, emptyMessage, skipIds);
        }
    }

    /**
     * Show error view with a button when something
     * goes wrong and prompting the user to try again
     */
    protected void showError(Drawable errorDrawable, String errorTitle,
                             String errorMessage, String errorBtnTxt,
                             List<Integer> skipIds, View.OnClickListener errorClickListener) {
//        if (errorDrawable == null) {
//            errorDrawable = new IconDrawable(this, Iconify.IconValue.zmdi_wifi_off)
//                    .colorRes(android.R.color.white);
//        }
        if (errorClickListener == null) {
            errorClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplication(), "Try again button clicked", Toast.LENGTH_LONG).show();
                }
            };
        }
        if (errorTitle == null) {
            errorTitle = "No Connection";
        }
        if (errorMessage == null) {
            errorMessage = "We could not establish a connection with our servers." +
                    " Please try again when you are connected to the internet.";
        }
        if (errorBtnTxt == null) {
            errorBtnTxt = "Try Again";
        }
        if (skipIds == null || skipIds.size() == 0) {
            mStatusLayout.showError(errorDrawable, errorTitle, errorMessage, errorBtnTxt, errorClickListener);
        } else {
            mStatusLayout.showError(errorDrawable, errorTitle, errorMessage, errorBtnTxt, errorClickListener, skipIds);
        }
    }

//    public DataManager getDataManager() {
//        return mDataManager;
//    }

    public void startActivity(Class<? extends Activity> tarActivity, Bundle options) {
        Intent intent = new Intent(this, tarActivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivity(intent, options);
        } else {
            startActivity(intent);
        }
    }

    public void startActivity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(this, tarActivity);
        startActivity(intent);
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    protected void setStatusBarColor(int color) {

        StatusBarUtil.setColor(this, color);

//        StatusBarUtil.setTransparent(this);

//        StatusBarUtil.setTranslucent(this, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);

//        StatusBarUtil.setTranslucentForImageViewInFragment(UseInFragmentActivity.this, null);

//        StatusBarUtil.setTranslucentForImageView(this, view);
//        StatusBarUtil.setTranslucentForImageView(ImageViewActivity.this, mAlpha, view);

//        StatusBarUtil.setColorForSwipeBack(this, mColor, 38);
    }

    protected void setToolBar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onBackPressedSupport();
                onBackPressed();
            }
        });
    }
}
