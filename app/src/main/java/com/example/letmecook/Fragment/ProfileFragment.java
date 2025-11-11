package com.example.letmecook.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Activity.ChangePassWord;
import com.example.letmecook.Activity.LoginActivity;
import com.example.letmecook.Activity.SaveFormulasActivity;
import com.example.letmecook.Activity.SendRequest;
import com.example.letmecook.Activity.UpCoinActivity;
import com.example.letmecook.Activity.UpdateProfileActivity;
import com.example.letmecook.Model.User;
import com.example.letmecook.R;
import com.example.letmecook.cache.CachedUserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProfileFragment extends Fragment {
    private TextView Logout, UpCoin,ChanePass,SenRequest;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    TextView tvNameUser, tvProfilename,tvProfilebirth,tvProfileemail,tvLv,tvCoin, tvSetting,tvSaveOffline,tvnote;
    ImageView imgUser,imageUpdate;

    private List<Long> favorites;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvProfilename = view.findViewById(R.id.tvProFileName);
        tvProfilebirth = view.findViewById(R.id.tvProfileBirth);
        tvProfileemail = view.findViewById(R.id.tvProfileEmail);
        Logout = view.findViewById(R.id.logout);
        tvnote = view.findViewById(R.id.note);
        tvNameUser = view.findViewById(R.id.tvNameUser);
        imgUser = view.findViewById(R.id.imgUser);
        tvLv = view.findViewById(R.id.tvLv);
        tvCoin = view.findViewById(R.id.tvCoinUser);
        SenRequest = view.findViewById(R.id.tvSendRequest);
        UpCoin = view.findViewById(R.id.tvUpCoin);
        tvSetting = view.findViewById(R.id.tvSetting);
        tvSaveOffline = view.findViewById(R.id.tvSaveOffline);
        imageUpdate = view.findViewById(R.id.imgUpdate);
        UserDetail();
        User cacheUser = CachedUserManager.getCurrentUser(getContext());
        String id = cacheUser.getUserId();
        String name = cacheUser.getName();
        String avatarUrl = cacheUser.getAvata();
        String email = cacheUser.getEmail();
        String birth = cacheUser.getBirth();

        imageUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
            intent.putExtra("ID",id);
            intent.putExtra("Name",name);
            intent.putExtra("MAIL",email);
            intent.putExtra("AVATA",avatarUrl);
            intent.putExtra("BIRTH",birth);
            startActivity(intent);
        });

        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassWord.class);
                startActivity(intent);
            }
        });
        tvSaveOffline.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SaveFormulasActivity.class);
            startActivity(intent);
        });
        SenRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendRequest.class);
                startActivity(intent);
            }
        });

        UpCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpCoinActivity.class);
                startActivity(intent);

            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut(); //

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });






        return view;
    }
    private void UserDetail() {
        User cacheUser = CachedUserManager.getCurrentUser(getContext());

        if (cacheUser == null) {

            tvProfilename.setText("Người dùng chưa đăng nhập");
            tvProfileemail.setText("example@email.com");
            tvProfilebirth.setText("dd/mm/yy");
            tvCoin.setText("0");
            tvLv.setText("Đầu bếp tập sự");
            tvNameUser.setText("Khách");
            imgUser.setImageResource(R.drawable.placeholder);
            return;
        }

        String name = cacheUser.getName();
        String avatarUrl = cacheUser.getAvata();
        String email = cacheUser.getEmail();
        String birth = cacheUser.getBirth();
        String lv = "Đầu bếp tập sự";


        Long coin = Long.valueOf(cacheUser.getCoin());
        int coinInt = coin != null ? coin.intValue() : 0;

        tvCoin.setText(String.valueOf(coinInt));
        tvProfileemail.setText(email != null ? email : "Không có email");
        tvProfilename.setText(name != null ? name : "Chưa có tên");
        tvProfilebirth.setText(birth != null ? birth : "dd/mm/yy");
        tvNameUser.setText(name != null ? name : "Khách");
        tvLv.setText(lv);

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(avatarUrl)
                    .transform(new RoundedCorners(30))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imgUser);
        } else {
            imgUser.setImageResource(R.drawable.placeholder);
        }
    }

}
