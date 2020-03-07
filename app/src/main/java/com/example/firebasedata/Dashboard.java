package com.example.firebasedata;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Dashboard extends Fragment {

    TextView txt_name;
    Button btn_logout,btn_delete;
    FirebaseUser user;
    FirebaseFirestore db;

    public Dashboard() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txt_name = view.findViewById(R.id.txt_dname);
        btn_logout = view.findViewById(R.id.btn_signout);
        btn_delete=view.findViewById(R.id.btn_delete);

        readFireStore();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                NavController navController = Navigation.findNavController(getActivity(),R.id.host_frag);
                navController.navigate(R.id.login);
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //  delUser(view);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = getArguments().getParcelable("user");
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void readFireStore()
    {
        DocumentReference docref = db.collection("user").document(user.getUid());


        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot ds = task.getResult();

                    if (ds.exists())
                    {
                        System.out.println(ds.getData());


                        txt_name.setText("Welcome "+ds.get("Name")+" !");

                    }
                }

            }
        });
    }

    public void delUser(View v)
    {
        final View popupview = getActivity().getLayoutInflater().inflate(R.layout.popupwindow,null);
        final PopupWindow popupWindow=new PopupWindow(popupview,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);

        if (Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }


        final EditText edt_email = popupview.findViewById(R.id.edt_popemail);
        final EditText edt_pass = popupview.findViewById(R.id.edt_poppassword);
        Button btn_log=popupview.findViewById(R.id.btn_poplogin);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),edt_email.getText(),Toast.LENGTH_LONG);
                if(edt_pass.getText().toString().length()<6)
                {
                    edt_pass.setError("Invalid passowrd,password should be at least 6 character");
                    edt_pass.requestFocus();

                }
                else{
                    if(TextUtils.isEmpty(edt_email.getText()))
                    {
                        edt_email.setError("email can not be empty");
                        edt_email.requestFocus();
                    }else if ((TextUtils.isEmpty(edt_pass.getText())))

                {
                    edt_pass.setError("password cannot be empty");
                    edt_pass.requestFocus();
                }else
                    {
                        AuthCredential credential= EmailAuthProvider.getCredential(edt_email.getText().toString(),edt_pass.getText().toString());
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    db.collection("user").document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                        NavOptions navOptions=new NavOptions.Builder().setPopUpTo(R.id.dashboard,true).build();
                                                        NavController navController=Navigation.findNavController(getActivity(),R.id.host_frag);
                                                        navController.navigate(R.id.login,null,navOptions);
                                                        popupWindow.dismiss();
                                                    }}
                                                });

                                            }else
                                            {
                                                System.out.println("delete task:"+task.getException().getMessage());
                                            }
                                        }
                                    });
                                }else
                                {
                                    System.out.println("ReAuth Task:"+task.getException().getMessage());
                                }
                            }
                        });
                    }
            }}
        });
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                popupWindow.showAtLocation(getView(), Gravity.CENTER,0,0);
    }


}
