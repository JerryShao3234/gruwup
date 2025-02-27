package com.cpen321.gruwup;

import static com.cpen321.gruwup.DiscoverFragment.epochToDate;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileFragment extends Fragment {
    TextView displayName;
    ImageView profilePic;
    Button signOutButton;
    TextView userBio;
    String bio;
    EditText bioInput;
    private GoogleSignInClient mGoogleSignInClient;

    Dialog profileDialog;
    Button editButton;
    Button infoButton;
    ArrayList<Map<String, String>> mAdventureList;

    final static String TAG = "ProfileFragment";
    final static String RESPONSE_TIME_TAG = "RESPONSE_TIME ";

    private String address;

    String UserID;
    String cookie;

    RecyclerView categoryView ;
    RecyclerView selectedCategories ;

    private ArrayList<String> mCategoryNames = new ArrayList<>();
    private ArrayList<String> mSelectedCategoryNames = new ArrayList<>();

    // TO DO: Can make this common later to avoid DRY
    private void initCategories(){
        mCategoryNames.add("MOVIE");
        mCategoryNames.add("MUSIC");
        mCategoryNames.add("SPORTS");
        mCategoryNames.add("FOOD");
        mCategoryNames.add("TRAVEL");
        mCategoryNames.add("DANCE");
        mCategoryNames.add("ART");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        address = getActivity().getString(R.string.connection_address);
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        // Note: get stored UserID this way for fragment
        UserID = SharedPreferencesUtil.getUserId(this.getActivity());
        cookie = SharedPreferencesUtil.getCookie(this.getActivity());

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        displayName = (TextView) view.findViewById(R.id.userName);
        userBio = (TextView) view.findViewById(R.id.userBio);
        if (this.getArguments()!=null){
            displayName.setText(this.getArguments().getString("Display_Name"));
            //set profile picture using the link from the bundle using Picasso
            profilePic = (ImageView) view.findViewById(R.id.userImage);
            if(this.getArguments().getString("Photo_URL") != null && !this.getArguments().getString("Photo_URL").equals("")) {
                Picasso.get().load(this.getArguments().getString("Photo_URL")).into(profilePic);
            }

            try {
                getProfileRequest();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }

            signOutButton = (Button) view.findViewById(R.id.sign_out_button);
            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ProfileFragment", "Signing out");
                    mGoogleSignInClient.signOut();
                    signOutRequest();
                }
            });
            Log.d(TAG, "Selected Categories "+mSelectedCategoryNames);
        }
        else {
            displayName.setText("User name");
            userBio.setText("Biography");
        }

        categoryView = (RecyclerView) view.findViewById(R.id.categoryRecyclerView);
        selectedCategories = (RecyclerView) view.findViewById(R.id.selectedCategories);

        profileDialog = new Dialog(getActivity());

        editButton = view.findViewById(R.id.edit_profile_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Edit Icon Clicked");
                try {
                    showPopUp(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        infoButton = view.findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showInfo(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public void showInfo(View v) throws IOException {

//        LinearLayoutManager layoutManager = new LinearLayoutManager(profileDialog.getContext());
//        RecyclerView adventureListView = (RecyclerView) profileDialog.findViewById(R.id.adventureRecyclerView);
//        adventureListView.setLayoutManager(layoutManager);
//        DiscAdvViewAdapter adapter = new DiscAdvViewAdapter(getActivity(), mAdventureList);
//        adventureListView.setAdapter(adapter);

        TextView goBack;
        profileDialog.setContentView(R.layout.info_pop_up);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView adventureListView = (RecyclerView) profileDialog.findViewById(R.id.adventureRecyclerView);
        adventureListView.setLayoutManager(layoutManager);
        AdventureInfoAdapter adapter = new AdventureInfoAdapter(getActivity(),mAdventureList);
        adventureListView.setAdapter(adapter);

        goBack  = (TextView) profileDialog.findViewById(R.id.goBack);
        goBack.setPaintFlags(goBack.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Note: can change this to display from cache  (previous selected categories)
                try {
                    getProfileRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileDialog.dismiss();
            }
        });

        TextView noAdventure = (TextView) profileDialog.findViewById(R.id.noAdventures);
        if (mAdventureList.size()==0){
            noAdventure.setVisibility(View.VISIBLE);
        }else{
            noAdventure.setVisibility(View.INVISIBLE);
        }

        profileDialog.show();
    }

    public void showPopUp(View v) throws IOException {
        TextView goBack;
        Button confirmButton;
        TextView bioValidation;
        TextView categoryValidation;
        TextView userBio;

        mSelectedCategoryNames.clear();

        profileDialog.setContentView(R.layout.profile_pop_up);
        goBack  = (TextView) profileDialog.findViewById(R.id.goBack);
        goBack.setPaintFlags(goBack.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        confirmButton = (Button) profileDialog.findViewById(R.id.confirmButton);
        bioInput = (EditText) profileDialog.findViewById(R.id.biographyInput);
        bioValidation = (TextView) profileDialog.findViewById(R.id.biographyAlert);
        categoryValidation = (TextView) profileDialog.findViewById(R.id.categoryAlert);
        userBio = (TextView) getView().findViewById(R.id.userBio);
        getProfileRequest();
        bioInput.setText(bio);
        // for categories
        initCategories();
        Log.d(TAG, "Initialize Category Recycler View");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoryView = (RecyclerView) profileDialog.findViewById(R.id.categoryRecyclerView);
        categoryView.setLayoutManager(layoutManager);
        CategoryViewAdapter adapter = new CategoryViewAdapter(getActivity(),mCategoryNames);
        categoryView.setAdapter(adapter);

        LinearLayoutManager categoriesLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        selectedCategories = (RecyclerView) getView().findViewById(R.id.selectedCategories);
        selectedCategories.setLayoutManager(categoriesLayoutManager);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Note: can change this to display from cache  (previous selected categories)
                try {
                    getProfileRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileDialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Pressed Confirm Button");
                Log.d(TAG, bioInput.getText().toString());

                if(!verifyUserInput(bioInput).equals("valid") && adapter.getSelectedCategoriesCount()<3){
                    bioValidation.setText(verifyUserInput(bioInput));
                    categoryValidation.setText("Please select at least 3 categories.");
                }
                else if (verifyUserInput(bioInput).equals("valid") && adapter.getSelectedCategoriesCount()<3){
                    bioValidation.setText("");
                    categoryValidation.setText("Please select at least 3 categories.");
                }
                else if (!verifyUserInput(bioInput).equals("valid")){
                    bioValidation.setText(verifyUserInput(bioInput));
                }
                else if(adapter.getSelectedCategoriesCount()<3){
                    categoryValidation.setText("Please select at least 3 categories.");
                }
                else{
                    mSelectedCategoryNames.clear();
                    Log.d(TAG, "Selected categories names before are: "+ mSelectedCategoryNames);

                    userBio.setText(bioInput.getText().toString());
                    for (int i = 0 ; i < adapter.getSelectedCategoriesCount(); i++){
                        mSelectedCategoryNames.add(mCategoryNames.get(adapter.getSelectedCategories().get(i)));
                    }
                    CategoryViewAdapter selectedCategoriesAdapter = new CategoryViewAdapter(getActivity(),mSelectedCategoryNames);
                    selectedCategories.setAdapter(selectedCategoriesAdapter);

                    // Note: Can store this in cache
                    Log.d(TAG, "Selected categories names are: "+ mSelectedCategoryNames);
                    try {
                        editProfileRequest(bioInput.getText().toString(), mSelectedCategoryNames);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    profileDialog.dismiss();

                    Toast.makeText(getActivity(), "Changed Profile Information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileDialog.show();
    }

    private void signOutRequest(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", UserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();
        RequestsUtil.postWithCookie("http://"+address+":8081/account/sign-out", jsonObject.toString(), cookie, new Callback(){

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                long end = System.currentTimeMillis();
                Log.d(RESPONSE_TIME_TAG, "SIGN OUT: " + (end-start) + " millis");

                if(response.isSuccessful()){
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    startActivity(intent);
                }
                else{
                    Log.d(TAG, "sign out unsuccessful");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Failed to sign out, try again or contact developers.", Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.d(TAG, response.body().string());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "sign out failed");
            }
        });

    }

    private void getProfileRequest() throws IOException{
        // To do: replace this with server url
        String cookie = SharedPreferencesUtil.getCookie(this.getActivity());
        Log.d(TAG, "User Id is "+ UserID);
        long start = System.currentTimeMillis();
        RequestsUtil.getWithCookie("http://"+address+":8081/user/profile/" + UserID + "/get", cookie, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "get profile not successful");
                Log.d(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long end = System.currentTimeMillis();
                Log.d(RESPONSE_TIME_TAG, "GET PROFILE: " + (end-start) + " millis");

                if(response.isSuccessful()){
                    Log.d(TAG, "get profile successful");
                    String jsonData = response.body().string();
                    Log.d(TAG, "get profile data "+ jsonData);

                    try {
                        JSONObject jsonObj = new JSONObject(jsonData);
                        bio = jsonObj.getString("biography");
                        JSONArray pref = jsonObj.getJSONArray("categories");

                        mAdventureList = new ArrayList<Map<String, String>>();
                        JSONArray jsonArray = jsonObj.getJSONArray("adventuresCreated");
                        int arrlen = jsonArray.length();

                        for (int i = 0; i < arrlen; i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(i);
                            mAdventureList.add(new HashMap<String, String>());
                            mAdventureList.get(i).put("title", jsonObject.getString("title"));
                            mAdventureList.get(i).put("event", jsonObject.getString("category"));
                            mAdventureList.get(i).put("id", jsonObject.getString("_id"));
                            mAdventureList.get(i).put("time", String.valueOf(epochToDate(String.valueOf(jsonObject.getString("dateTime")))));
                            mAdventureList.get(i).put("location", jsonObject.getString("location"));
                            mAdventureList.get(i).put("count", String.valueOf((new JSONArray(jsonObject.getString("peopleGoing"))).length()));
                            mAdventureList.get(i).put("description", jsonObject.getString("description"));
                            mAdventureList.get(i).put("image", jsonObject.getString("image"));
                        }

//                        Log.d(TAG, "LIST OF ADV" + mAdventureList.toString());

                        ArrayList<String> preferences_list = new ArrayList<String>();
                        if (pref !=null){
                            for (int i=0; i<pref.length(); i++){
                                preferences_list.add(pref.getString(i));
                            }
                        }

                        mSelectedCategoryNames = preferences_list;
                        if(getActivity() == null)
                            return;

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                userBio = (TextView) getView().findViewById(R.id.userBio);

                                userBio.setText(bio);
                                RecyclerView.LayoutManager mLayoutManafer = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                selectedCategories.setLayoutManager(mLayoutManafer);
                                CategoryViewAdapter adapter = new CategoryViewAdapter(getActivity(), preferences_list);
                                selectedCategories.setAdapter(adapter);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Log.d(TAG, "get profile is unsuccessful");
                    Log.d(TAG, response.body().string());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Failed to get profile, try navigating again or contact developers.", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });

    }


    private void editProfileRequest(String biography, ArrayList<String> categoryNames) throws IOException {
        bio = biography;
        String cookie = SharedPreferencesUtil.getCookie(this.getActivity());
        Log.d(TAG, "bio is "+ biography);

        JSONObject jsonObject = new JSONObject();

        JSONArray preferences = new JSONArray(categoryNames);
        try {
            jsonObject.put("userId", UserID);
            jsonObject.put("name", displayName.toString());
            jsonObject.put("biography", biography);
            jsonObject.put("categories", preferences);
            if (this.getArguments()!=null){
                jsonObject.put("image", this.getArguments().getString("Photo_URL"));
            }
            else{
                jsonObject.put("image", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();

        RequestsUtil.putWithCookie("http://"+address+":8081/user/profile/" + UserID + "/edit", jsonObject.toString(), cookie, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "could not edit the user profile");
                Log.d(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long end = System.currentTimeMillis();
                Log.d(RESPONSE_TIME_TAG, "EDIT PROFILE: " + (end-start) + " millis");

                if(response.isSuccessful()){
                    Log.d(TAG, "profile edit successful");
                }
                else{
                    Log.d(TAG, "profile edit unsuccessful");
                    Log.d(TAG, response.message().toString());
                    Log.d(TAG, response.toString());
                }
            }
        });

    }

    public static String verifyUserInput(EditText field) {
        if (field.getText().toString().trim().equals("")){
            System.out.println("Empty field");
            return "This field cannot be empty.";
        }
        return "valid";
    }
}