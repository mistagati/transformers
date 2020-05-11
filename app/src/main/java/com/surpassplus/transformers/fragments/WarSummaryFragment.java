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
import com.surpassplus.transformers.adapters.BattleAdapter;
import com.surpassplus.transformers.application.AppController;
import com.surpassplus.transformers.customviews.CustomEditText;
import com.surpassplus.transformers.intefaces.TransformerClickListenerInterface;
import com.surpassplus.transformers.model.Transformer;
import com.surpassplus.transformers.model.Transformers;
import com.surpassplus.transformers.model.War;
import com.surpassplus.transformers.model.Winner;
import com.surpassplus.transformers.util.PreferencesHandler;
import com.surpassplus.transformers.util.Utils;
import com.surpassplus.transformers.webservice.TransformerAPIClient;
import com.surpassplus.transformers.webservice.TransformerAPIInterface;

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


public class WarSummaryFragment extends Fragment implements TransformerClickListenerInterface {

    private OnFragmentInteractionListener mListener;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private BattleAdapter mBattleAdapter;
    private TransformerAPIInterface mTransformerApiInterface;
    private PreferencesHandler mPreferencesHandler;
    private ArrayList<Transformer> mTransformers;
    private ArrayList<Transformer> mTransformerAllResults = new ArrayList<>();
    private ArrayList<Transformer> mTransformerResults = new ArrayList<>();
    private Dialog dialog;
    private ProgressBar mProgressBar;
    private boolean isScrollEnabled = true;
    private CustomEditText mCityEditText;
    private TextView mNoBranchesAvailableTextView;
    private ImageView mEditImageView;
    private Button mApplyButton;
    private TextView mTitleHeadingTextView;
    private TextView mLocationTextView;
    private LinearLayout mLayoutCityOptions;
    private Typeface typeFace;
    private Window w;

    private ImageView mBackArrow;

    //private ImageView mAddButton;
    private Button mAutobotButton;
    private Button mDecepticonButton;

    private Button mWarButton;
    private TextView mBattleCount;

    private TextView aBattleWins;
    private TextView dBattleWins;

    private ArrayList<Transformer> autobots = new ArrayList<>();
    private ArrayList<Transformer> decepticons = new ArrayList<>();

    private TextView mWinningTeam;

    public enum ListMode {
        AUTOBOT,
        DECEPTICON
    }

    public ListMode listMode;

    public WarSummaryFragment() { }

    private static War mWar;


    public static WarSummaryFragment newInstance(War war) {
        WarSummaryFragment fragment = new WarSummaryFragment();
        mWar = war;
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
        mRootView = inflater.inflate(R.layout.fragment_war_summary, container, false);
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
        mEditImageView = (ImageView) mRootView.findViewById(R.id.editImageView);

        mAutobotButton = (Button) mRootView.findViewById(R.id.autobotButton);
        mDecepticonButton = (Button) mRootView.findViewById(R.id.decepticonButton);
        typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/Cabin-Regular.ttf");
        mEditImageView.setOnClickListener(onClickListener);
        mLocationTextView.setOnClickListener(onClickListener);

        mWinningTeam = (TextView) mRootView.findViewById(R.id.winningTeam);

        aBattleWins = (TextView) mRootView.findViewById(R.id.aBattleWins);
        dBattleWins = (TextView) mRootView.findViewById(R.id.dBattleWins);



        mBackArrow = (ImageView) mRootView.findViewById(R.id.backButton);
        mBackArrow.setOnClickListener(onClickListener);


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


        if (mWar.getWinner() == Winner.AUTOBOT) {
            mWinningTeam.setText("Autobots");
        } else if (mWar.getWinner() == Winner.DECEPTICON) {
            mWinningTeam.setText("Decepticons");
        } else  {

        }

        aBattleWins.setText(mWar.getAutobotWinTally()+"");
        dBattleWins.setText(mWar.getDecepticonWinTally()+"");

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
                case R.id.editImageView:
                    prepareForWar();
                    break;
                case R.id.backButton:
                    goBack();
                    break;
                default:
                    break;
            }

        }
    };

    private void goBack() {
        getFragmentManager().popBackStack();
    }


    private void fetchSmoothly() {
        w = getActivity().getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void setDataToAdapter() {
        mBattleAdapter = new BattleAdapter(mWar.getBattles(), getActivity(), getFragmentManager(), this);
        RecyclerView.LayoutManager mLayoutManager = new CustomGridLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mBattleAdapter);
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
                mTitleHeadingTextView.setVisibility(VISIBLE);
                mEditImageView.setVisibility(VISIBLE);
                //mLocationTextView.setVisibility(VISIBLE);
                mLocationTextView.setText(mPreferencesHandler.getFilterLocation());
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

