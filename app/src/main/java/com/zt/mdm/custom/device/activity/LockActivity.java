package com.zt.mdm.custom.device.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.zt.mdm.custom.device.R;
import com.zt.mdm.custom.device.util.MdmUtil;
import com.zt.mdm.custom.device.util.StorageUtil;
import com.zt.mdm.custom.device.util.TaskUtil;

import static com.zt.mdm.custom.device.util.AppConstants.DEFAULT_LOCK_MSG;
import static com.zt.mdm.custom.device.util.AppConstants.FLAG_HOMEKEY_DISPATCHED;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK_MSG;
import static com.zt.mdm.custom.device.util.AppConstants.LOCK_MSG_STR;
import static com.zt.mdm.custom.device.util.TaskUtil.startLockReceiver;


/**
 * @author Z T
 * @data 20201015
 */
public class LockActivity extends Activity {
    private static final String TAG = "LockActivity";
    public static LockActivity lockActivity;
    private String lockMsg = DEFAULT_LOCK_MSG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_lock);
        TextView lockTV = findViewById(R.id.lock_TV);
        lockMsg = (String) StorageUtil.get(LOCK_MSG,LOCK_MSG_STR);
        lockTV.setText(lockMsg);
        lockActivity = this;
        findViewById(R.id.phone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MdmUtil.showDialog(getLockActivity());
            }
        });
    }
    public static LockActivity getLockActivity() {
        return lockActivity;
    }
    /**
     * 屏蔽返回键的代码:
     * */
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_CALL:
            case KeyEvent.KEYCODE_SYM:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_STAR:
                return true;
            default:
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onPause() {
        super.onPause();
        startLockReceiver();
    }
    @Override
    public void onAttachedToWindow() {
        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
        super.onAttachedToWindow();
    }

}