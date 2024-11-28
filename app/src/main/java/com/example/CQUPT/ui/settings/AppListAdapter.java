package com.example.CQUPT.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.CQUPT.R;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    private final List<AppNotificationSettingsActivity.AppInfo> apps;
    private final OnAppSelectedListener listener;

    public interface OnAppSelectedListener {
        void onAppSelected(String packageName, boolean isChecked);
    }

    public AppListAdapter(List<AppNotificationSettingsActivity.AppInfo> apps, OnAppSelectedListener listener) {
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view, (packageName, isChecked) -> {
            int position = getPositionByPackageName(packageName);
            if (position != -1) {
                apps.get(position).isSelected = isChecked;
                listener.onAppSelected(packageName, isChecked);
            }
        });
    }

    private int getPositionByPackageName(String packageName) {
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).packageName.equals(packageName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotificationSettingsActivity.AppInfo app = apps.get(position);
        holder.bind(app);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView appName;
        final TextView packageName;
        final CheckBox checkbox;
        private String currentPackageName;
        private final OnAppSelectedListener listener;

        ViewHolder(View view, OnAppSelectedListener listener) {
            super(view);
            this.listener = listener;
            appName = view.findViewById(R.id.appName);
            packageName = view.findViewById(R.id.packageName);
            checkbox = view.findViewById(R.id.checkbox);

            // 在ViewHolder构造时设置一次监听器
            view.setOnClickListener(v -> checkbox.setChecked(!checkbox.isChecked()));
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (currentPackageName != null) {
                    listener.onAppSelected(currentPackageName, isChecked);
                }
            });
        }

        void bind(AppNotificationSettingsActivity.AppInfo app) {
            // 暂时移除监听器
            checkbox.setOnCheckedChangeListener(null);
            
            currentPackageName = app.packageName;
            appName.setText(app.appName);
            packageName.setText(app.packageName);
            checkbox.setChecked(app.isSelected);

            // 重新设置监听器
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (currentPackageName != null) {
                    listener.onAppSelected(currentPackageName, isChecked);
                }
            });
        }
    }
}
