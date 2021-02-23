/*
 * 10Pearls - Android Framework v1.0
 *
 * The contributors of the framework are responsible for releasing
 * new patches and make modifications to the code menu. Any bug or
 * suggestion encountered while using the framework should be
 * communicated to any of the contributors.
 *
 * Contributors:
 *
 * Ali Mehmood       - ali.mehmood@tenpearls.com
 * Arsalan Ahmed     - arsalan.ahmed@tenpearls.com
 * M. Azfar Siddiqui - azfar.siddiqui@tenpearls.com
 * Syed Khalilullah  - syed.khalilullah@tenpearls.com
 */
package util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Encapsulates methods for UI widgets like {@link Toast}, {@link AlertDialog}
 * etc.
 */
public class UIUtility {

    /**
     * Displays a standard Toast for a small duration, contains just a text
     * view.
     *
     * @param message The message to show
     * @param context A valid context
     */
    public static void showShortToast(String message, Context context) {

        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Displays a standard Toast for a long duration, contains just a text view.
     *
     * @param message The message to show
     * @param context A valid context
     */
    public static void showLongToast(String message, Context context) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Displays an Alert dialog with a primary button.
     *
     * @param title   Title of the dialog
     * @param message Descriptive message for the dialog
     * @param context A valid context
     */
    public static void showAlert(String title, String message, Context context) {

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton("Ok", null);

            alertDialogBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Hides the soft keyboard from the phone's screen.
     *
     * @param editText A valid reference to any EditText, currently in the view
     *                 hierarchy
     * @param context  A valid context
     */
    public static void hideSoftKeyboard(EditText editText, Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(EditText editText, Context context) {

        new Handler().postDelayed(new Runnable() {

            public void run() {

                editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 200);

    }

    public static void setFocus(EditText editText, Context context) {
        editText.post(new Runnable() {
            public void run() {
                editText.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

    }

    public static void callAction(Context context){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Constants.call_number));
        context.startActivity(callIntent);
    }

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public static boolean isEmpty(String text) {
        //TODO: Replace this with your own logic
        return text.isEmpty();
    }

    public static boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 10;
        }
        return false;    }


}
