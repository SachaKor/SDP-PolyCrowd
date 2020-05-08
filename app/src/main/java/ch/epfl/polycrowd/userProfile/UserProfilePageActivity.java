package ch.epfl.polycrowd.userProfile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.groupPage.GroupsListActivity;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class UserProfilePageActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private User user;
    View view;
    AlertDialog dialog;
    EditText editText;

    private byte[] imageInBytes;

    ImageView userImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(this).inflate(R.layout.activity_user_profile_page, null, false) ;
        setUpViews();
        setContentView(view);
        userImg = findViewById(R.id.imgUser);
        user = PolyContext.getCurrentUser();

    }

    void setUpViews() {
        user = PolyContext.getCurrentUser();
        TextView usernameField = view.findViewById(R.id.profileUserName);
        usernameField.setText(user.getName());
        TextView emailField = view.findViewById(R.id.profileEmail);
        emailField.setText(user.getEmail());
        downloadUserImage();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void downloadUserImage() {
        userImg = findViewById(R.id.imgUser);
        String imgUri = user.getImageUri();
        if(null != imgUri) {
            PolyContext.getDatabaseInterface().downloadUserProfileImage(user, image -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageInBytes = image;
                userImg.setImageBitmap(bmp);
            });
        } else {
           //leave the default pic (ie dont do anything)
        }
    }

    public void onEventsListClick(View view) {
        Intent intent = new Intent(this, UserEventListActivity.class) ;
        startActivity(intent);
    }

    public void onMyGroupsButtonClick(View view) {
        Intent intent = new Intent(this, GroupsListActivity.class) ;
        startActivity(intent);
    }

    public void OnUserProfileEditPasswordButtonClick(View view) {
        TextView textView = (TextView) findViewById(R.id.profilePassword);
        setupDialogForPasswordChangeCredentials(textView);

    }
    public void OnUserProfileEditEmailButtonClick(View view) {
        TextView textView = (TextView) findViewById(R.id.profileEmail);
        setupDialogForEmailChangeCredentials(textView);
    }

    public void OnUserProfileEditUsernameButtonClick(View view) {
        dialog = new AlertDialog.Builder(this).create();
        EditText newUsernameText = new EditText(this);
        newUsernameText.setId(R.id.editTextChangeUsernameUsername);
        //TODO make it so no one can have the same username!!!
        newUsernameText.setHint("Enter new Username");
        TextView textView = (TextView) findViewById(R.id.profilePassword);
        Context context = textView.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(newUsernameText);
        dialog.setView(layout); // Again this is a set method, not add
        dialog.setTitle("Change username");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PolyContext.getDatabaseInterface().
                                updateCurrentUserUsername(newUsernameText.getText().toString(),
                                ()->{
                                    setUpViews();}  );
                    }
                });
        dialog.show();
    }

    public void OnUserProfileEditImgClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * Handles the image picked from the gallery
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK  && requestCode == PICK_IMAGE && data!= null) {

            //data.getData returns the content URI for the selected Image
            Uri imageUri = data.getData();
            userImg.setImageURI(imageUri);
            compressAndSetImage();
            //crop selected image
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Uri selectedImage = result.getUri();
                userImg.setImageURI(selectedImage);
                compressAndSetImage();
            }
        }
    }

    /**
     * - Compresses the image to <= 1Mb
     * - Sets userImg ImageView and imageInBytes attribute
     */
    private void compressAndSetImage() {
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) userImg.getDrawable());
        final int ONE_MEGABYTE = 1024*1024;
        int streamLength = ONE_MEGABYTE;
        int compressQuality = 105;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        while (streamLength >= ONE_MEGABYTE && compressQuality > 5) {
            try {
                bmpStream.flush();//to avoid out of memory error
                bmpStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            compressQuality -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
        }

        imageInBytes = bmpStream.toByteArray();
        // upload the image to the storage
        //TODO update image stored in user!(so dont have to ask database everytime
        //check how sasha did it !
        PolyContext.getDatabaseInterface().uploadUserProfileImage(user, imageInBytes, event -> {
                PolyContext.getDatabaseInterface().updateUser(user, event1 -> {
            });
        });

    }

    // Prompt the user to re-provide their sign-in credentials
    private void setupDialogForPasswordChangeCredentials(TextView textView) {
        //TODO ask to enter new password twice
        dialog = new AlertDialog.Builder(this).create();
        EditText emailText = new EditText(this);
        emailText.setId(R.id.editTextChangePassEmail);
        emailText.setHint("Enter email");

        EditText oldPasswordText= new EditText(this);
        oldPasswordText.setId(R.id.editTextChangePassCurPass);
        oldPasswordText.setHint("enter current password");
        oldPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        EditText newPasswordText = new EditText(this);
        newPasswordText.setId(R.id.editTextChangePassNewPass1);
        newPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPasswordText.setHint("enter new password");

        EditText newPasswordText2 = new EditText(this);
        newPasswordText2.setId(R.id.editTextChangePassNewPass2);
        newPasswordText2.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPasswordText2.setHint("enter new password");

        editText = new EditText(this);
        AuthCredential credential =  EmailAuthProvider
                .getCredential("doesnt matter", "wont work by default");

        Context context = textView.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(emailText);
        layout.addView(oldPasswordText);
        layout.addView(newPasswordText);
        layout.addView(newPasswordText2);
        dialog.setView(layout); // Again this is a set method, not add
        dialog.setTitle("Change password");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("changePass" , newPasswordText.getText().toString());
                        Log.d("changePass" , newPasswordText2.getText().toString());
                        if(newPasswordText.getText().toString().equals(newPasswordText2.getText().toString())) {
                            PolyContext.getDatabaseInterface().reauthenticateAndChangePassword(emailText.getText().toString(),
                                    oldPasswordText.getText().toString(),
                                    newPasswordText.getText().toString(),
                                    getApplicationContext());
                        } else {
                            Toast.makeText(getApplicationContext(), "two new passwords did not match",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog.show();
    }

    private void setupDialogForEmailChangeCredentials(TextView textView) {
        dialog = new AlertDialog.Builder(this).create();
        EditText emailText = new EditText(this);
        emailText.setId(R.id.editTextChangeEmailEmail);
        emailText.setHint("Enter email");
        EditText passwordText= new EditText(this);
        passwordText.setId(R.id.editTextChangeEmailCurPassword);
        passwordText.setHint("enter current password");
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        EditText newEmailText = new EditText(this);
        newEmailText.setId(R.id.editTextChangeEmailNewEmail);
        newEmailText.setHint("enter new email");
        editText = new EditText(this);
        AuthCredential credential =  EmailAuthProvider
                .getCredential("doesnt matter", "wont work by default");

        Context context = textView.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(emailText);
        layout.addView(passwordText);
        layout.addView(newEmailText);
        dialog.setView(layout); // Again this is a set method, not add
        dialog.setTitle("Change Email");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PolyContext.getDatabaseInterface().reauthenticateAndChangeEmail(emailText.getText().toString(),
                                passwordText.getText().toString(),
                                newEmailText.getText().toString(),
                                ()->{
                                    setUpViews();},
                                getApplicationContext());
                    }
                });
        dialog.show();
    }
}
