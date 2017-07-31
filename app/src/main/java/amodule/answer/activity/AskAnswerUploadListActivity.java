package amodule.answer.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangha.R;

import java.util.ArrayList;
import java.util.Map;

import acore.broadcast.ConnectionChangeReceiver;
import acore.override.XHApplication;
import acore.override.activity.base.BaseActivity;
import acore.tools.ToolsDevice;
import amodule.answer.model.AskAnswerModel;
import amodule.answer.model.AskAnswerUploadAdapter;
import amodule.answer.upload.AskAnswerUploadListPool;
import amodule.dish.view.CommonDialog;
import amodule.upload.UploadListControl;
import amodule.upload.UploadListPool;
import amodule.upload.bean.UploadItemData;
import amodule.upload.bean.UploadPoolData;
import amodule.upload.callback.UploadListUICallBack;
import aplug.basic.ReqInternet;
import xh.windowview.XhDialog;

/**
 * Created by sll on 2017/7/20.
 */

public class AskAnswerUploadListActivity extends BaseActivity {

    private ListView mListView;
    private LinearLayout mAllStart;
    private LinearLayout mAllStop;
    private TextView mUploadStatics;

    private TextView mTitle;
    private TextView mCancelUpload;
    private ImageView mBack;

    private ConnectionChangeReceiver mConnectionChangeReceiver;
    private UploadListPool mListPool;
    private UploadPoolData mUploadPoolData = new UploadPoolData();
    private ArrayList<Map<String, String>> mArrayList = new ArrayList<>();
    private AskAnswerUploadAdapter mAdapter;
    private int mDraftId;
    private String mTitleText;
    private String mTimesStamp;
    private String mCoverPath;
    private String mFinalVideoPath;

