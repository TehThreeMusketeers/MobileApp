package com.sunicola.setapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sunicola.setapp.R;
import com.sunicola.setapp.helper.APICalls;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TriggerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TriggerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TriggerFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int stateSelect;
    private int valTypeSelect;
    private String valSelect;
    private int opSelect;
    private String actSelect;
    private EditText edit_text;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TriggerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TriggerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TriggerFragment newInstance(String param1, String param2) {
        TriggerFragment fragment = new TriggerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_trigger, container, false);

        Button b = (Button) v.findViewById(R.id.button);
        b.setOnClickListener(this);

        Spinner spinnerState = v.findViewById(R.id.spinner_state);
        Spinner spinnerVar = v.findViewById(R.id.spinner_var);
        Spinner spinnerOperator = v.findViewById(R.id.spinner_operator);
        Spinner spinnerAction = v.findViewById(R.id.spinner_action);

        edit_text = (EditText)v.findViewById(R.id.editText4);

        //populate spinners here
        String[] stateVal = {"ARMED", "DISARMED"};
        String[] varVal = {"Motion", "Light level", "Sound level"};
        String[] opVal = {"<", ">", "="};
        String[] actionVal = {"Blink LED", "function2", "function3"};

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, stateVal);
        ArrayAdapter<String> varAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, varVal);
        ArrayAdapter<String> opAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, opVal);
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, actionVal);

        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        varAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerState.setAdapter(stateAdapter);
        spinnerVar.setAdapter(varAdapter);
        spinnerOperator.setAdapter(opAdapter);
        spinnerAction.setAdapter(actionAdapter);

        spinnerState.setOnItemSelectedListener(this);
        spinnerVar.setOnItemSelectedListener(this);
        spinnerOperator.setOnItemSelectedListener(this);
        spinnerAction.setOnItemSelectedListener(this);


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int i, long l) {
        switch (parent.getId()){
            case R.id.spinner_action:
                actSelect = parent.getItemAtPosition(i).toString();
                Log.e("action", actSelect);
                break;
            case R.id.spinner_operator:
                if(i==2){
                    opSelect = 5;
                }else{
                    opSelect = i+1;
                }
                break;
            case R.id.spinner_state:
                stateSelect = i+1;
                break;
            case R.id.spinner_var:
                valTypeSelect = i+3;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        valSelect = edit_text.getText().toString();
        APICalls api = new APICalls(getContext());
        api.sendTrigger(2, stateSelect, valTypeSelect, opSelect, valSelect, actSelect);
        System.out.println(stateSelect +" " +valTypeSelect +" " +opSelect +" " +valSelect +" " +actSelect);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
