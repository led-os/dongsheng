package amodule.dish.view;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by Fang Ruijiao on 2017/7/25.
 */
public class DishsWebView extends DishWebView{

    private int index = 0;
    private ArrayList<String> mData;
    private OnWebViewLoadDataCallback mCallBack;

    public DishsWebView(Context context) {
        super(context);
    }

    public DishsWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DishsWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnLoadCallback(OnWebViewLoadDataCallback callback){
        mCallBack = callback;
    }

    @Override
    public void onLoadFinishCallback(String html) {
        super.onLoadFinishCallback(html);
        saveDishData();
        if(mData != null && index < mData.size())
            loadDishData(mData.get(index++));
        if(mCallBack != null) mCallBack.onLoadFinish();
    }

    public void loadDishData(ArrayList<String> data){
        mData = data;
        if(mData.size() > 0){
            loadDishData(mData.get(index++));
        }
    }

    public interface OnWebViewLoadDataCallback{
        public void onLoadFinish();
    }
}