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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends Fragment {


    EditText edt_remail,edt_rname,edt_rpass,edt_cpass;
    Button btn_reg;
    private FirebaseAuth fAuth;

    public Register(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edt_remail = view.findViewById(R.id.edt_regemail);
        edt_rpass = view.findViewById(R.id.edt_regpassword);
        edt_cpass = view.findViewById(R.id.edt_regcpassword);
        edt_rname = view.findViewById(R.id.edt_regname);
        btn_reg = view.findViewById(R.id.btn_register);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkEmptyFields())
                {
                    if (edt_rpass.getText().length()<6)
                    {
                        edt_rpass.setError("Invalid Password, Password Should be at least 6 Characters");
                        edt_rpass.requestFocus();
                    }else
                    {
                        if (!edt_rpass.getText().toString().equals(edt_cpass.getText().toString()))
                        {
                            edt_cpass.setError("Password not Match!");
                            edt_cpass.requestFocus();
                        }else {
                            String email = edt_remail.getText().toString();
                            String pass = edt_rpass.getText().toString();
                            String name = edt_rname.getText().toString();

                            createUser(email,pass,name);
                        }
                    }
                }

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public boolean checkEmptyFields()
    {
        if (TextUtils.isEmpty(edt_remail.getText().toString()))
        {
            edt_remail.setError("Email cannot be blank!");
            edt_remail.requestFocus();
            return true;

        }else if(TextUtils.isEmpty(edt_rpass.getText().toString()))
        {
            edt_rpass.setError("Password cannot be blank!");
            edt_rpass.requestFocus();
            return true;
        }else if(TextUtils.isEmpty(edt_cpass.getText().toString()))
        {
            edt_cpass.setError("Confirm Password cannot be blank!");
            edt_cpass.requestFocus();
            return true;
        }else if(TextUtils.isEmpty(edt_rname.getText().toString()))
        {
            edt_rname.setError("Name cannot be blank!");
            edt_rname.requestFocus();
            return true;
        }


        return false;
    }

    public void createUser(final String email, String pass, final String name)
    {
        fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {

                    FirebaseUser user = fAuth.getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String,Object> usermap = new HashMap<>();
                    usermap.put("Name",name);
                    usermap.put("Email",email);


                    db.collection("user").document(user.getUid()).set(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getActivity().getApplicationContext(),"Registration Success!",Toast.LENGTH_LONG).show();

                            FirebaseAuth.getInstance().signOut();
                            NavController navController = Navigation.findNavController(getActivity(),R.id.host_frag);
                            navController.navigate(R.id.login);
                        }
                    });
                }else
                {
                    System.out.println("Error "+task.getException());
                }

            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
