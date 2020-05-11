package com.surpassplus.transformers.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.surpassplus.transformers.R;
import com.surpassplus.transformers.adapters.TransformerAdapter;
import com.surpassplus.transformers.application.AppController;
import com.surpassplus.transformers.customviews.CustomEditText;
import com.surpassplus.transformers.intefaces.TransformerClickListenerInterface;
import com.surpassplus.transformers.model.Battle;
import com.surpassplus.transformers.model.Transformer;
import com.surpassplus.transformers.model.Transformers;
import com.surpassplus.transformers.model.War;
import com.surpassplus.transformers.model.Winner;
import com.surpassplus.transformers.util.PreferencesHandler;
import com.surpassplus.transformers.util.Utils;
import com.surpassplus.transformers.webservice.TransformerAPIClient;
import com.surpassplus.transformers.webservice.TransformerAPIInterface;
//import com.halifax.branchlocator.webservice.HalifaxAPIClient;
//import com.halifax.branchlocator.webservice.HalifaxAPIInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Lists branches based on filtered criteria
 */
public class TransformerListingFragment extends Fragment implements TransformerClickListenerInterface {

    private OnFragmentInteractionListener mListener;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private TransformerAdapter mTransformerAdapter;
    private TransformerAPIInterface mTransformerApiInterface;
    private PreferencesHandler mPreferencesHandler;
    private ArrayList<Transformer> mTransformers;
    private ArrayList<Transformer> mTransformerAllResults = new ArrayList<>();
    private ArrayList<Transformer> mTransformerResults = new ArrayList<>();
    private Dialog dialog;
    private ImageView closeImageView;
    private ProgressBar mProgressBar;
    private boolean isScrollEnabled = true;
    private CustomEditText mCityEditText;
    private TextView mNoBranchesAvailableTextView;
    private ImageView mEditImageView;
    private ImageView mWarImageView;
    private Button mApplyButton;
    private TextView mTitleHeadingTextView;
    private TextView mLocationTextView;
    private LinearLayout mLayoutCityOptions;
    private Typeface typeFace;
    private Window w;

    private ImageView mAddButton;
    private Button mAutobotButton;
    private Button mDecepticonButton;

    private Button mWarButton;
    private TextView mBattleCount;

    private ArrayList<Transformer> autobots = new ArrayList<>();
    private ArrayList<Transformer> decepticons = new ArrayList<>();

    public enum ListMode {
        AUTOBOT,
        DECEPTICON
    }

    public ListMode listMode;

    public TransformerListingFragment() { }

    private War war;


