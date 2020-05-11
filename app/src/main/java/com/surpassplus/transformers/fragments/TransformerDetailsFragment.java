package com.surpassplus.transformers.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.surpassplus.transformers.R;
import com.surpassplus.transformers.activities.TransformerContainerActivity;
import com.surpassplus.transformers.customviews.CustomEditText;
import com.surpassplus.transformers.customviews.CustomTextView;
import com.surpassplus.transformers.model.Transformer;
import com.surpassplus.transformers.util.PreferencesHandler;
import com.surpassplus.transformers.webservice.TransformerAPIClient;
import com.surpassplus.transformers.webservice.TransformerAPIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

/**
 * Displays specific Branch's detailed information
 */
public class TransformerDetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static Transformer mTransformer;
    private View mRootView;
    private Typeface mTypeface;
    //private ProgressBar mBranchDetailsProgressBar;
    private ProgressBar mTransformerDetailsProgressBar;
    private FragmentManager fragmentManager;
    private Fragment mFragment;
    private PreferencesHandler mPreferencesHandler;
    private ImageView mBackArrow;
    private RelativeLayout mParentContanerLayout;
    private TextView mBranchTextView;
    private TextView mAddressTextView;
    private TextView mPhoneTextView;
    private TextView mHoursTextView;

    private SeekBar mCourageSeekBar;
    private CustomTextView mCourageValueTextView;

    private SeekBar mEnduranceSeekBar;
    private CustomTextView mEnduranceValueTextView;

    private SeekBar mFirepowerSeekBar;
    private CustomTextView mFirepowerValueTextView;

    private SeekBar mIntelligenceSeekBar;
    private CustomTextView mIntelligenceValueTextView;

    private SeekBar mRankSeekBar;
    private CustomTextView mRankValueTextView;

    private SeekBar mSkillSeekBar;
    private CustomTextView mSkillValueTextView;

    private SeekBar mSpeedSeekBar;
    private CustomTextView mSpeedValueTextView;

    private SeekBar mStrengthSeekBar;
    private CustomTextView mStrengthValueTextView;

    private ImageView mSaveImageView;
    private ImageView mDeleteImageView;

    private CustomEditText mNameEditText;
    private Button mAutobotButton;
    private Button mDecepticonButton;

    private TextView mModeTextView;



    private TransformerAPIInterface mTransformerApiInterface;
    AlertDialog.Builder builder;


    public enum SCALE {
        NONE,
        ADD,
        SUBTRACT;
    }

    public enum EditMode {
        ADD,
        EDIT
    }


    private EditMode editMode;

    private boolean changesSaved = false;

    public TransformerDetailsFragment() { }

    public static TransformerDetailsFragment newInstance(Transformer transformer) {
        TransformerDetailsFragment fragment = new TransformerDetailsFragment();
        mTransformer = transformer;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_transformer_detail, container, false);
        mTransformerApiInterface = TransformerAPIClient.getTransformerData().create(TransformerAPIInterface.class);
        builder = new AlertDialog.Builder(getActivity());
        initUi();
        return mRootView;
    }

    private void initUi() {
        mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Cabin-Regular.ttf");
        mTransformerDetailsProgressBar = (ProgressBar) mRootView.findViewById(R.id.transformerDetailsProgressBar);
        mBackArrow=(ImageView)mRootView.findViewById(R.id.backArrow);
        mParentContanerLayout=(RelativeLayout)mRootView.findViewById(R.id.mParentContainerLayout);
        mPreferencesHandler=PreferencesHandler.getInstance(getActivity());
        mParentContanerLayout.setVisibility(View.GONE);
       // mBranchTextView = (TextView) mRootView.findViewById(R.id.branchTextView);
        mAddressTextView = (TextView) mRootView.findViewById(R.id.addressTextView);
        mPhoneTextView = (TextView) mRootView.findViewById(R.id.phoneTextView);
        mHoursTextView = (TextView) mRootView.findViewById(R.id.hoursTextView);

        mCourageSeekBar = (SeekBar) mRootView.findViewById(R.id.courageSeekBar);
        mCourageValueTextView = (CustomTextView) mRootView.findViewById(R.id.courageValueTextView);

        mModeTextView = (TextView) mRootView.findViewById(R.id.modeTextView);

        mNameEditText = (CustomEditText) mRootView.findViewById(R.id.nameEditText);
        mSaveImageView = (ImageView) mRootView.findViewById(R.id.saveImageView);
        mDeleteImageView = (ImageView) mRootView.findViewById(R.id.deleteImageView);


        if (mTransformer.getId().trim().length() == 0) {
            editMode = EditMode.ADD;
            mModeTextView.setText("Add");
            mDeleteImageView.setVisibility(View.INVISIBLE);
            mDeleteImageView.getLayoutParams().width = 0;
            setMargins(mSaveImageView, 0,0,0,0);

        } else {
            editMode = EditMode.EDIT;
            mModeTextView.setText("Edit");
            mDeleteImageView.setVisibility(VISIBLE);
        }
        mNameEditText.setText(mTransformer.getName());

        mSaveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });


        mDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptDelete();
            }
        });

        mAutobotButton = (Button) mRootView.findViewById(R.id.autobotButton);
        mDecepticonButton = (Button) mRootView.findViewById(R.id.decepticonButton);
        mAutobotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { setToAutobot(); }
        });
        mDecepticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { setToDecepticon(); }
        });
        if (mTransformer.getTeam().trim().equals("D")) {
            setToDecepticon();
        } else {
            setToAutobot();
        }

        mCourageValueTextView.setText(scaledValue(mTransformer.getCourage(), SCALE.NONE));
        mCourageSeekBar.setProgress(scaledIntValue(mTransformer.getCourage(), SCALE.SUBTRACT));
        mCourageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCourageValueTextView.setText(scaledValue(i, SCALE.ADD));
                mTransformer.setCourage(scaledIntValue(i,SCALE.ADD));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mEnduranceSeekBar = (SeekBar) mRootView.findViewById(R.id.enduranceSeekBar);
        mEnduranceValueTextView = (CustomTextView) mRootView.findViewById(R.id.enduranceValueTextView);
        mEnduranceValueTextView.setText(scaledValue(mTransformer.getEndurance(), SCALE.NONE));
        mEnduranceSeekBar.setProgress(scaledIntValue(mTransformer.getEndurance(), SCALE.SUBTRACT));
        mEnduranceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mEnduranceValueTextView.setText(scaledValue(i, SCALE.ADD));
                mTransformer.setEndurance(scaledIntValue(i,SCALE.ADD));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mFirepowerSeekBar = (SeekBar) mRootView.findViewById(R.id.firepowerSeekBar);
        mFirepowerValueTextView = (CustomTextView) mRootView.findViewById(R.id.firepowerValueTextView);
        mFirepowerValueTextView.setText(scaledValue(mTransformer.getFirepower(), SCALE.NONE));
        mFirepowerSeekBar.setProgress(scaledIntValue(mTransformer.getFirepower(), SCALE.SUBTRACT));
        mFirepowerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mFirepowerValueTextView.setText(scaledValue(i, SCALE.ADD));
                mTransformer.setFirepower(scaledIntValue(i,SCALE.ADD));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mIntelligenceSeekBar = (SeekBar) mRootView.findViewById(R.id.intelligenceSeekBar);
        mIntelligenceValueTextView = (CustomTextView) mRootView.findViewById(R.id.intelligenceValueTextView);
        mIntelligenceValueTextView.setText(scaledValue(mTransformer.getIntelligence(), SCALE.NONE));
        mIntelligenceSeekBar.setProgress(scaledIntValue(mTransformer.getIntelligence(), SCALE.SUBTRACT));
        mIntelligenceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mIntelligenceValueTextView.setText(scaledValue(i, SCALE.ADD));
                mTransformer.setIntelligence(scaledIntValue(i,SCALE.ADD));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mRankSeekBar = (SeekBar) mRootView.findViewById(R.id.rankSeekBar);
        mRankValueTextView = (CustomTextView) mRootView.findViewById(R.id.rankValueTextView);
        mRankValueTextView.setText(scaledValue(mTransformer.getRank(), SCALE.NONE));
        mRankSeekBar.setProgress(scaledIntValue(mTransformer.getRank(), SCALE.SUBTRACT));
        mRankSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mRankValueTextView.setText(scaledValue(i, SCALE.ADD));
                mTransformer.setRank(scaledIntValue(i,SCALE.ADD));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mSkillSeekBar = (SeekBar) mRootView.findViewById(R.id.skillSeekBar);
        mSkillValueTextView = (CustomTextView) mRootView.findViewById(R.id.skillValueTextView);
        mSkillValueTextView.setText(scaledValue(mTransformer.getSkill(), SCALE.NONE));
        mSkillSeekBar.setProgress(scaledIntValue(mTransformer.getSkill(), SCALE.SUBTRACT));
        mSkillSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSkillValueTextView.setText(scaledValue(i, SCALE.ADD));
                mTransformer.setSkill(scaledIntValue(i,SCALE.ADD));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mSpeedSeekBar = (SeekBar) mRootView.findViewById(R.id.speedSeekBar);
        mSpeedValueTextView = (CustomTextView) mRootView.findViewById(R.id.speedValueTextView);
        mSpeedValueTextView.setText(scaledValue(mTransformer.getSpeed(), SCALE.NONE));
        mSpeedSeekBar.setProgress(scaledIntValue(mTransformer.getSpeed(), SCALE.SUBTRACT));
        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSpeedValueTextView.setText(scaledValue(i, SCALE.ADD));
                mTransformer.setSpeed(scaledIntValue(i,SCALE.ADD));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mStrengthSeekBar = (SeekBar) mRootView.findViewById(R.id.strengthSeekBar);
        mStrengthValueTextView = (CustomTextView) mRootView.findViewById(R.id.strengthValueTextView);
        mStrengthValueTextView.setText(scaledValue(mTransformer.getStrength(), SCALE.NONE));
        mStrengthSeekBar.setProgress(scaledIntValue(mTransformer.getStrength(), SCALE.SUBTRACT));
        mStrengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mStrengthValueTextView.setText(scaledValue(i, SCALE.ADD));
                mTransformer.setStrength(scaledIntValue(i,SCALE.ADD));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });



        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changesSaved) {
                    goBack(true);
                } else {
                    goBack(false);
                }
                //((BranchContainerActivity) getActivity()).setInitiateReload(true);

                //getActivity().onBackPressed();

               // getFragmentManager().popBackStack();

                //getFragmentManager().popBackStack("list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        setValuesToView();
    }

    private void goBack(boolean doReload) {
        if (doReload) {
            ((TransformerContainerActivity) getActivity()).setInitiateReload(true);
        }
        getActivity().onBackPressed();
    }

    private String scaledValue(int value, SCALE scale) {
        switch (scale) {
            case ADD:
                return (value + 1) + "";
            case SUBTRACT:
                return (value - 1) + "";
            default:
                return value + "";
        }
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private Integer scaledIntValue(int value, SCALE scale) {
        switch (scale) {
            case ADD:
                return (value + 1);
            case SUBTRACT:
                return (value - 1);
            default:
                return value;
        }
    }

    private void setValuesToView() {
        mParentContanerLayout.setVisibility(View.VISIBLE);
        mTransformerDetailsProgressBar.setVisibility(View.GONE);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void saveChanges() {
        if (mNameEditText.getText().toString().trim().length() > 0) {
            mTransformer.setName(mNameEditText.getText().toString().trim());
        } else {
            showError("Oops!", "Looks like you'll need to give your Transformer a name first.");
            return;
        }
        if (editMode == EditMode.EDIT) {
            updateTransformer();
        } else {
            addTransformer();
        }
    }

    private void addTransformer() {
        Call call = mTransformerApiInterface.createTransformer(mTransformer);
        mTransformerDetailsProgressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                mTransformerDetailsProgressBar.setVisibility(View.GONE);
                changesSaved = true;
                showSuccess("Success", "Transformer was succesfully added.", true);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                mTransformerDetailsProgressBar.setVisibility(View.GONE);
                showError("Oops!", "Something must have gone wrong, please try again.");
            }
        });
    }

    private void updateTransformer() {
        Call call = mTransformerApiInterface.updateTransformer(mTransformer);
        mTransformerDetailsProgressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                mTransformerDetailsProgressBar.setVisibility(View.GONE);
                changesSaved = true;
                showSuccess("Success", "Changes to Transformer have been saved.", false);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                mTransformerDetailsProgressBar.setVisibility(View.GONE);
                showError("Oops!", "Something must have gone wrong, please try again.");
            }
        });
    }

    private void deleteTransformer() {
        Call call = mTransformerApiInterface.deleteTransformer(mTransformer.getId());
        mTransformerDetailsProgressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                mTransformerDetailsProgressBar.setVisibility(View.GONE);
                changesSaved = true;
                showSuccess("Success", "Transformer has been successfully deleted!", true);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                mTransformerDetailsProgressBar.setVisibility(View.GONE);
                showError("Oops!", "Something must have gone wrong, please try again.");
            }
        });
    }

    private void promptDelete() {
        builder.setMessage("Are you sure you want to delete this Transformer?")
                .setTitle("Delete Transformer")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        deleteTransformer();
                        // finish();
                        // Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                        //         Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //  Action for 'NO' Button

                       // Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                       //         Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        //alert.setTitle("AlertDialogExample");
        alert.show();
    }

    private void showSuccess(String title, String message, boolean goBack) {
        //Uncomment the below code to Set the message and title from the strings.xml file
        //builder.setMessage(message) .setTitle(title);

        builder = new AlertDialog.Builder(getActivity());
        //Setting message manually and performing action on button click
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        if (goBack) {
                            goBack(true);
                        }
                       // finish();
                       // Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                       //         Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showError(String title, String message) {
        builder.setMessage(message)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        //Setting the title manually
        //alert.setTitle("AlertDialogExample");
        alert.show();
    }

    private void setToAutobot() {
        mAutobotButton.setBackground(getResources().getDrawable(R.drawable.sorting_selected_button));
        mAutobotButton.setTextColor(getResources().getColor(R.color.textColor));
        mDecepticonButton.setTextColor(getResources().getColor(R.color.textColorButton));
        mDecepticonButton.setBackgroundColor(getResources().getColor(R.color.transparent));
        mTransformer.setTeam("A");
    }

    private void setToDecepticon() {
        mDecepticonButton.setBackground(getResources().getDrawable(R.drawable.sorting_selected_button));
        mDecepticonButton.setTextColor(getResources().getColor(R.color.textColor));
        mAutobotButton.setTextColor(getResources().getColor(R.color.textColorButton));
        mAutobotButton.setBackgroundColor(getResources().getColor(R.color.transparent));
        mTransformer.setTeam("D");
    }

}