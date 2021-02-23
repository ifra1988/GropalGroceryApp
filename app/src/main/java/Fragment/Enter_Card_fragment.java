package Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import gropal.in.MainActivity;
import gropal.in.R;

/**
 * Created by Rajesh Dabhi on 28/6/2017.
 */

public class Enter_Card_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Enter_Card_fragment.class.getSimpleName();

    @BindView(R.id.btn_save)
    Button btn_save;

    @BindView(R.id.et_card_number)
    EditText et_card_number;

    @BindView(R.id.et_clabel)
    EditText et_clabel;

    @BindView(R.id.et_cholder_name)
    EditText et_cholder_name;

    @BindView(R.id.sp_yy)
    Spinner sp_yy;

    @BindView(R.id.sp_mm)
    Spinner sp_mm;

    public Enter_Card_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_card, container, false);
        ButterKnife.bind(this,view);

        ((MainActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.add_your_card));

        btn_save.setOnClickListener(this);
        setMonthSpinner();
        setYearSpinner();

        return view;
    }

    private void setYearSpinner()
    {
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1900; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_yy.setAdapter(adapter);
    }

    private void setMonthSpinner()
    {
        String[] months = { "January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December" };
       ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item, months);
       // DateSpinnerAdapter adapter = new DateSpinnerAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_mm.setAdapter(adapter);

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Fragment fm = null;
        if (id == R.id.btn_edit) {
        } else if (id == R.id.iv_pro_img) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
        }

        if (fm != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();

        }
    }



}
