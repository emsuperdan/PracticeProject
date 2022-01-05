package com.example.practiceproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annotation.annotation.TestAnnotation;
import com.example.annotation.annotation.TestClassAnnotation;
import com.example.api.TestApi;

import java.lang.reflect.Method;

/**
 * @author dan.tang
 */
public class MainActivity extends AppCompatActivity {

    private Button button;

    private String mName;
    private String mAge;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test).setOnClickListener(v -> {
            testMethod();

            Log.e("td", "name:" + mName + "  age:" + mAge);
        });

        TestApi.getInstance().get(SecActivity.class).setTheme(getTheme().getResources().newTheme());
        getAnnotationData(MainActivity.class);
    }


    private void getAnnotationData(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            TestAnnotation testAnnotation = method.getAnnotation(TestAnnotation.class);
            if (testAnnotation != null) {
                mName = testAnnotation.name();
                mAge = testAnnotation.age();
            }
        }
    }

    @TestClassAnnotation(path = "123")
    private void testMethod(){

    }
}