    public static TransformerListingFragment newInstance() {
        TransformerListingFragment fragment = new TransformerListingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_transformer_listing, container, false);
        listMode = ListMode.AUTOBOT;
        initUi();
        return mRootView;
    }


    private void initUi() {

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.jobListRecyclerView);
        mPreferencesHandler = PreferencesHandler.getInstance(getActivity());
        mNoBranchesAvailableTextView = (TextView) mRootView.findViewById(R.id.noBranchesTextView);
        mTitleHeadingTextView = (TextView) mRootView.findViewById(R.id.titleHeading);
        mLocationTextView = (TextView) mRootView.findViewById(R.id.locationTextView);
        mLayoutCityOptions = (LinearLayout) mRootView.findViewById(R.id.layoutCityOptions);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.mJobListProgressBar);
        //mEditImageView = (ImageView) mRootView.findViewById(R.id.editImageView);
        mWarImageView = (ImageView) mRootView.findViewById(R.id.warImageView);
        mAddButton = (ImageView) mRootView.findViewById(R.id.addButton);
        mAutobotButton = (Button) mRootView.findViewById(R.id.autobotButton);
        mDecepticonButton = (Button) mRootView.findViewById(R.id.decepticonButton);
        typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/Cabin-Regular.ttf");
        //mEditImageView.setOnClickListener(onClickListener);
        mLocationTextView.setOnClickListener(onClickListener);
        mAddButton.setOnClickListener(onClickListener);


        mWarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareForWar();
            }
        });

        mAutobotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToAutobot();
                listMode = ListMode.AUTOBOT;
                loadData(false);
            }
        });

        mDecepticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToDecepticon();
                listMode = ListMode.DECEPTICON;
                loadData(false);
            }
        });

        setDataToAdapter();

        /*
        if (listMode == ListMode.AUTOBOT)
            setToAutobot();
        else
            setToDecepticon();

        loadData(false);*/

        mAutobotButton.callOnClick();
    }



    public void loadData(boolean withFilter) {
        if (Utils.isNetworkAvailable(getActivity())) {
            mProgressBar.setVisibility(VISIBLE);
            fetchBranchData(withFilter);
        } else {
            Utils.Toast(getActivity(), AppController.NO_NETWORK_CONNECTION_MESSAGE);
            mNoBranchesAvailableTextView.setVisibility(VISIBLE);
            mNoBranchesAvailableTextView.setText(AppController.NO_NETWORK_CONNECTION_MESSAGE);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addButton:
                    addNewTransformer();
                    break;
                default:
                    break;
            }

        }
    };

    private void addNewTransformer() {
        FragmentManager fragmentManager = getFragmentManager();
        Transformer newTransformer = new Transformer();
        Fragment fragment = TransformerDetailsFragment.newInstance(newTransformer);
        if (fragment != null) {
            fragmentManager.beginTransaction().add(R.id.branchcontainer, fragment).addToBackStack(null).commit();
        }
    }

    private void fetchSmoothly() {
        w = getActivity().getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void setDataToAdapter() {
        mTransformerAdapter = new TransformerAdapter(mTransformerResults, getActivity(), getFragmentManager(), this);
        RecyclerView.LayoutManager mLayoutManager = new CustomGridLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTransformerAdapter);
    }


    private void updateResults() {
        mTransformerResults.clear();
        if (listMode == ListMode.AUTOBOT) {
            for (Transformer transformer: mTransformerAllResults) {
                if (transformer.getTeam().trim().equals("A")) {
                    mTransformerResults.add(transformer);
                }
            }
        } else {
            for (Transformer transformer: mTransformerAllResults) {
                if (transformer.getTeam().trim().equals("D")) {
                    mTransformerResults.add(transformer);
                }
            }
        }

        Comparator<Transformer> c = (s1, s2) -> s1.compareTo(s2);
        mTransformerResults.sort(c);
        autobots.sort(c);
        decepticons.sort(c);

        setDataToAdapter();
    }

    private void prepareForWar() {
        Call call;
        call = mTransformerApiInterface.getTransformers();
        mProgressBar.setVisibility(VISIBLE);
        autobots.clear();
        decepticons.clear();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                mProgressBar.setVisibility(GONE);
                if (response.body() != null) {
                    ArrayList<Transformer> transformers = ((Transformers) response.body()).getTransformers();
                    if (transformers.size() > 0) {
                        for (Transformer t : transformers) {
                            if (t.getTeam().trim().equals("A")) {
                                autobots.add(t);
                            } else if (t.getTeam().trim().equals("D")) {
                                decepticons.add(t);
                            }
                        }
                        Comparator<Transformer> c = (s1, s2) -> s1.compareTo(s2);
                        autobots.sort(c);
                        decepticons.sort(c);
                        wageWar();
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                mProgressBar.setVisibility(GONE);
            }
        });

    }


    private void fetchBranchData(boolean withFilter) {

        if (mTransformerAllResults.size() > 0 && withFilter) {
            mProgressBar.setVisibility(VISIBLE);
            updateResults();
            mProgressBar.setVisibility(GONE);
            return;
        }

        fetchSmoothly();
        mTransformerApiInterface = TransformerAPIClient.getTransformerData().create(TransformerAPIInterface.class);
        Call call;
        call = mTransformerApiInterface.getTransformers();

        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                isScrollEnabled = true;
                //mLocationTextView.setText(mPreferencesHandler.getFilterLocation());
                if (response.body() != null) {
                    mTransformers = ((Transformers) response.body()).getTransformers();
                    if (mTransformers != null) {
                        mTransformerAllResults = mTransformers;
                        updateResults();
                        mProgressBar.setVisibility(GONE);
                    } else {
                        mProgressBar.setVisibility(GONE);
                    }
                } else {
                    mProgressBar.setVisibility(GONE);
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getContext(), jObjError.getString("msg"), Toast.LENGTH_LONG).show();
                            String msg = jObjError.getString("msg");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mProgressBar.setVisibility(GONE);
                isScrollEnabled = true;
                call.cancel();
            }
        });

    }

    @Override
    public void onMyItemClickCallBack(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        if (mTransformerResults.size() > 0) {
            Fragment fragment = TransformerDetailsFragment.newInstance(mTransformerResults.get(position));
            if (fragment != null) {
                fragmentManager.beginTransaction().add(R.id.branchcontainer, fragment).addToBackStack(null).commit();
            }
        }
    }

    public class CustomGridLayoutManager extends LinearLayoutManager {
        public CustomGridLayoutManager(Context context) {
            super(context);
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void wageWar() {

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.wagewar_popup);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        closeImageView = (ImageView) dialog.findViewById(R.id.popupCloseView);
        mWarButton = (Button) dialog.findViewById(R.id.warButton);
        mBattleCount = (TextView) dialog.findViewById(R.id.battleCount);
        mWarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                startWar();

            }
        });

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ArrayList<Battle> battles = new ArrayList<Battle>();
        for (int i=0; i<autobots.size() && i<decepticons.size(); i++) {
            Battle battle = new Battle((i+1),autobots.get(i), decepticons.get(i));
            battles.add(battle);
        }

        war = new War(battles);

        mBattleCount.setText(battles.size() + " Battles");

        dialog.show();

    }

    private void startWar() {
        mProgressBar.setVisibility(VISIBLE);
        war.fightWar();
        mProgressBar.setVisibility(GONE);
        String msg = "No clear winner!";
        if (war.getWinner() == Winner.AUTOBOT) {
            msg = "Autobots win!";
        } else if ( war.getWinner() == Winner.DECEPTICON) {
            msg = "Decepticons win!";
        } else if ( war.getWinner() == Winner.TIE) {
            msg = "There was a tie!";
        }

        showWarResults();
    }


    private void showWarResults() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = WarSummaryFragment.newInstance(war);
        if (fragment != null) {
            fragmentManager.beginTransaction().add(R.id.branchcontainer, fragment).addToBackStack(null).commit();
        }
    }


    private void setToAutobot() {
        mAutobotButton.setBackground(getResources().getDrawable(R.drawable.sorting_selected_button));
        mAutobotButton.setTextColor(getResources().getColor(R.color.textColor));
        mDecepticonButton.setTextColor(getResources().getColor(R.color.textColorButton));
        mDecepticonButton.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    private void setToDecepticon() {
        mDecepticonButton.setBackground(getResources().getDrawable(R.drawable.sorting_selected_button));
        mDecepticonButton.setTextColor(getResources().getColor(R.color.textColor));
        mAutobotButton.setTextColor(getResources().getColor(R.color.textColorButton));
        mAutobotButton.setBackgroundColor(getResources().getColor(R.color.transparent));
    }


}

