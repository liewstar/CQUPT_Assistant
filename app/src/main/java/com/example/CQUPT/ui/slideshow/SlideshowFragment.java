package com.example.CQUPT.ui.slideshow;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.CQUPT.databinding.FragmentLoadClassesBinding;
import com.example.CQUPT.ui.HttpUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class SlideshowFragment extends Fragment {

    private FragmentLoadClassesBinding binding;
    private SlideshowViewModel slideshowViewModel;
    private File currentIcsFile;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentLoadClassesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.importCalendarButton.setOnClickListener(v -> importCalendar());
        binding.shareButton.setOnClickListener(v -> shareCalendar());

        return root;
    }

    private void importCalendar() {
        String studentId = binding.studentIdInput.getText().toString();
        if (studentId.isEmpty()) {
            showMessage("请输入学号");
            return;
        }

        String icsContent = getIcsContentFromApi(studentId);
        if (icsContent != null) {
            try {
                saveAndOpenIcsFile(icsContent, studentId, true);
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("导入日历失败");
            }
        } else {
            showMessage("获取日历数据失败");
        }
    }

    private void shareCalendar() {
        String studentId = binding.studentIdInput.getText().toString();
        if (studentId.isEmpty()) {
            showMessage("请输入学号");
            return;
        }

        String icsContent = getIcsContentFromApi(studentId);
        if (icsContent != null) {
            try {
                saveAndOpenIcsFile(icsContent, studentId, false);
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("分享失败");
            }
        } else {
            showMessage("获取日历数据失败");
        }
    }

    private void saveAndOpenIcsFile(String icsContent, String studentId, boolean importToCalendar) throws IOException {
        // 使用getExternalCacheDir()来存储文件，这样其他应用也能访问
        File cacheDir = requireContext().getExternalCacheDir();
        if (cacheDir != null) {
            currentIcsFile = new File(cacheDir, studentId + "的课表.ics");
            FileOutputStream fos = new FileOutputStream(currentIcsFile);
            fos.write(icsContent.getBytes());
            fos.close();

            Uri contentUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    currentIcsFile);

            if (importToCalendar) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(contentUri, "text/calendar");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "选择日历应用"));
            } else {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/calendar");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "分享课表"));
            }
        } else {
            throw new IOException("无法访问外部存储");
        }
    }

    private String getIcsContentFromApi(String studentId) {
        String url = "http://8.137.36.93:3000/" + studentId + ".ics";
        return HttpUtil.get(url);
    }

    private void showMessage(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}