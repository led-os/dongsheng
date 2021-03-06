package amodule.main.view.circle;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiangha.R;

import java.util.ArrayList;

import acore.tools.StringManager;
import acore.tools.Tools;
import third.ad.BannerAd;
import third.ad.scrollerAd.XHAllAdControl;

import static third.ad.tools.AdPlayIdConfig.MAIN_CIRCLE_TITLE;

/**
 * PackageName : amodule.main.view.circle
 * Created by MrTrying on 2016/8/24 17:43.
 * E_mail : ztanzeyu@gmail.com
 */
public class CircleHeaderAD extends LinearLayout {
    private String stiaticID = "";
    private ImageView adImageView;
    private String mAdType;
    public CircleHeaderAD(Context context) {
        this(context, null, 0);
    }

    public CircleHeaderAD(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleHeaderAD(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.a_circle_header_ad, this);
        setOrientation(VERTICAL);
    }

    XHAllAdControl xhAllAdControl;

    //生活圈首页顶部
    public void init(Activity activity, BannerAd.OnBannerListener listener) {
        adImageView = (ImageView) findViewById(R.id.ad_banner_item_iv_single);
        ArrayList<String> list = new ArrayList<>();
        list.add(MAIN_CIRCLE_TITLE);
        xhAllAdControl = new XHAllAdControl(list, activity, "community_top");
        xhAllAdControl.start((isRefresh,map) -> {
            if (xhAllAdControl == null) {
                return;
            }
            if (map.containsKey(MAIN_CIRCLE_TITLE)) {
                BannerAd bannerAdBurden = new BannerAd(activity, xhAllAdControl, adImageView);
                map = StringManager.getFirstMap(map.get(MAIN_CIRCLE_TITLE));
                mAdType = map.get("type");
                bannerAdBurden.setOnBannerListener(new BannerAd.OnBannerListener() {
                    @Override
                    public void onShowAd() {
                        if (listener != null)
                            listener.onShowAd();
                    }

                    @Override
                    public void onClickAd() {
                        if (listener != null)
                            listener.onClickAd();
                    }

                    @Override
                    public void onImgShow(int imgH) {
                        if (listener != null)
                            listener.onImgShow(imgH);
                    }
                });
                bannerAdBurden.onShowAd(map);
            }
        });
        xhAllAdControl.registerRefreshCallback();
    }

    public void onAdBind() {
        xhAllAdControl.onAdBind(0, adImageView, "");
    }

    public String getAdType() {
        return mAdType;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h != 0 && h != oldh) {
            post(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.blank_view).setVisibility(View.VISIBLE);
                }
            });
        } else if (h == Tools.getDimen(getContext(), R.dimen.dp_10)) {
            findViewById(R.id.blank_view).setVisibility(View.GONE);
        }
    }

    public String getStiaticID() {
        return stiaticID;
    }

    public void setStiaticID(String stiaticID) {
        this.stiaticID = stiaticID;
    }
}
