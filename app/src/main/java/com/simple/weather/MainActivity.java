package com.simple.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.simple.weather.model.Casts;
import com.simple.weather.model.Forecasts;
import com.simple.weather.model.Lives;
import com.simple.weather.model.TimeWeather;
import com.simple.weather.model.Weather;
import com.simple.weather.utils.HttpClient;
import com.simple.weather.utils.WeatherUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String cityName;
    private String adcode;
    private LinearLayout relativeLayout;
    private LinearLayout linearLayout;
    private TextView headerLabel;
    private TextView temperatureLabel;
    private TextView posttimeLabel;
    private TextView windLabel;
    private TextView windDirectionLabel;
    private TextView weatherLabel;
    private ImageView weatherImage;
    private Button switchBtn;
    private Button refreshBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21)
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        initView();
        initData();
    }

    private void initView(){
        relativeLayout = findViewById(R.id.activity_main);
        linearLayout = findViewById(R.id.main_weather_info_layout);
        headerLabel = findViewById(R.id.main_header_label);
        weatherImage = findViewById(R.id.main_weather_image);
        weatherLabel = findViewById(R.id.main_weather_info);

        windDirectionLabel = findViewById(R.id.main_weather_direction);
        windLabel = findViewById(R.id.main_weather_wind);
        posttimeLabel = findViewById(R.id.main_weather_posttime);
        temperatureLabel = findViewById(R.id.main_wearher_temperature);

        switchBtn = findViewById(R.id.change_btn);
        refreshBtn = findViewById(R.id.refresh_btn);
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SelectCityActivity.class);
                startActivity(intent);
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.HOUR_OF_DAY) > 18 || now.get(Calendar.HOUR_OF_DAY) < 7){
            relativeLayout.setBackgroundResource(R.drawable.dark);
        }else {
            relativeLayout.setBackgroundResource(R.drawable.light);
        }
    }

    private void initData(){
        Intent intent = getIntent();
        cityName = intent.getStringExtra(SplashActivity.CITYNAME);
        adcode = intent.getStringExtra(SplashActivity.ADCODE);
        headerLabel.setText(cityName);
        HttpClient.query(adcode, HttpClient.WEATHER_TYPE_BASE, Weather.class, new HttpClient.IHttpCallback() {
            @Override
            public <T> void onSuccess(T result, boolean isSuccess) {
                if(isSuccess){
                     Weather weather = (Weather)result;
                    if (weather.getInfo().equals("OK") && weather.getCount().equals("1")){
                        final Lives info = weather.getLives().get(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                temperatureLabel.setText(info.getTemperature());            //温度
                                posttimeLabel.setText(format(info.getReporttime()) + "更新");//更新时间
                                windDirectionLabel.setText(info.getWinddirection());        //风向
                                windLabel.setText("湿度" + info.getHumidity() + "%");         //湿度
                                if (WeatherUtils.WeatherKV.containsKey(info.getWeather())){
                                    weatherLabel.setText(info.getWeather());
                                    weatherImage.setImageResource(WeatherUtils.WeatherKV.get(info.getWeather()));
                                }else {
                                    temperatureLabel.setText("N/A");
                                }
                                Toast.makeText(MainActivity.this,"更新数据成功",Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                temperatureLabel.setText("服务不可用");
                                Toast.makeText(MainActivity.this, "服务不可用", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            temperatureLabel.setText("无法提供天气信息");
                            Toast.makeText(MainActivity.this, "无法提供天气信息", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private String format(String posttime){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(posttime);

            return new SimpleDateFormat("HH:MM").format(date);
        } catch (ParseException e) {
            return "刚刚更新";
        }
    }

    private String getDay(String date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date toDate = dateFormat.parse(date);

            return new SimpleDateFormat("dd").format(toDate);
        } catch (ParseException e) {
            return "N/A";
        }
    }

    private String getWeek(String week){
        switch (week)
        {
            case "1":
                return "星期一";
            case "2":
                return "星期二";
            case "3":
                return "星期三";
            case "4":
                return "星期四";
            case "5":
                return "星期五";
            case "6":
                return "星期六";
            default:
                return "星期日";
        }
    }
}
