package com.zt.mdm.custom.device.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zt.mdm.custom.device.R;

public class AlertDialogUtils {

    public static AlertDialogUtils getInstance() {
        return new AlertDialogUtils();
    }

  /*  *//**
     * 弹出自定义样式的AlertDialog
     *
     * @param context 上下文
     * @param title   AlertDialog的标题
     * @param tv      点击弹出框选择条目后，要改变文字的TextView
     * @param args    作为弹出框中item显示的字符串数组
     *//*
    public void showAlertDialog(Context context, String title, final TextView tv, final List<String> args) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        dialog.show();

        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_salary, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_alert_dialog_salary);
        ListView list = (ListView) view.findViewById(R.id.lv_alert_dialog_salary);
        tvTitle.setText(title);
       ListAdapter adpter = new ArrayAdapter<String>(context, R.layout.item_listview_salary, R.id.tv_item_listview_salary, args);
        list.setAdapter(adpter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = args.get(position);
                tv.setText(str);
                if (onDialogItemSelectListener != null) {
                    onDialogItemSelectListener.onItemSelect(str);
                }
                dialog.dismiss();
            }
        });

        dialog.getWindow().setContentView(view);
    }*/

    private OnDialogItemSelectListener onDialogItemSelectListener;

    public void setOnDialogItemSelectListener(OnDialogItemSelectListener onDialogItemSelectListener) {
        this.onDialogItemSelectListener = onDialogItemSelectListener;
    }

    /**
     * item选中回调接口
     */
    public interface OnDialogItemSelectListener {
        /**
         * item选中回调方法
         *
         * @param str 选中的item中的String
         */
        void onItemSelect(String str);
    }

    /**
     * 带有确认取消按钮的自定义dialog
     *
     * @param context 上下文
     */
    public static void showConfirmDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.create();
       // alertDialog.setCancelable(false);
       //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                // >= 6.0 没有权限的情况下，如果设置系统级弹窗，会导致崩溃
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // >= 8.0 顶层弹窗
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                // >= 6.0 && <8.0  顶层弹窗
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
        } else {
            // < 6.0 在 AndroidManifest.xml 中申请权限
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        alertDialog.show();

        View view = View.inflate(context, R.layout.view_alert_dialog_confirm, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_message_dialog);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel_dialog);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm_dialog);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickListener.onNegativeButtonClick(alertDialog);
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                           }
        });

        alertDialog.getWindow().setContentView(view);
    }

    private static OnButtonClickListener onButtonClickListener;

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        AlertDialogUtils.onButtonClickListener = onButtonClickListener;
    }

    /**
     * 按钮点击回调接口
     */
    public interface OnButtonClickListener {
        /**
         * 确定按钮点击回调方法
         *
         * @param dialog 当前 AlertDialog，传入它是为了在调用的地方对 dialog 做操作，比如 dismiss()
         *               也可以在该工具类中直接  dismiss() 掉，就不用将 AlertDialog 对象传出去了
         */
        void onPositiveButtonClick(AlertDialog dialog,String transactionId);

        /**
         * 取消按钮点击回调方法
         *
         * @param dialog 当前AlertDialog
         */
        void onNegativeButtonClick(AlertDialog dialog);
    }


}