    private int mHeaderViewCount;
    private boolean mIsStopUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initActivity("上传列表", 4, 0, R.layout.c_view_bar_title_uploadlist,R.layout.a_dish_upload_list);
        initView();
        initData();
        getData();
        registnetworkListener();
    }

    private void initData() {
        Intent intent = getIntent();
        mDraftId = intent.getIntExtra("draftId", 0);
        mTimesStamp = intent.getStringExtra("time");
        mCoverPath = intent.getStringExtra("coverPath");
        mFinalVideoPath = intent.getStringExtra("finalVideoPath");
    }

    private void registnetworkListener() {
        mConnectionChangeReceiver = new ConnectionChangeReceiver(new ConnectionChangeReceiver.ConnectionChangeListener() {
            @Override
            public void disconnect() {}

            @Override
            public void wifi() {}

            @Override
            public void mobile() {
                if(!mIsStopUpload) {
                    allStartOrPause(false);
                    hintNetWork();
                }
            }
        });
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mConnectionChangeReceiver,filter);
    }

    private void allStartOrPause(boolean isAllStart) {
        mIsStopUpload = !isAllStart;
        mListPool.allStartOrStop(isAllStart ? UploadListPool.TYPE_START : UploadListPool.TYPE_PAUSE);
        mAllStart.setVisibility(isAllStart ? View.GONE : View.VISIBLE);
        mAllStop.setVisibility(isAllStart ? View.VISIBLE : View.GONE);
    }

    private void hintNetWork(){
        final XhDialog xhDialog = new XhDialog(AskAnswerUploadListActivity.this);
        xhDialog.setTitle("当前不是WiFi环境，是否继续上传？")
                .setCanselButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xhDialog.cancel();
                    }
                }).setSureButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xhDialog.cancel();
                allStartOrPause(true);
            }
        }).setSureButtonTextColor("#333333")
                .setCancelButtonTextColor("#333333")
                .show();
    }

    private void getData() {
        if (mDraftId < 1) {
            Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
            super.finish();
            return;
        }

        mListPool = UploadListControl.getUploadListControlInstance()
                .add(AskAnswerUploadListPool.class,
                        mDraftId,mCoverPath,mFinalVideoPath,mTimesStamp, generateUiCallback());
        mUploadPoolData = mListPool.getUploadPoolData();

        AskAnswerModel model = mUploadPoolData.getUploadAskAnswerData();
        if(model == null){
            finish();
            return;
        }

        mTitleText = mUploadPoolData.getTitle();
        mArrayList = mUploadPoolData.getListData();
        mAdapter = new AskAnswerUploadAdapter(this);
        if(!TextUtils.isEmpty(mTitleText)) {
            mTitle.setText(mTitleText.length() > 7 ? mTitleText.substring(0,6) + "..." : mTitleText);
        }

        boolean isAutoUpload = getIntent().getBooleanExtra("isAutoUpload",false);
        if(isAutoUpload) {
            if ("wifi".equals(ToolsDevice.getNetWorkType(this))) {
                allStartOrPause(true);
            } else {
                allStartOrPause(false);
                hintNetWork();
            }
        }else{
            allStartOrPause(false);
        }

        loadManager.changeMoreBtn(ReqInternet.REQ_OK_STRING, -1, -1, 1, true);
        loadManager.setLoading(mListView, mAdapter, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        loadManager.hideProgressBar();

    }

    private UploadListUICallBack generateUiCallback() {
        return new UploadListUICallBack() {
            @Override
            public void changeState() {
                refreshUploadView();
            }

            @Override
            public void changeState(int pos, int index, UploadItemData data) {
            }
            @Override
            public void uploadOver(boolean flag, String responseStr) {
                refreshUploadView();
                mIsStopUpload = !flag;
            }
        };
    }

    private void refreshUploadView() {
        if (mUploadPoolData == null || mAdapter == null) {
            return;
        }
        mArrayList = mUploadPoolData.getListData();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setData(mArrayList);
                setStatisInfo();
            }
        });
    }

    private void setStatisInfo() {
        int success = 0;
        int fail = 0;
        int left = 0;
        for (UploadItemData itemData : mUploadPoolData.getTotalDataList()) {
            if (itemData.getState() == UploadItemData.STATE_FAILD) {
                fail++;
            } else if (itemData.getState() == UploadItemData.STATE_SUCCESS) {
                success++;
            } else {
                left++;
            }
            mUploadStatics.setText("已上传" + success + "，剩余" + left + "，失败" + fail);
        }
    }

    private void initView() {
        findViewById(R.id.all_content).setBackgroundColor(Color.parseColor("#ffffff"));
        mTitle = (TextView) findViewById(R.id.title);
        mCancelUpload = (TextView) findViewById(R.id.tv_cancel_upload);
        mUploadStatics = (TextView) findViewById(R.id.tv_upload_statis);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mListView = (ListView) findViewById(R.id.list_upload);
        mListView.addHeaderView(getHeaderView());
        mHeaderViewCount ++;
        mListView.addFooterView(getFooterView());
        addListener();
    }

    private View getFooterView() {
        return LayoutInflater.from(this)
                .inflate(R.layout.c_upload_footer_item, null);
    }

    private View getHeaderView() {
        View view = LayoutInflater.from(this).inflate(R.layout.c_upload_list_header_item, null);
        mAllStart = (LinearLayout) view.findViewById(R.id.ll_allstart);
        mAllStop = (LinearLayout) view.findViewById(R.id.ll_allstop);
        mAllStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("wifi".equals(ToolsDevice.getNetWorkType(AskAnswerUploadListActivity.this))) {
                    allStartOrPause(true);
                } else {
                    hintNetWork();
                }

            }
        });
        mAllStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allStartOrPause(false);
            }
        });
        return view;
    }

    private void addListener() {
        mCancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnMsg1 = "确定";
                String btnMsg2 = "取消";

                final CommonDialog dialog = new CommonDialog(AskAnswerUploadListActivity.this);
                dialog.setMessage("确定取消上传吗？").setSureButton(btnMsg1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        mListPool.cancelUpload();
                        mAllStart.setVisibility(View.VISIBLE);
                        mAllStop.setVisibility(View.GONE);
                        mIsStopUpload = true;
                        AskAnswerUploadListActivity.this.finish();
                    }
                });
                dialog.setCanselButton(btnMsg2, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> itemMap = mAdapter.getItem(position - mHeaderViewCount);
                if (itemMap == null || itemMap.isEmpty())
                    return;
                if (UploadItemData.STATE_FAILD == Integer.valueOf(itemMap.get("state"))) {
                    mListPool.oneStartOrStop(Integer.valueOf(itemMap.get("pos")),
                            Integer.valueOf(itemMap.get("index")), UploadListPool.TYPE_START);
                }
            }
        });
    }

    private void goBack() {
        if (!mIsStopUpload) {
            Toast.makeText(XHApplication.in().getApplicationContext(), "问答会在后台继续上传", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
        super.onBackPressed();
    }
}
