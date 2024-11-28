package com.example.CQUPT.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.CQUPT.R;

import java.util.List;

public class UsageWarningDialog extends Dialog {
    
    public UsageWarningDialog(Context context, List<AppUsageInfo> appUsageInfoList) {
        super(context);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_usage_warning, null);
        setContentView(view);
        
        // 设置对话框宽度为屏幕宽度的90%
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setAttributes(layoutParams);
        }
        
        TextView messageText = view.findViewById(R.id.message_text);
        SpannableStringBuilder message = new SpannableStringBuilder();
        message.append("今天以下应用的使用时间已超过设定阈值：\n\n");
        
        for (int i = 0; i < appUsageInfoList.size(); i++) {
            AppUsageInfo info = appUsageInfoList.get(i);
            
            // 应用名加粗
            int start = message.length();
            message.append(info.getAppName());
            message.setSpan(new StyleSpan(Typeface.BOLD), start, message.length(), 0);
            
            message.append("：");
            message.append(info.getUsageTime());
            
            // 如果不是最后一个应用，添加换行
            if (i < appUsageInfoList.size() - 1) {
                message.append("\n");
            }
        }
        
        message.append("\n\n要不要休息一下？");
        messageText.setText(message);
        
        Button okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> dismiss());
    }
}
