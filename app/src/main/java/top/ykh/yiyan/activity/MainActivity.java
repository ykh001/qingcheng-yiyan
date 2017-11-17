package top.ykh.yiyan.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Random;

import top.ykh.yiyan.R;
import top.ykh.yiyan.bean.SettingsBean;
import top.ykh.yiyan.database.SQLiteHelper;
import top.ykh.yiyan.service.YiYanService;
import top.ykh.yiyan.thread.BaseRequest;
import top.ykh.yiyan.widget.MissView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private Spinner spinnerMainSelectTime;
    private Spinner spinnerMainSelectType;
    private SwitchCompat toggleMainSaveYiYan;
    private AppCompatButton btMainShowLocalYiYan;
    private NumberPicker numPickerMainSelectNum;
    private TextView textMainDemo;
    private EditText etInputTextColor;
    private Spinner spinnerMainSelectClickEvent;
    private Spinner spinnerMainSelectGravity;
    private SwitchCompat toggleMainTextFrom;
    private SwitchCompat toggleMainAddDot;
    private Button btMainDonate;
    private Button btMainAbout;

    private SettingsBean settingsBean;
    private SQLiteHelper helper;

    private TextView select_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查新版本
//        checkUpdate();
        helper = new SQLiteHelper(this);
        if (AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, MissView.class)).length != 0)
            startService(new Intent(this, YiYanService.class).putExtra("Update", false));
        try {
            settingsBean = helper.getSettingsDao().queryForId(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initView();
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        int versionCode;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "无法获取版本号，检查更新不可用。", Toast.LENGTH_SHORT).show();
            return;
        }
        new BaseRequest(this).setUrl("http://cloud.bmob.cn/c08218628cf8326c/checkUpdate?versionCode=" + versionCode).go(new BaseRequest.RequestListener() {
            @Override
            public void onSuccess(String response) {
                JSONObject resultJson = null;
                try {
                    resultJson = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (resultJson == null) {
                    return;
                }
                String newVersionName = resultJson.optString("newVersionName");
                String updateNote = resultJson.optString("versionReadme");
                final String downloadUrl = resultJson.optString("downloadUrl");
                if (TextUtils.isEmpty(newVersionName) || TextUtils.isEmpty(updateNote) || TextUtils.isEmpty(downloadUrl))
                    return;
                new AlertDialog.Builder(MainActivity.this).setTitle("发现新版本😄").setMessage("本次更新(" + newVersionName + ")：\n\n  " + updateNote).setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gotoWeb(downloadUrl);
                    }
                }).show();
            }
        });
    }

    private void initView() {
        spinnerMainSelectTime = (Spinner) findViewById(R.id.spinner_main_selectTime);
        spinnerMainSelectType = (Spinner) findViewById(R.id.spinner_main_selectType);
        toggleMainSaveYiYan = (SwitchCompat) findViewById(R.id.toggle_main_saveYiYan);
        btMainShowLocalYiYan = (AppCompatButton) findViewById(R.id.bt_main_showLocalYiYan);
        numPickerMainSelectNum = (NumberPicker) findViewById(R.id.numPicker_main_selectNum);
        textMainDemo = (TextView) findViewById(R.id.text_main_demo);
        etInputTextColor = (EditText) findViewById(R.id.et_main_inputColor);
        spinnerMainSelectClickEvent = (Spinner) findViewById(R.id.spinner_main_selectClickEvent);
        spinnerMainSelectGravity = (Spinner) findViewById(R.id.spinner_main_selectGravity);
        toggleMainTextFrom = (SwitchCompat) findViewById(R.id.toggle_main_textFrom);
        toggleMainAddDot = (SwitchCompat) findViewById(R.id.toggle_main_addDot);
        btMainDonate = (Button) findViewById(R.id.bt_main_donate);
        btMainAbout = (Button) findViewById(R.id.bt_main_about);
        select_color = (TextView)findViewById(R.id.select_color);

        numPickerMainSelectNum.setMinValue(9);
        numPickerMainSelectNum.setMaxValue(30);

        btMainShowLocalYiYan.setOnClickListener(this);
        btMainDonate.setOnClickListener(this);
        btMainAbout.setOnClickListener(this);

        spinnerMainSelectTime.setAdapter(getAdapter(R.array.selectRequestLoop));
        spinnerMainSelectType.setAdapter(getAdapter(R.array.selectRequestType));
        spinnerMainSelectClickEvent.setAdapter(getAdapter(R.array.clickEvents));
        spinnerMainSelectGravity.setAdapter(getAdapter(R.array.textGravity));

        spinnerMainSelectType.setOnItemSelectedListener(this);
        spinnerMainSelectType.setOnItemSelectedListener(this);
        spinnerMainSelectClickEvent.setOnItemSelectedListener(this);
        spinnerMainSelectGravity.setOnItemSelectedListener(this);

        toggleMainSaveYiYan.setOnCheckedChangeListener(this);
        toggleMainTextFrom.setOnCheckedChangeListener(this);
        toggleMainAddDot.setOnCheckedChangeListener(this);

        numPickerMainSelectNum.setWrapSelectorWheel(false);
        numPickerMainSelectNum.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                textMainDemo.setTextSize(newVal);
                settingsBean.setTextSize(newVal);
                update();
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opeAdvancenDialog();
            }
        });

        etInputTextColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    try {
                        int color = Color.parseColor("#" + s.toString());
                        settingsBean.setTextColor(color);
                        textMainDemo.setTextColor(color);
                        Toast.makeText(MainActivity.this, "更新颜色成功~（#" + s.toString() + "）", Toast.LENGTH_SHORT).show();
                        update();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "啊哦……颜色值好像不对哦~", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (settingsBean != null) {
            spinnerMainSelectTime.setSelection(settingsBean.getRequestTime());
            spinnerMainSelectType.setSelection(settingsBean.getRequestType());
            toggleMainSaveYiYan.setChecked(settingsBean.isSaveYiYanToLocal());
            numPickerMainSelectNum.setValue(settingsBean.getTextSize());
            textMainDemo.setTextSize(settingsBean.getTextSize());
            spinnerMainSelectClickEvent.setSelection(settingsBean.getClickEvent());
            spinnerMainSelectGravity.setSelection(settingsBean.getTextGravity());

            toggleMainTextFrom.setChecked(settingsBean.isTextFrom());
            toggleMainAddDot.setChecked(settingsBean.isAddTextDot());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_main_showLocalYiYan:
                startActivity(new Intent(this, YiYanHistoryActivity.class));
                break;
            case R.id.bt_main_donate:
                new AlertDialog.Builder(this).setTitle("非常感谢:)").setMessage("非常感谢你的心意，你无需为此捐赠。这是一个开源项目。如果你的手头非常宽裕，可以考虑为OpenSSL™捐款").setPositiveButton("转到OpenSSL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoWeb("https://www.openssl.org/");
                    }
                }).setNeutralButton("项目地址(GitHub)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoWeb("https://github.com/HaizhiH/hongdou/");
                    }
                }).setNegativeButton("取消", null).show();
                break;
            case R.id.bt_main_about:
                if (new Random().nextInt(2) + 1 == 1)
                    Toast.makeText(this, "❤", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "✨", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private ArrayAdapter<String> getAdapter(int arrayId) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(arrayId));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_main_selectType:
                settingsBean.setRequestType(position);
                break;
            case R.id.spinner_main_selectTime:
                settingsBean.setRequestTime(position);
                break;
            case R.id.spinner_main_selectGravity:
                settingsBean.setTextGravity(position);
                break;
            case R.id.spinner_main_selectClickEvent:
                settingsBean.setClickEvent(position);
                break;
        }
        update();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.toggle_main_addDot:
                settingsBean.setAddTextDot(isChecked);
                break;
            case R.id.toggle_main_saveYiYan:
                settingsBean.setSaveYiYanToLocal(isChecked);
                break;
            case R.id.toggle_main_textFrom:
                settingsBean.setTextFrom(isChecked);
                break;
        }
        update();
    }

    private void gotoWeb(String url) {
        if(TextUtils.isEmpty(url))
            return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void update() {
        try {
            helper.getSettingsDao().update(settingsBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //颜色选择器
    public static final int DIALGE_ID = 0;
    public void opeAdvancenDialog() {
        //传入的默认color
        int color = textMainDemo.getCurrentTextColor();
        //设置dialog标题
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color)
                .setDialogTitle(R.string.color_picker)
                //设置为自定义模式  TYPE_CUSTOM和TYPE_PRESETS，默认两种模式都是允许的。
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                //设置有透明度模式，默认没有透明度
                .setShowAlphaSlider(true)
                //设置Id,回调时传回用于判断
                .setDialogId(DIALGE_ID)
                //不显示预知模式
                .setAllowPresets(false)
                //Buider创建
                .create();
        //设置回调，用于获取选择的颜色
        colorPickerDialog.setColorPickerDialogListener(pickerDialogListener);

        colorPickerDialog.show(this.getFragmentManager(), "color-picker-dialog");
    }

    public ColorPickerDialogListener pickerDialogListener = new ColorPickerDialogListener() {
        @Override
        public void onColorSelected(int dialogId, @ColorInt int color) {
            if (dialogId == DIALGE_ID) {
                textMainDemo.setTextColor(color);
                settingsBean.setTextColor(color);
                update();
            }
        }

        @Override
        public void onDialogDismissed(int dialogId) {

        }
    };

}