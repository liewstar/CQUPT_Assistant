package com.example.CQUPT.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.CQUPT.R;

public class UsageWarningDialog extends Dialog {
    
    public UsageWarningDialog(Context context, String appName, String usageTime) {
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
        messageText.setText(String.format("今天已经使用%s %s了，\n要不要休息一下？", appName, usageTime));
        
        Button okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> dismiss());
    }
}
