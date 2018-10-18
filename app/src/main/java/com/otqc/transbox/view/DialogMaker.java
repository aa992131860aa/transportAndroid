package com.otqc.transbox.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.util.DisplayUtil;
import com.otqc.transbox.util.JStringKit;


/**
 * 对app的所有对话框进行管理
 * 
 * @author blue
 */
@SuppressLint("InflateParams")
public class DialogMaker
{

	public interface DialogCallBack
	{
		/**
		 * 对话框按钮点击回调
		 * 
		 * @param position
		 *            点击Button的索引.
		 * @param tag
		 */
		public void onButtonClicked(Dialog dialog, int position, Object tag);

		/**
		 * 当对话框消失的时候回调
		 * 
		 * @param tag
		 */
		public void onCancelDialog(Dialog dialog, Object tag);
	}

	public static final String BIRTHDAY_FORMAT = "%04d-%02d-%02d";

	/**
	 * 创建一个通用的alert对话框
	 * 
	 * @param title
	 *            对话框的标题。为null或""没有标题
	 * @param msg
	 *            对话框要显示内容
	 * @param btns
	 *            显示按钮的标题。为null没有按钮
	 * @param callBack
	 *            点击按钮的回调
	 * @param isCanCancelabel
	 *            是否可以点击back键消失对话框
	 * @param isDismissAfterClickBtn
	 *            点击任何按钮后是否消失对话框
	 * @param tag
	 * 
	 * @return 显示并返回对话框
	 */
	public static Dialog showCommonAlertDialog(Context context, String title, String msg, String[] btns, final DialogCallBack callBack, boolean isCanCancelabel, final boolean isDismissAfterClickBtn, final Object tag)
	{
		final Dialog dialog = new Dialog(context, R.style.DialogNoTitleStyleTranslucentBg);
		View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_alert_common_layout, null);
		TextView contentTv = (TextView) contentView.findViewById(R.id.dialog_content_tv);
		TextView titleTv = (TextView) contentView.findViewById(R.id.dialog_title_tv);
		OnClickListener lis = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (null != callBack)
				{
					callBack.onButtonClicked(dialog, (Integer) v.getTag(), tag);
				}
				if (isDismissAfterClickBtn)
				{
					dialog.dismiss();
				}
			}

		};
		if (null != btns)
		{
			int len = btns.length;
			if (len > 0)
			{
				LinearLayout btnLayout = (LinearLayout) contentView.findViewById(R.id.btn_layout);
				Button btn;
				View LineView;
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, DisplayUtil.dip2px(context, 44));
				params.weight = 1.0f;
				for (int i = 0; i < len; i++)
				{
					btn = new Button(context);
					btn.setText(btns[i]);
					btn.setTag(i);
					btn.setSingleLine(true);
					btn.setEllipsize(TruncateAt.END);
					btn.setOnClickListener(lis);

					btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.dip2px(context, 16));
					btn.setTextColor(0xff007cff);
					btn.setGravity(Gravity.CENTER);
					if (0 == i && 1 == len)
					{
						btn.setBackgroundResource(R.drawable.alert_single_btn_selector);
					} else if (0 == i)
					{
						btn.setBackgroundResource(R.drawable.alert_left_btn_selector);
					} else if (i > 0 && len > 2)
					{
						btn.setBackgroundResource(R.drawable.alert_mid_btn_selector);
					} else if (len - 1 == i)
					{
						btn.setBackgroundResource(R.drawable.alert_right_btn_selector);
					}
					btn.setPadding(10, 10, 10, 10);
					btnLayout.addView(btn, params);
					LineView = new View(context);
					LineView.setBackgroundColor(Color.parseColor("#b2b2b2"));
					if (i < len - 1 && len > 1)
					{
						btnLayout.addView(LineView, new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
					}
				}
			}

		}

		if (JStringKit.isNotBlank(title))
		{
			setDialogTextViewContent(context, title, titleTv);
			titleTv.setVisibility(View.VISIBLE);
		} else if (JStringKit.isNotBlank(msg))
		{
			// 若标题为空 而内容不为空 内容按标题格式显示
			title = msg;
			msg = null;
			setDialogTextViewContent(context, title, titleTv);
			titleTv.setVisibility(View.VISIBLE);
		} else
		{
			contentTv.setVisibility(View.GONE);
			titleTv.setVisibility(View.GONE);
		}

		MarginLayoutParams mParams = (MarginLayoutParams) titleTv.getLayoutParams();
		if (JStringKit.isNotBlank(msg))
		{
			final int margin = DisplayUtil.dip2px(context, 21.33f);
			mParams.topMargin = margin;
			mParams.bottomMargin = 0;
			titleTv.setLayoutParams(mParams);
			setDialogTextViewContent(context, msg, contentTv);
			contentTv.setVisibility(View.VISIBLE);
		} else if (JStringKit.isNotBlank(title))
		{
			final int margin = DisplayUtil.dip2px(context, 38.67f);
			mParams.topMargin = margin;
			mParams.bottomMargin = margin;
			titleTv.setLayoutParams(mParams);
			contentTv.setVisibility(View.GONE);
		}

		contentView.requestLayout();

		dialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{

				if (null != callBack)
				{
					callBack.onCancelDialog((Dialog) dialog, tag);
				}
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(isCanCancelabel);
		dialog.setContentView(contentView);
		// 设置对话框宽度
		Window window = dialog.getWindow();
		WindowManager.LayoutParams aWmLp = window.getAttributes();
		aWmLp.width = App.get().screenW - 100;
		aWmLp.gravity = Gravity.CENTER;
		window.setAttributes(aWmLp);

		dialog.show();
		return dialog;
	}

	// 设置文字信息
	public static void setDialogTextViewContent(Context context, String content, TextView tView)
	{
		if (null == tView || TextUtils.isEmpty(content))
		{
			return;
		}

		String NEW_LINE = System.getProperty("line.separator");
		if (content.contains(NEW_LINE) || content.contains("\n"))
		{
			tView.setGravity(Gravity.CENTER);
		} else
		{
			float destWidth = JStringKit.getContentWidth(content, tView);
			float maxWidth = DisplayUtil.dip2px(context, 235.33f);
			if (destWidth >= Math.round(maxWidth - 0.5f))
			{
				tView.setGravity(Gravity.LEFT);
			} else
			{
				tView.setGravity(Gravity.CENTER);
			}
		}

		tView.setText(content);
	}

	/**
	 * 显示统一风格的等待对话框。没有标题
	 * 
	 * @param msg
	 *            对话框内容
	 * @param callBack
	 *            对话框回调
	 * @param isCanCancelabel
	 *            是否可以取消
	 * @param tag
	 *           
	 */
	public static Dialog showCommenWaitDialog(Context context, String msg, final DialogCallBack callBack, boolean isCanCancelabel, final Object tag)
	{
		final Dialog dialog = new Dialog(context, R.style.DialogNoTitleRoundCornerStyle);
		dialog.setOwnerActivity((Activity)context);
		View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_wait_common_layout, null);
		TextView contentTv = (TextView) contentView.findViewById(R.id.dialog_content_tv);
		if (JStringKit.isNotBlank(msg))
		{
			contentTv.setText(msg);
		}
		dialog.setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{

				if (null != callBack)
				{
					callBack.onCancelDialog((Dialog) dialog, tag);
				}
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(isCanCancelabel);
		dialog.setContentView(contentView);
		dialog.show();
		return dialog;

	}
}
