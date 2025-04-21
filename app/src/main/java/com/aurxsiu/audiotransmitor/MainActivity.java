package com.aurxsiu.audiotransmitor;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView selectedTextView;
    private List<OptionData> options;
    private OptionAdapter optionAdapter;
    private PopupWindow popupWindow;
    private FileHelper fileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedTextView = findViewById(R.id.selectedTextView);
        selectedTextView.setOnClickListener(v -> showOptionPopup());
        fileHelper = new FileHelper(this);

        options = new ArrayList<>(loadOptions());
    }

    private List<OptionData> loadOptions() {
        return fileHelper.getIpLog();
    }

    private void showOptionPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_option_list, null);
        RecyclerView recyclerView = popupView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        optionAdapter = new OptionAdapter(options, new OptionAdapter.Listener() {
            @Override
            public void onItemClick(OptionData item) {
                onItemSelected(item);
                popupWindow.dismiss();
            }

            @Override
            public void onEditClick(OptionData item) {
                showAddOrEditDialog(item);
            }
        });
        recyclerView.setAdapter(optionAdapter);

        popupView.findViewById(R.id.addNewButton).setOnClickListener(v -> {
            showAddOrEditDialog(null);
        });

        popupWindow = new PopupWindow(popupView, selectedTextView.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAsDropDown(selectedTextView);
    }

    private void showAddOrEditDialog(@Nullable OptionData existing) {
        AddOrEditDialog dialog = new AddOrEditDialog(this, existing, (content, remark) -> {
            if (existing == null) {
                OptionData newOption = new OptionData(content, remark);
                options.add(0,newOption);
                onItemAdded(content, remark);
            } else {
                onItemEdited(existing, content, remark);
                existing.content = content;
                existing.remark = remark;
            }
            optionAdapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    private void onItemSelected(OptionData item) {
        selectedTextView.setText(item.toString());
        Toast.makeText(this, "选中: " + item.toString(), Toast.LENGTH_SHORT).show();
        ArrayList<OptionData> ipLog = fileHelper.getIpLog();
        fileHelper.setIpFirst(item);
        new Connector().getConnected(item.content);
    }

    private void onItemAdded(String content, String remark) {
        if(new InetAddressValidator().isValid(content)){
            fileHelper.addIpToLog(new OptionData(content,remark));
            Toast.makeText(this, "添加新项: " + content + ", " + remark, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Warn:添加失败!"+content+"格式可能不正确",Toast.LENGTH_LONG).show();
        }
    }

    private void onItemEdited(OptionData oldData, String newContent, String newRemark) {
        Toast.makeText(this, "修改项: " + oldData.toString() + " → " + newContent + "（" + newRemark + "）", Toast.LENGTH_SHORT).show();
        fileHelper.replaceIpLog(oldData,new OptionData(newContent,newRemark));
    }
}