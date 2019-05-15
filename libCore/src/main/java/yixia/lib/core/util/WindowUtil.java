package yixia.lib.core.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.View;

@SuppressWarnings("unused")
public class WindowUtil {

    public static Dialog createAlertDialog(Context ctx, int titleId, String message, int onOkId,
                                           OnClickListener onOKClick, int onCancelId, OnClickListener onCancelClick) {
        try {
            return createAlertDialog(ctx, titleId, message, ctx.getString(onOkId), onOKClick, ctx.getString(onCancelId), onCancelClick);
        } catch (Exception e) {
            return null;
        }
    }

    public static Dialog createAlertDialog(Context ctx, int titleId, String message, String onOKText,
                                           OnClickListener onOKClick, String onCancelText, OnClickListener onCancelClick) {
        try {
            return createAlertDialog(ctx, titleId == 0 ? "" : ctx.getString(titleId), message, onOKText, onOKClick, onCancelText, onCancelClick);
        } catch (Exception e) {
            return null;
        }
    }

    public static Dialog createAlertDialog(Context ctx, String title, String message, String onOKText,
                                           OnClickListener onOKClick, String onCancelText, OnClickListener onCancelClick) {
        try {
            AlertDialog.Builder builder = createAlertDialogBuilder(ctx, title);
            if (builder == null) {
                return null;
            }
            builder.setMessage(message).setPositiveButton(onOKText, onOKClick);
            if (TextUtils.isEmpty(onCancelText)) {
                return builder.create();
            }
            builder.setNegativeButton(onCancelText, onCancelClick);
            return builder.create();
        } catch (Exception e) {
            return null;
        }
    }

    public static Dialog createAlertDialog(Context ctx, int titleId, String message) {
        try {
            return createAlertDialog(ctx, titleId, message, (dialog, which) -> dialog.dismiss());
        } catch (Exception e) {
            return null;
        }
    }

    public static Dialog createAlertDialog(Context ctx, int titleId, String message, OnClickListener onClick) {
        try {
            return createAlertDialog(ctx, titleId, message, onClick, android.R.string.ok);
        } catch (Exception e) {
            return null;
        }
    }

    public static Dialog createAlertDialog(Context ctx, int title, String message, OnClickListener onClick, int buttonText) {
        try {
            AlertDialog.Builder builder = createAlertDialogBuilder(ctx, ctx.getString(title));
            if (builder == null) {
                return null;
            }
            return builder.setMessage(message).setPositiveButton(buttonText, onClick).create();
        } catch (Exception e) {
            return null;
        }

    }

    public static Dialog createAlertDialog(Context ctx, int titleId, View view, OnClickListener onOKClick, OnClickListener onCancelClick) {
        try {
            return createAlertDialog(ctx, titleId, view, onOKClick, android.R.string.ok, onCancelClick, android.R.string.cancel);
        } catch (Exception e) {
            return null;
        }
    }

    public static Dialog createAlertDialog(Context ctx, int title, View view, OnClickListener onOKClick, int okText, OnClickListener onCancelClick, int cancelText) {
        try {
            String titleString = title == 0 ? "" : ctx.getString(title);
            AlertDialog.Builder builder = createAlertDialogBuilder(ctx, titleString);
            if (builder == null) {
                return null;
            }
            builder.setView(view);
            if (onOKClick != null) {
                builder.setPositiveButton(okText == 0 ? android.R.string.ok : okText, onOKClick);
            }
            if (onCancelClick != null) {
                builder.setNegativeButton(cancelText == 0 ? android.R.string.cancel : cancelText, onCancelClick);
            }
            return builder.create();
        } catch (Exception e) {
            return null;
        }
    }

    private static AlertDialog.Builder createAlertDialogBuilder(Context context, String title) {
        try {
            if (TextUtils.isEmpty(title)) {
                return new AlertDialog.Builder(context);
            } else {
                return new AlertDialog.Builder(context).setTitle(title);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static ProgressDialog createProgressDialog(Context ctx, int titleId, int messageId, boolean cancelable) {
        try {
            String message = ctx.getResources().getString(messageId);
            return createProgressDialog(ctx, titleId, message, cancelable);
        } catch (Exception e) {
            return null;
        }
    }

    public static ProgressDialog createProgressDialog(Context ctx, int titleId, String message, boolean cancelable) {
        try {
            String title = titleId == 0 ? "" : ctx.getString(titleId);
            ProgressDialog dialog = new ProgressDialog(ctx);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setCancelable(cancelable);
            return dialog;
        } catch (Exception e) {
            return null;
        }
    }

    public static ProgressDialog createProgressDialog(Context ctx, int titleId, String message, boolean cancelable, boolean indeterminate) {
        try {
            ProgressDialog dialog = createProgressDialog(ctx, titleId, message, cancelable);
            if (dialog != null && !indeterminate) {
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setIndeterminate(false);
            }
            return dialog;
        } catch (Exception e) {
            return null;
        }
    }

    public static ProgressDialog createProgressDialog(Context ctx, int theme, int titleId, int messageId, boolean cancelable) {
        try {
            String message = ctx.getResources().getString(messageId);
            return createProgressDialog(ctx, theme, titleId, message, cancelable);
        } catch (Exception e) {
            return null;
        }
    }

    public static ProgressDialog createProgressDialog(Context ctx, int theme, int titleId, String message, boolean cancelable) {
        try {
            String title = titleId == 0 ? "" : ctx.getString(titleId);
            ProgressDialog dialog = new ProgressDialog(ctx, theme);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setCancelable(cancelable);
            return dialog;
        } catch (Exception e) {
            return null;
        }
    }

    public static ProgressDialog createProgressDialog(Context ctx, int theme, int titleId, String message, boolean cancelable, boolean indeterminate) {
        try {
            ProgressDialog dialog = createProgressDialog(ctx, theme, titleId, message, cancelable);
            if (dialog != null && !indeterminate) {
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setIndeterminate(false);
            }
            return dialog;
        } catch (Exception e) {
            return null;
        }
    }
}
