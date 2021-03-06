package amodule.user.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import acore.widget.rvlistview.adapter.RvBaseAdapter;
import acore.widget.rvlistview.holder.RvBaseViewHolder;
import amodule.user.view.module.ModuleItemS0View;

/**
 * ModuleS的标准adapter
 */
public class AdapterModuleS0 extends RvBaseAdapter<Map<String, String>> {
    private String statisticId = "";//统计id
    public AdapterModuleS0(Context context, @Nullable List<Map<String, String>> data) {
        super(context, data);
    }
    @Override
    public RvBaseViewHolder<Map<String, String>> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModuleS0ViewHolder(new  ModuleItemS0View(mContext));
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(getItemType(position));
    }

    public String getItemType(int position) {
        return "0";
    }
    /**
     * 模块数据
     */
    public class ModuleS0ViewHolder extends RvBaseViewHolder<Map<String, String>> {
        ModuleItemS0View view;
        public ModuleS0ViewHolder(ModuleItemS0View view) {
            super(view);
            this.view = view;
        }
        @Override
        public void bindData(int position, @Nullable Map<String, String> data) {
            if (view != null) {
                view.setStatisticId(getStatisticId());
                view.initData(data);
            }
        }
    }

    public String getStatisticId() {
        return statisticId;
    }

    public void setStatisticId(String statisticId) {
        this.statisticId = statisticId;
    }
}
