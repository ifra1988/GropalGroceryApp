package Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import gropal.in.MainActivity;
import gropal.in.R;
import util.UIUtility;

public class Update_Profile_Fragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.et_first_name)
    EditText et_fName;
    @BindView(R.id.et_last_name)
    EditText et_lName;
    @BindView(R.id.et_number)
    EditText et_phone;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.sp_gender)
    Spinner sp_gender;
    @BindView(R.id.et_dob)
    EditText et_dob;
    @BindView(R.id.btn_save)
    Button btn_save;
    @BindView(R.id.cb_tick)
    CheckBox cb_tick;
    @BindView(R.id.iv_calender)
    ImageView iv_calender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        ButterKnife.bind(this,view);

        ((MainActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.edit_profile));

        UIUtility.showSoftKeyboard(et_fName, getContext());

        iv_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        setGenderSpinner();

        return view;

    }

/*    private void selectGender()
    {
        ArrayList<String> gender_list = new ArrayList<>();
        gender_list.add(getString(R.string.female));
        gender_list.add(getString(R.string.male));
        gender_list.add(getString(R.string.other));


//        @SuppressLint("ResourceType") ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
  //              android.R.layout.simple_spinner_dropdown_item, gender_list);
        Adapter.SpinnerAdapter adapter =  new Adapter.SpinnerAdapter(getContext(),gender_list);
       // act_gender.setThreshold(0);
        act_gender.setAdapter(adapter);

        act_gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String gender = parent.getItemAtPosition(position).toString();
                act_gender.setText(gender);
                UIUtility.showAlert("",gender,getContext());
            }
        });
        act_gender.setFocusable(false);
        act_gender.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub
                act_gender.showDropDown();
                UIUtility.hideSoftKeyboard(act_gender,getContext());
                return false;
            }
        });

        Log.i("gender",act_gender.getText().toString());
    }
*/
    private void setGenderSpinner()
    {
        ArrayList<String> gender_list = new ArrayList<>();
        gender_list.add(getString(R.string.female));
        gender_list.add(getString(R.string.male));
        gender_list.add(getString(R.string.other));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, gender_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_gender.setAdapter(adapter);
    }

    private void   selectDate() {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofyear, int dayofmonth) {
                et_dob.setText(monthofyear + "/" + dayofmonth + "/" + year);
            }
        }, year, month, day);
        dpd.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_save:

                break;
            case R.id.cb_tick:
                break;

        }
    }

    private void validateFields()
    {
        if(!UIUtility.isPhoneValid(et_phone.getText().toString()))
        {
            UIUtility.showAlert(getString(R.string.app_name),getString(R.string.phone_error_msg),getContext());
        }
        else if(!UIUtility.isEmailValid(et_email.getText().toString()))
        {
            UIUtility.showAlert(getString(R.string.app_name),getString(R.string.invalide_email_address),getContext());
        }
        else if(TextUtils.isEmpty(et_fName.getText()))
        {
            UIUtility.showAlert(getString(R.string.app_name),getString(R.string.empty_fName),getContext());
        }
    }


    private void saveProfile()
    {

    }

}
