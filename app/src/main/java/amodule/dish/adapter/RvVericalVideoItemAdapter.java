package amodule.dish.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiangha.R;

import java.util.List;

import acore.widget.rvlistview.adapter.BaseAdapter;
import acore.widget.rvlistview.holder.RvBaseViewHolder;
import amodule.dish.video.module.ShortVideoDetailModule;
import amodule.dish.view.ShortVideoItemView;

/**
 * item adapter
 */
public class RvVericalVideoItemAdapter extends BaseAdapter<ShortVideoDetailModule, RvVericalVideoItemAdapter.ItemViewHolder<ShortVideoDetailModule>> {

    private ItemViewHolder<ShortVideoDetailModule> mCurrentViewHolder;
    private ShortVideoItemView.AttentionResultCallback mAttentionResultCallback;

    private ShortVideoItemView.GoodResultCallback mGoodResultCallback;

    public RvVericalVideoItemAdapter(Context context, @Nullable List<ShortVideoDetailModule> data) {
        super(context, data);
    }
    @Override
    public ItemViewHolder<ShortVideoDetailModule> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_short_video_parent, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public void onViewRecycled(ItemViewHolder<ShortVideoDetailModule> holder) {
            holder.stopVideo();
    }

    @Override
    public boolean onFailedToRecycleView(ItemViewHolder<ShortVideoDetailModule> holder) {
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder<ShortVideoDetailModule> holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    public void setAttentionResultCallback(ShortVideoItemView.AttentionResultCallback attentionResultCallback) {
        mAttentionResultCallback = attentionResultCallback;
    }

    public void setGoodResultCallback(ShortVideoItemView.GoodResultCallback goodResultCallback) {
        mGoodResultCallback = goodResultCallback;
    }

    public class ItemViewHolder<T extends ShortVideoDetailModule> extends RvBaseViewHolder<T>{
        private ShortVideoItemView shortVideoItemView;
        public ShortVideoDetailModule data;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            shortVideoItemView= itemView.findViewById(R.id.videoItem);
        }

        @Override
        public void bindData(int position, @Nullable T data) {
            itemView.setTag(position);
            this.data = data;
            shortVideoItemView.setData(data,position);
            shortVideoItemView.setAttentionResultCallback(new ShortVideoItemView.AttentionResultCallback() {
                @Override
                public void onResult(boolean success) {
                    if (mAttentionResultCallback != null) {
                        mAttentionResultCallback.onResult(success);
                    }
                }
            });
            shortVideoItemView.setGoodResultCallback(new ShortVideoItemView.GoodResultCallback() {
                @Override
                public void onResult(boolean success) {
                    if (mGoodResultCallback != null) {
                        mGoodResultCallback.onResult(success);
                    }
                }
            });
        }

        public int getPlayState() {
            if (shortVideoItemView != null)
                return shortVideoItemView.getPlayState();
            return -1;
        }

        public void startVideo() {
            shortVideoItemView.prepareAsync();
        }

        public void resumeVideo() {
            shortVideoItemView.resumeVideo();
        }

        public void pauseVideo() {
            shortVideoItemView.pauseVideo();
        }

        public void stopVideo() {
            shortVideoItemView.releaseVideo();
        }

        public void gotoUser() {
            shortVideoItemView.gotoUser();
        }

        public void updateShareNum() {
            shortVideoItemView.updateShareNum();
        }

        public void updateLikeState() {
            shortVideoItemView.updateLikeState();
        }

        public void updateLikeNum() {
            shortVideoItemView.updateLikeNum();
        }

        public void updateCommentNum() {
            shortVideoItemView.updateCommentNum();
        }

        public void updateAttentionState() {
            shortVideoItemView.updateAttentionState();
        }

        public void updateFavoriteState() {
            shortVideoItemView.updateFavoriteState();
        }
    }

    public void notifyGotoUser() {
        if (mCurrentViewHolder != null) {
            mCurrentViewHolder.gotoUser();
        }
    }

    public ItemViewHolder<ShortVideoDetailModule> getCurrentViewHolder() {
        return mCurrentViewHolder;
    }

    public void setCurrentViewHolder(ItemViewHolder<ShortVideoDetailModule> currentViewHolder) {
        mCurrentViewHolder = currentViewHolder;
    }

    public void onResume() {
        if (mCurrentViewHolder != null) {
            mCurrentViewHolder.resumeVideo();
        }
    }

    public void onPause() {
        if (mCurrentViewHolder != null) {
            mCurrentViewHolder.pauseVideo();
        }
    }

    public void onDestroy() {
        if (mCurrentViewHolder != null) {
            mCurrentViewHolder.stopVideo();
        }
    }

    public int getPlayState() {
        if (mCurrentViewHolder != null) {
            return mCurrentViewHolder.getPlayState();
        }
        return -1;
    }
}
