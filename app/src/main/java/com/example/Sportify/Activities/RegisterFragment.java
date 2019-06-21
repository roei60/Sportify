package com.example.Sportify.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.navigation.Navigation;


import com.example.Sportify.R;
import com.example.Sportify.models.User;
import com.example.Sportify.utils.FileUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    boolean mIsSignIn=true;
    EditText mEmailtxt;
    EditText mPasswordtxt;
    EditText mNameTxt;
    Button mRegisterbtn;
    TextView mTitletxt;
    TextView mSwitchRegSignIntxt;
    ProgressDialog mProgressDialog;
    FloatingActionButton mAddPictureBt;
    ImageView mUserImageView;
    Uri mUserImageUri;
    ImageView mLogoImageView;

    String mName;
    String mEmail;
    String mPassword;
    Boolean mIsPro;

    FirebaseAuth mFireBashAuth;
    DatabaseReference mDatabaseRef;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register, container, false);

        getActivity().setTitle("Login");

        mProgressDialog= new ProgressDialog(getActivity());
        mFireBashAuth=FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        mLogoImageView = view.findViewById(R.id.register_logo_image_view);
        //mTitletxt = view.findViewById(R.id.Register_Titlettxt);
        mNameTxt = view.findViewById(R.id.Register_name_txt);
        mEmailtxt = view.findViewById(R.id.Register_emailtxt);
        mPasswordtxt = view.findViewById(R.id.Register_passwordtxt);
        mRegisterbtn = view.findViewById(R.id.Register_registerBtn);
        mSwitchRegSignIntxt = view.findViewById(R.id.Register_switchRegisterSignIn);
        mAddPictureBt = view.findViewById(R.id.register_add_picture_bt);
        mUserImageView = view.findViewById(R.id.register_user_image_view);
        mUserImageUri = null;



        mAddPictureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture();
            }
        });

        mSwitchRegSignIntxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsSignIn) {
                    mRegisterbtn.setText("Register");
                    mNameTxt.setVisibility(View.VISIBLE);
                    mUserImageView.setVisibility(View.VISIBLE);
                    mAddPictureBt.show();
                    mLogoImageView.setVisibility(View.GONE);
                    mSwitchRegSignIntxt.setText("have an account? Sign in here");
                    getActivity().setTitle("Register");
                }
                else {
                    mRegisterbtn.setText("Login");
                    mNameTxt.setVisibility(View.GONE);
                    mUserImageView.setVisibility(View.GONE);
                    mAddPictureBt.hide();
                    mLogoImageView.setVisibility(View.VISIBLE);
                    mSwitchRegSignIntxt.setText("Not have an account? Register here");
                    getActivity().setTitle("Login");
                }
                mIsSignIn=!mIsSignIn;
            }
        });
        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPro = false;// mIsProCb.isChecked();
                mName = mNameTxt.getText().toString();
                mEmail = mEmailtxt.getText().toString().trim();
                mPassword = mPasswordtxt.getText().toString().trim();

                if(!isFormValid(mName, mEmail, mPassword))
                    return;

                mProgressDialog.setMessage("Registering user...");
                mProgressDialog.show();
                if(!mIsSignIn) {
                    mProgressDialog.setMessage("Registering user...");
                    mProgressDialog.show();
                    mFireBashAuth.createUserWithEmailAndPassword(mEmail, mPassword).
                            addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // if user image chosen - upload to firebase sorage
                                        if (mUserImageUri != null)
                                            uploadFile();
                                        else
                                            saveUser(null);
                                    } else {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Registeraion Failed! pls try again later...", Toast.LENGTH_LONG).show();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else {
                    mProgressDialog.setMessage("Login user...");
                    mProgressDialog.show();
                    mFireBashAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                final FirebaseUser currentUser = mFireBashAuth.getCurrentUser();
                                Map<String, Object> tokenMap = new HashMap<>();
                                tokenMap.put("token", FirebaseInstanceId.getInstance().getToken());
                                mDatabaseRef.child(currentUser.getUid()).updateChildren(tokenMap);

                                mProgressDialog.dismiss();
                                //register completed and logged in.
                                Toast.makeText(getActivity(), "Sign In Successfull!", Toast.LENGTH_LONG).show();
                                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_cardsListFragment);
                            } else {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), "Sign in Failed! pls try again later...", Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                }


            }
        });
        return view;
    }

    private void selectProfilePicture()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setRequestedSize(1024, 1024, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .start(getActivity(),this);
    }

    private void uploadFile()
    {
        String userId = mFireBashAuth.getCurrentUser().getUid();
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference("Uploads/" + userId + "/ProfilePics")
                .child(System.currentTimeMillis() + "." + FileUtils.getFileExtension(mUserImageUri));
        UploadTask uploadTask = fileRef.putFile(mUserImageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(getActivity(), "Upload successfully!", Toast.LENGTH_SHORT).show();
                    saveUser(downloadUri.toString());
                } else {
                    Toast.makeText(getActivity(), "Something got wrong.. pls try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveUser(String imageUri)
    {
        // create user in DB
        final FirebaseUser currentUser = mFireBashAuth.getCurrentUser();
        User user = new User(mName, mEmail, FirebaseInstanceId.getInstance().getToken(), imageUri);
        String userId = currentUser.getUid();
        mDatabaseRef.child(userId).setValue(user);
        mProgressDialog.dismiss();
        //register completed and logged in.
        Toast.makeText(getActivity(), "Registeraion Successfull!", Toast.LENGTH_LONG).show();
        Navigation.findNavController(this.getView()).navigate(R.id.action_registerFragment_to_cardsListFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        try {
            // handle result of CropImageActivity
            if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    mUserImageUri = result.getUri();
                    Picasso.with(this.getContext()).load(mUserImageUri).fit().into(mUserImageView);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this.getActivity(), "Profile cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean isFormValid(String name, String email, String password){
        if (email.isEmpty() || !VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            Toast.makeText(getActivity(), "Email or Password is not valid!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (password.isEmpty() || password.length()<6){
            Toast.makeText(getActivity(), "Must enter at least 6 chars length password!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!mIsSignIn && name.isEmpty()) {
            Toast.makeText(getActivity(), "Must enter your name!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      //  if (context instanceof OnFragmentInteractionListener) {
       //     mListener = (OnFragmentInteractionListener) context;
        //} else {
          //  throw new RuntimeException(context.toString()
            //        + " must implement OnFragmentInteractionListener");
      //  }
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
}
