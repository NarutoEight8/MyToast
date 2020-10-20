package com.naruto.dispatchersample;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naruto.mytoast.ToastConfirmNormal;


public class MainActivity extends AppCompatActivity {
    private TextView txt_result,txt_name;
    private Button btn_start,btn_switch;
    private EditText edit_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_name = findViewById(R.id.txt_name);
        txt_result = findViewById(R.id.txt_result);
        btn_start = findViewById(R.id.btn_start);
        btn_switch = findViewById(R.id.btn_switch);
        edit_input = findViewById(R.id.edit_input);
        txt_name.setText("Activity:"+MainActivity.class.getSimpleName());
    }

    @Override
    protected void onDestroy() {
        Log.e("Main","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_result.setText("结果："+ edit_input.getText().toString().trim());
            }
        });
        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ToastConfirmNormal(MainActivity.this,null)
                        .withTitle("确定要干什么换一边吗？")
                        .withClick(new ToastConfirmNormal.OnButtonClickListener() {
                            @Override
                            public void onClickConfirm(boolean isClickConfirm) {
                                if(!isClickConfirm)return;
                                finish();
                                Intent intent = new Intent(MainActivity.this,ActivityNext.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

}
