package com.example.firebasedata;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends Fragment implements View.OnClickListener {

    private FirebaseAuth fAuth;
    FirebaseUser curUser;
    EditText edt_email,edt_pass;
    TextView txt_reg;
    Button btn_log;

    public Login(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        edt_email = view.findViewById(R.id.edt_logemail);
        edt_pass = view.findViewById(R.id.edt_logpasswrod);
        txt_reg = view.findViewById(R.id.txt_register);

        btn_log = view.findViewById(R.id.btn_login);

        txt_reg.setOnClickListener(this);
        btn_log.setOnClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btn_login)
        {
            if(TextUtils.isEmpty(edt_email.getText().toString()))
            {
                edt_email.setError("Email can not be blank!");
                edt_email.requestFocus();
            }else if (TextUtils.isEmpty(edt_pass.getText().toString()))
            {
                edt_pass.setError("Password can not be blank!");
                edt_pass.requestFocus();
            }else {
                String email = edt_email.getText().toString();
                String pass = edt_pass.getText().toString();

                loginUser(email,pass);
            }



        }else if (id == R.id.txt_register)
        {
            NavController navController = Navigation.findNavController(getActivity(),R.id.host_frag);

            navController.navigate(R.id.register);
        }



    }

    @Override
    public void onStart() {
        super.onStart();

        curUser = fAuth.getCurrentUser();

        if (curUser != null)
        {
            Toast.makeText(getActivity().getApplicationContext(),"User Already Singing",Toast.LENGTH_LONG).show();
            updateUI(curUser);
        }

    }

    public void loginUser(String email, String password)
    {
        fAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            curUser = fAuth.getCurrentUser();
                            //start intent
                            Toast.makeText(getActivity().getApplicationContext(),"Login Success!",Toast.LENGTH_LONG).show();
                            updateUI(curUser);
                        }


                    }
                }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateUI(FirebaseUser user)
    {
        NavController navController = Navigation.findNavController(getActivity(),R.id.host_frag);
        Bundle b = new Bundle();
        b.putParcelable("user",user);
        navController.navigate(R.id.dashboard,b);
    }
}
