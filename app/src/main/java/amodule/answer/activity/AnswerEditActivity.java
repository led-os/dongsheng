package amodule.answer.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xianghatest.R;

import java.util.ArrayList;
import java.util.Map;

import acore.tools.StringManager;
import amodule.answer.model.AskAnswerModel;

/**
 * Created by sll on 2017/7/18.
 */

public class AnswerEditActivity extends BaseEditActivity {

    private TextView mQATitleTextView;
    private LinearLayout mAnswerContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(mIsAnswerMore ? "追答" : "我答", R.layout.answer_edit_activity);
    }

    @Override
    protected void initData() {
        super.initData();
        mQAType = mIsAnswerMore ? AskAnswerModel.TYPE_ANSWER_AGAIN : AskAnswerModel.TYPE_ANSWER;
    }

    @Override
    protected void initView(String title, int contentResId) {
        super.initView(title, contentResId);
        mAnswerContainer = (LinearLayout) findViewById(R.id.answer_container);
        mQATitleTextView = (TextView) findViewById(R.id.answer_title);
        if (!TextUtils.isEmpty(mQATitle)) {
            String tempTitle = mQATitle;
            if (tempTitle.length() > 10)
                tempTitle = tempTitle.substring(0, 10) + "...";
            mQATitleTextView.setText(tempTitle);
            mAnswerContainer.setVisibility(View.VISIBLE);
        }
        getLocalData();
    }

    private void getLocalData() {
        loadManager.showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                onLocalDataReady(mSQLite.queryData(mDishCode, mQAType));
            }
        }).start();
    }

    private void onLocalDataReady(final AskAnswerModel model) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadManager.hideProgressBar();
                if (model != null) {
                    mModel = model;
                    mEditText.setText(model.getmText());
                    String imgsStr = model.getmImgs();
                    if (!TextUtils.isEmpty(imgsStr)) {
                        ArrayList<Map<String, String>> imgs = StringManager.getListMapByJson(imgsStr);
                        if (imgs != null && !imgs.isEmpty()) {
                            for (Map<String, String> img : imgs)
                                mImgController.addData(img);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onEditTextChanged(CharSequence s, int start, int before, int count) {
        mCountText.setText(s.length() + "/2000");
        if (s.length() >= 2000)
            Toast.makeText(this, "不能继续输入", Toast.LENGTH_SHORT).show();
    }

}