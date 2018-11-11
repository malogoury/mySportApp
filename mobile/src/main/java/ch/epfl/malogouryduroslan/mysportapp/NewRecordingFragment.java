package ch.epfl.malogouryduroslan.mysportapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class NewRecordingFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    enum SPORT {RUNNING, CYCLING, SKIING, CLIMBING};
    View fragmentView;

    public NewRecordingFragment() {
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
        fragmentView =  inflater.inflate(R.layout.fragment_new_recording,
                container, false);

        ImageButton buttonRunning = fragmentView.findViewById(R.id.runningButton);
        buttonRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActivityNameAndImage(SPORT.RUNNING);
            }
        });

        ImageButton buttonCycling = fragmentView.findViewById(R.id.cyclingButton);
        buttonCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActivityNameAndImage(SPORT.CYCLING);
            }
        });

        ImageButton buttonSkiing = fragmentView.findViewById(R.id.skiingButton);
        buttonSkiing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActivityNameAndImage(SPORT.SKIING);
            }
        });

        ImageButton buttonClimbing = fragmentView.findViewById(R.id.climbingButton);
        buttonClimbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActivityNameAndImage(SPORT.CLIMBING);
            }
        });
        return fragmentView;
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

    private void setActivityNameAndImage(SPORT activity) {
        Drawable image = getResources().getDrawable(R.drawable.ic_logo);
        String name = "Select Activity";

        ImageView activityImage = fragmentView.findViewById(R.id.imageActivity);
        TextView activityName = fragmentView.findViewById(R.id.nameActivity);

        switch (activity) {
            case RUNNING:
                image = getResources().getDrawable(R.drawable.running);
                name = getString(R.string.running);
                break;
            case CYCLING:
                image = getResources().getDrawable(R.drawable.cycling);
                name = getString(R.string.cycling);
                break;
            case SKIING:
                image = getResources().getDrawable(R.drawable.skiing);
                name = getString(R.string.skiing);
                break;
            case CLIMBING:
                image = getResources().getDrawable(R.drawable.climbing);
                name = getString(R.string.climbing);
                break;
        }

        activityName.setText(name);
        activityImage.setImageDrawable(image);
    }
}
