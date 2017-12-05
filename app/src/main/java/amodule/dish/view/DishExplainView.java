package amodule.dish.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiangha.R;

import java.util.Map;

import acore.override.helper.XHActivityManager;
import acore.override.view.ItemBaseView;


/**
 * 小贴士
 */
public class DishExplainView extends ItemBaseView {
    private LinearLayout mAdLayout;
    private DishAdDataViewNew dishAdDataView;
    public DishExplainView(Context context) {
        super(context, R.layout.view_dish_explain);
    }

    public DishExplainView(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.view_dish_explain);
    }
    public DishExplainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, R.layout.view_dish_explain);
    }
    @Override
    public void init() {
        super.init();
        mAdLayout = (LinearLayout)findViewById(R.id.a_dish_detail_ad);
        dishAdDataView = new DishAdDataViewNew(context,R.layout.view_dish_tips_ad_layout_distance);
    }
    public void setAdData(){
        dishAdDataView.getRequest(XHActivityManager.getInstance().getCurrentActivity(), mAdLayout);
    }
    public void setData(final Map<String,String> maps){
        TextView explain_content_tv= (TextView) findViewById(R.id.explain_content_tv);
        if(maps.containsKey("remark")&& !TextUtils.isEmpty(maps.get("remark"))){
            explain_content_tv.setText(maps.get("remark"));
            findViewById(R.id.tv_explain).setVisibility(View.VISIBLE);
            explain_content_tv.setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.tv_explain).setVisibility(View.GONE);
            explain_content_tv.setVisibility(View.GONE);
        }
    }
}
