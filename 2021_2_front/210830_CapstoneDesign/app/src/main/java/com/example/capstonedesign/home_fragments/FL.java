package com.example.capstonedesign.home_fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.capstonedesign.FriendListActivity;
import com.example.capstonedesign.R;
import com.example.capstonedesign.home_fragments.Message.MessageActivity;
import com.example.capstonedesign.imageActivity;
import com.example.capstonedesign.retrofit.FCM.FcmMessageRequest;
import com.example.capstonedesign.retrofit.FCM.FcmMessageResponse;
import com.example.capstonedesign.retrofit.ProfileResponse;
import com.example.capstonedesign.retrofit.Ranking;
import com.example.capstonedesign.retrofit.RankingResponse;
import com.example.capstonedesign.retrofit.RetrofitClient;
import com.example.capstonedesign.retrofit.initMyApi;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FL extends Fragment { //친구수 받아오는것 구현 필요
    SharedPreferences sharedPreferences;
    private initMyApi initMyApi;
    CircleImageView profile_myphoto;
    TextView profile_name, profile_people, profile_walk;
    TextView profile_one_name, profile_one_walk;
    TextView profile_two_name, profile_two_walk;
    TextView profile_three_name, profile_three_walk;
    CircleImageView profile_one_photo, profile_two_photo, profile_three_photo;
    //ArrayList<Ranking> ranking;
    LinearLayout parent_layout;

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fl,container,false);

        Log.d("FL_onCreateView","in");

        Button btn_friendlist = rootView.findViewById(R.id.btn_friendlist);
        btn_friendlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendListActivity.class);
                startActivity(intent);
            }
        });

        Button btn_messageBox = rootView.findViewById(R.id.messagebox);
        btn_messageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                startActivity(intent);
            }
        });

        Button first_tab = rootView.findViewById(R.id.first_tab);
        first_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), imageActivity.class);
                startActivity(intent);
                //imageActivity activity = (imageActivity) getActivity();
                //activity.onFragmentChanged(0);
            }
        });

        profile_myphoto = rootView.findViewById(R.id.profile_myphoto);
        profile_name = rootView.findViewById(R.id.profile_name);
        profile_walk = rootView.findViewById(R.id.profile_walk);
        profile_people = rootView.findViewById(R.id.profile_people);
        parent_layout = rootView.findViewById(R.id.parent_layout);

        profile_one_name = rootView.findViewById(R.id.profile_one_name);
        profile_one_walk = rootView.findViewById(R.id.profile_one_walk);
        profile_one_photo = rootView.findViewById(R.id.profile_one_photo);

        profile_two_name = rootView.findViewById(R.id.profile_two_name);
        profile_two_walk = rootView.findViewById(R.id.profile_two_walk);
        profile_two_photo = rootView.findViewById(R.id.profile_two_photo);

        profile_three_name = rootView.findViewById(R.id.profile_three_name);
        profile_three_walk = rootView.findViewById(R.id.profile_three_walk);
        profile_three_photo = rootView.findViewById(R.id.profile_three_photo);

        getProfile();
        getRanking();

        return rootView;
    }
    private void getProfile() {
        sharedPreferences = getActivity().getSharedPreferences("email", Context.MODE_PRIVATE);
        String mytoken = sharedPreferences.getString("token","");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        initMyApi = RetrofitClient.getRetrofitInterface();

        Call<ProfileResponse> call = initMyApi.getProfileResponse("Bearer "+mytoken);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if(response.isSuccessful()) {
                    ProfileResponse result = response.body();
                    String status = result.getStatus();
                    String name = result.getName();
                    int step = result.getStep();
                    String profilePhoto = result.getProfilePhoto();

                    profile_name.setText(name);
                    profile_walk.setText(String.valueOf(step));
                    Glide.with(getActivity()).load(profilePhoto).into(profile_myphoto);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
            }
        });
    }
    private void getRanking() {
        sharedPreferences = getActivity().getSharedPreferences("email", Context.MODE_PRIVATE);
        String mytoken = sharedPreferences.getString("token","");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        initMyApi = RetrofitClient.getRetrofitInterface();

        //ranking = new ArrayList<Ranking>();
        Call<RankingResponse> call = initMyApi.getRankingResponse("Bearer "+mytoken);
        call.enqueue(new Callback<RankingResponse>() {
            @Override
            public void onResponse(Call<RankingResponse> call, Response<RankingResponse> response) {
                if(response.isSuccessful()) {
                    RankingResponse result = response.body();
                    String status = result.getStatus();
                    List<Ranking> rank = result.getRanking();
                    profile_people.setText(String.valueOf(result.getRanking().size()));

                    if(result.getRanking().size() == 1) {
                        profile_one_name.setText(result.getRanking().get(0).getName());
                        profile_one_walk.setText(String.valueOf(result.getRanking().get(0).getStep_count()));
                        Glide.with(getActivity()).load(result.getRanking().get(0).getProfilePhoto()).into(profile_one_photo);
                    } else if(result.getRanking().size() == 2) {
                        profile_one_name.setText(result.getRanking().get(0).getName());
                        profile_one_walk.setText(String.valueOf(result.getRanking().get(0).getStep_count()));
                        Glide.with(getActivity()).load(result.getRanking().get(0).getProfilePhoto()).into(profile_one_photo);

                        profile_two_name.setText(result.getRanking().get(1).getName());
                        profile_two_walk.setText(String.valueOf(result.getRanking().get(1).getStep_count()));
                        Glide.with(getActivity()).load(result.getRanking().get(1).getProfilePhoto()).into(profile_two_photo);
                    } else if(result.getRanking().size() >= 3) {
                        profile_one_name.setText(result.getRanking().get(0).getName());
                        profile_one_walk.setText(String.valueOf(result.getRanking().get(0).getStep_count()));
                        Glide.with(getActivity()).load(result.getRanking().get(0).getProfilePhoto()).into(profile_one_photo);

                        profile_two_name.setText(result.getRanking().get(1).getName());
                        profile_two_walk.setText(String.valueOf(result.getRanking().get(1).getStep_count()));
                        Glide.with(getActivity()).load(result.getRanking().get(1).getProfilePhoto()).into(profile_two_photo);

                        profile_three_name.setText(result.getRanking().get(2).getName());
                        profile_three_walk.setText(String.valueOf(result.getRanking().get(2).getStep_count()));
                        Glide.with(getActivity()).load(result.getRanking().get(2).getProfilePhoto()).into(profile_three_photo);

                        for(int i = 3; i<result.getRanking().size(); i++) {
                            LinearLayout playout = new LinearLayout(getContext());
                            playout.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams mplayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            mplayout.topMargin = 20;
                            playout.setLayoutParams(mplayout);

                            LinearLayout layout = new LinearLayout(getContext());
                            layout.setOrientation(LinearLayout.HORIZONTAL);
                            layout.setGravity(Gravity.CENTER);
                            LinearLayout.LayoutParams mlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            mlayout.topMargin = 20;
                            layout.setLayoutParams(mlayout);

                            CircleImageView get_image = new CircleImageView(getContext());
                            Glide.with(getActivity()).load(result.getRanking().get(i).getProfilePhoto()).into(get_image);
                            int imgwidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics());
                            int imgheight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics());
                            LinearLayout.LayoutParams mget_image = new LinearLayout.LayoutParams(imgwidth, imgheight);
                            mget_image.gravity = Gravity.CENTER;
                            mget_image.rightMargin = 60;
                            get_image.setLayoutParams(mget_image);
                            layout.addView(get_image);

                            TextView get_rank = new TextView(getContext());
                            get_rank.setText(String.valueOf(result.getRanking().get(i).getRank()));
                            LinearLayout.LayoutParams mget_rank = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            mget_rank.gravity = Gravity.CENTER;
                            get_rank.setLayoutParams(mget_rank);
                            get_rank.setTextColor(Color.BLACK);
                            get_rank.setTextSize(20);
                            layout.addView(get_rank);

                            TextView unit1 = new TextView(getContext());
                            unit1.setText("위");
                            LinearLayout.LayoutParams munit1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            munit1.rightMargin = 45;
                            munit1.gravity = Gravity.CENTER;
                            unit1.setLayoutParams(munit1);
                            unit1.setTextColor(Color.BLACK);
                            unit1.setTextSize(20);
                            layout.addView(unit1);

                            TextView get_name = new TextView(getContext());
                            get_name.setText(result.getRanking().get(i).getName());
                            LinearLayout.LayoutParams mget_name = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            mget_name.rightMargin = 40;
                            mget_name.gravity = Gravity.CENTER;
                            get_name.setLayoutParams(mget_name);
                            get_name.setTextColor(Color.BLACK);
                            get_name.setTextSize(20);
                            layout.addView(get_name);

                            TextView get_walk = new TextView(getContext());
                            get_walk.setText(String.valueOf(result.getRanking().get(i).getStep_count()));
                            LinearLayout.LayoutParams mget_walk = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            mget_walk.gravity = Gravity.CENTER;
                            get_walk.setLayoutParams(mget_walk);
                            get_walk.setTextColor(Color.BLACK);
                            get_walk.setTextSize(20);
                            layout.addView(get_walk);

                            TextView unit2 = new TextView(getContext());
                            unit2.setText("걸음");
                            LinearLayout.LayoutParams munit2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            munit2.rightMargin = 80;
                            munit2.gravity = Gravity.CENTER;
                            unit2.setLayoutParams(munit2);
                            unit2.setTextColor(Color.BLACK);
                            unit2.setTextSize(20);
                            layout.addView(unit2);

                            Button sendbtn = new Button(getContext()); //메시지 전송 버튼 생성
                            sendbtn.setId(i); //버튼 별로 접근 가능하도록 아이디 생성, 0부터 시작
                            sendbtn.setBackgroundResource(R.drawable.send);
                            int btnwidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics());
                            int btnheight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics());
                            LinearLayout.LayoutParams msendbtn = new LinearLayout.LayoutParams(btnwidth, btnheight);
                            msendbtn.gravity = Gravity.CENTER;
                            sendbtn.setLayoutParams(msendbtn);
                            layout.addView(sendbtn);
                            setMessageBtnListener(sendbtn,rank.get(i).getUser_friend_email(),rank.get(i).getName());

                            View v = new View(getContext());
                            LinearLayout.LayoutParams mv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
                            mv.topMargin = 30;
                            v.setLayoutParams(mv);
                            v.setBackgroundResource(R.color.input_register_hint);

                            if(i != result.getRanking().size()-1) {
                                playout.addView(layout);
                                playout.addView(v);
                            } else {
                                playout.addView(layout);
                            }
                            parent_layout.addView(playout);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<RankingResponse> call, Throwable t) {

            }
        });
    }
    private void createTextView() {
        TextView textViewNew = new TextView(getContext());
        textViewNew.setText("");
        textViewNew.setTextSize(18);
        textViewNew.setId(0);
        //LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //textViewNew.setLayoutParams(param);
        //tv.addView(textViewNew);
    }
    private void setMessageBtnListener(Button btn, String userEmail,String name){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = getContext();
                PopupMenu popupMenu = new PopupMenu(mContext,v);

                getActivity().getMenuInflater().inflate(R.menu.message_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.menu1 :
                                showMessagingDialog(true,mContext,userEmail,name);
                                break;
                            case R.id.menu2 :
                                showMessagingDialog(false,mContext,userEmail,name);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    public void showMessagingDialog(boolean typeOfMsg,Context mContext,String userEmail,String name){
        Log.d("showMessagingDialog","In");
        String title = typeOfMsg ? "격려 메시지" : "도발 메시지";
        String[] presetMsg = typeOfMsg ?
                new String[]{
                        "오늘도 열심히 걸어보자!",
                        "지금 산책 하자!",
                        "지금 딱! 산책하기 좋은날씨인데?"
                } :
                new String[]{
                        "산책좀 해 ㄷㅈㅇ~",
                        "ㅋㅋ ㅈ밥",
                        "나였으면 그 시간에 산책이나 했다~"
                };

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(title);

        final int[] index = new int[1];

        // 제목 옆에 들어갈 이이콘 설정 : dialog.setIcon();
        dialog.setSingleChoiceItems(presetMsg, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index[0] = which;
            }
        });
        dialog.setPositiveButton("보내기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                RetrofitClient retrofitClient = RetrofitClient.getNewInstance(getActivity().getApplicationContext());
                initMyApi initMyApi = RetrofitClient.getNewRetrofitInterface();

                FcmMessageRequest fcmMessageRequest = new FcmMessageRequest(userEmail,profile_name.getText()+"님의 "+title,presetMsg[index[0]]);

                initMyApi.FcmMessage(fcmMessageRequest).enqueue(new Callback<FcmMessageResponse>() {
                    @Override
                    public void onResponse(Call<FcmMessageResponse> call, Response<FcmMessageResponse> response) {
                        if(response.isSuccessful()){
                            FcmMessageResponse result = response.body();
                            String status = result.getStatus();
                            String message = result.getMessage();

                            Log.d("Messaging_status",status);
                        }
                    }
                    @Override
                    public void onFailure(Call<FcmMessageResponse> call, Throwable t) {
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("FL","onAttach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("FL","onDestroy");
    }
}