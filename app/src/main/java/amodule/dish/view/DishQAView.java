package amodule.dish.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiangha.R;

import java.util.ArrayList;
import java.util.Map;

import acore.logic.AppCommon;
import acore.logic.LoginManager;
import acore.logic.XHClick;
import acore.override.helper.XHActivityManager;
import acore.override.view.ItemBaseView;
import acore.tools.StringManager;
import acore.tools.Tools;
import amodule.comment.activity.CommentActivity;
import amodule.dish.activity.DetailDish;
import amodule.user.activity.login.LoginByAccout;
import aplug.imageselector.ImgWallActivity;

/**
 * 菜谱详情页问答
 */
public class DishQAView extends ItemBaseView{
    private ImageView auther_userImg;
    private TextView text_user,text_answer,text_degree,text_time;
    private LinearLayout qa_content_linear;
    private Map<String,String> maptemp,mapuser;
    public DishQAView(Context context) {
        super(context, R.layout.view_dish_qa);
    }

    public DishQAView(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.view_dish_qa);
    }

    public DishQAView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, R.layout.view_dish_qa);
    }
    @Override
    public void init() {
        super.init();
        auther_userImg = (ImageView) findViewById(R.id.auther_userImg);
        text_user= (TextView) findViewById(R.id.text_user);
        text_answer= (TextView) findViewById(R.id.text_answer);
        text_degree= (TextView) findViewById(R.id.text_degree);
        text_time= (TextView) findViewById(R.id.text_time);
        qa_content_linear= (LinearLayout) findViewById(R.id.qa_content_linear);
    }

    /**
     * 处理用户信息
     */
    public void setUserMap(Map<String,String> mapuser){
        this.mapuser = mapuser;
        setViewImage(auther_userImg,mapuser,"img");
        findViewById(R.id.cusType).setVisibility(mapuser.containsKey("isGourmet")&& mapuser.get("isGourmet").equals("2")?View.VISIBLE:View.GONE);
        text_user.setText(mapuser.get("nickName"));
        auther_userImg.setOnClickListener(onClickListener);
        text_user.setOnClickListener(onClickListener);

    }
    /**
     *用户点击跳转页面
     */
    private View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            XHClick.mapStat(XHActivityManager.getInstance().getCurrentActivity(), DetailDish.tongjiId_detail, "问答", "点击作者头像");
            AppCommon.openUrl(XHActivityManager.getInstance().getCurrentActivity(), mapuser.get("url"),true);
        }
    };

    /**
     * 处理列表信息
     * @param list
     */
    public void setData(ArrayList<Map<String,String>> list){
        maptemp= list.get(0);
        qa_content_linear.removeAllViews();
        text_answer.setText(maptemp.get("answerNum"));
        if(maptemp.containsKey("satisfyRateIsShow")&&"2".equals(maptemp.get("satisfyRateIsShow"))) {
            text_degree.setText(maptemp.get("satisfyRate"));
            findViewById(R.id.degree_linear).setVisibility(View.VISIBLE);
        }else findViewById(R.id.degree_linear).setVisibility(View.GONE);
        text_time.setText(maptemp.get("avgRespondTime"));
        boolean isShowCount=false;
        if(maptemp.containsKey("count")&& !TextUtils.isEmpty(maptemp.get("count"))&&Integer.parseInt(maptemp.get("count"))>0){
            this.findViewById(R.id.qa_more_linaer).setVisibility(View.VISIBLE);
            isShowCount = true;
        }else  this.findViewById(R.id.qa_more_linaer).setVisibility(View.GONE);
        ArrayList<Map<String,String>> listQA = StringManager.getListMapByJson(maptemp.get("list"));
        if(listQA!=null&&listQA.size()>0){
            for(int i=0;i<listQA.size();i++){
                final Map<String,String> mapQA= listQA.get(i);
                final int index = i;
                View qaItem=LayoutInflater.from(context).inflate(R.layout.view_dish_qa_item,null);
                TextView content_one= (TextView) qaItem.findViewById(R.id.content_one);
                TextView content_two= (TextView) qaItem.findViewById(R.id.content_two);
                content_one.setText(getClickableSpan(mapQA.get("text"),mapQA));
                content_one.setMovementMethod(LinkMovementMethod.getInstance());//必须设置否则无效
                content_two.setText(mapQA.get("useRate")+"%觉的有用");
                qaItem.findViewById(R.id.qa_line).setVisibility(i==listQA.size()-1&&!isShowCount?View.GONE:View.VISIBLE);
                if(mapQA.containsKey("isAttend")&&"1".equals(mapQA.get("isAttend"))) {
                    qaItem.findViewById(R.id.money_linear).setVisibility(View.VISIBLE);
                    ((TextView)qaItem.findViewById(R.id.money_qa)).setText(mapQA.get("peekMoney")+"元偷看");
                    qaItem.findViewById(R.id.money_linear).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!LoginManager.isLogin()){
                                context.startActivity(new Intent(context, LoginByAccout.class));
                                return;
                            }
                            XHClick.mapStat(XHActivityManager.getInstance().getCurrentActivity(), DetailDish.tongjiId_detail, "问答", "点击第"+index+"条问答");
                            AppCommon.openUrl(XHActivityManager.getInstance().getCurrentActivity(),mapQA.get("link"),false);
                        }
                    });
                }
                content_one.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!LoginManager.isLogin()){
                            context.startActivity(new Intent(context, LoginByAccout.class));
                            return;
                        }
                        XHClick.mapStat(XHActivityManager.getInstance().getCurrentActivity(), DetailDish.tongjiId_detail, "问答", "点击第"+index+"条问答");
                        AppCommon.openUrl(XHActivityManager.getInstance().getCurrentActivity(),mapQA.get("link"),false);
                    }
                });

                qa_content_linear.addView(qaItem);
            }
        }
        this.findViewById(R.id.qa_more_linaer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maptemp!= null&&maptemp.containsKey("moreQaLink")){
                    XHClick.mapStat(XHActivityManager.getInstance().getCurrentActivity(), DetailDish.tongjiId_detail, "问答", "点击【更多问答】");
                    AppCommon.openUrl(XHActivityManager.getInstance().getCurrentActivity(),maptemp.get("moreQaLink"),false);
                }
            }
        });
    }

    private SpannableString getClickableSpan(String content, final Map<String,String> maps) {
        boolean isShowIm= false;
        content="问 "+content;
        if(maps.containsKey("isHaveImg")&&"2".equals(maps.get("isHaveImg")))isShowIm=true;
        if(isShowIm)content+=" 图";
        SpannableString spanableInfo = new SpannableString(content);
        //处理问图片
        Drawable d = getResources().getDrawable(R.drawable.dish_qa_icon);
        d.setBounds(1, 1, Tools.getDimen(context,R.dimen.dp_19), Tools.getDimen(context,R.dimen.dp_19));
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        spanableInfo.setSpan( span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

       //处理图片数据
        if(isShowIm) {
            Drawable bitmap = getResources().getDrawable(R.drawable.dish_qa_bitmap);
            bitmap.setBounds(1, 1, Tools.getDimen(context, R.dimen.dp_18), Tools.getDimen(context, R.dimen.dp_18));
            ImageSpan span_bitmap = new ImageSpan(bitmap, ImageSpan.ALIGN_BASELINE);
            spanableInfo.setSpan(span_bitmap, content.length() - 1, content.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            View.OnClickListener bitSpanClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   ArrayList<Map<String,String>> maplist = StringManager.getListMapByJson(maps.get("imgs"));
                    if(maplist!=null&&maplist.size()>0) {
                        ArrayList<String> data= new ArrayList<>();
                        for(Map<String,String> str:maplist){
                            data.add(str.get(""));
                        }
                        Intent intent = new Intent(context, ImgWallActivity.class);
                        intent.putStringArrayListExtra("images", data);
                        intent.putExtra("index", 0);
                        context.startActivity(intent);
                    }
                }
            };
            spanableInfo.setSpan(new Clickable(bitSpanClick), content.length() - 1, content.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanableInfo;
    }
    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener mListener) {
            this.mListener = mListener;
        }
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }
}
