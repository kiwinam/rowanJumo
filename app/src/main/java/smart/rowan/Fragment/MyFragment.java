package smart.rowan.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import smart.rowan.HomeActivity;
import smart.rowan.LoginActivity;
import smart.rowan.R;
import smart.rowan.databinding.FragmentMyBinding;

import static android.app.Activity.RESULT_OK;
import static smart.rowan.HomeActivity.sRest;
import static smart.rowan.HomeActivity.sUser;

public class MyFragment extends Fragment {
    FragmentMyBinding mMyBinding;
    SharedPreferences myData;
    private static final int PICK_FROM_GALLERY = 1;
    private static final int CROP_FROM_GALLERY = 2;
    private File changed;
    private Uri imageUri;
    private long lastMsg;
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted");
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my, container, false);
        myData = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        String img = myData.getString("profileImg", "0");
        View view = mMyBinding.getRoot();
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        // Set title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("MY");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //set data
        setMyData(sRest.getRestName(), sRest.getRestAddress(), sRest.getRestPhone(), sUser.getFirstName(), sUser.getLastName(),
                sUser.getPhone(), sUser.getBirthday(), sUser.getEmail(), sUser.getAddress(), sUser.getStartDate(), sUser.getEndDate());
        Log.d("rest Phone", sRest.getRestPhone() + "..");
        // Signout and clear the session.
        if (!img.equals("0")) {
            Bitmap bitmap = decodeBase64(img);
            mMyBinding.img.setImageBitmap(bitmap);
        }
        mMyBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });

        mMyBinding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Good bye " + sUser.getFirstName().toUpperCase(), Toast.LENGTH_SHORT).show();
                HashMap<String, Long> tmpHashMap = new HashMap<>();

                if (sUser.getPosition().equals("waiter")) {
                    lastMsg = myData.getLong("last", 0L);
                    Log.d("before logout", lastMsg + "");
                } else if (sUser.getPosition().equals("owner")) {
                    lastMsg = myData.getLong("oLastMsg", 0L);
                }
                tmpHashMap.put(sUser.getId(), lastMsg);
                Log.d("sUserId = " + sUser.getEmail(), "lastMsg" + lastMsg);
                Gson gson = new Gson();
                String hashMapString = gson.toJson(tmpHashMap);
                SharedPreferences tmpData = getActivity().getSharedPreferences("tmpData", Context.MODE_PRIVATE);
                SharedPreferences.Editor tmpEdit = tmpData.edit();
                tmpEdit.putString("tmpString", hashMapString);
                tmpEdit.apply();
                SharedPreferences.Editor editor = myData.edit();
                editor.clear();
                editor.apply();
                HomeActivity.isLogout = true;
                HomeActivity.isHome = false;
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }


    public void setMyData(String restName, String restAddress, String restPhone, String firstName, String lastName, String phone, String birthday, String email, String address, String startDate, String endDate) {
        try {
            mMyBinding.myContentRestaurantName.setText(restName);
            mMyBinding.myContentRestaurantAddr.setText(restAddress);
            mMyBinding.myContentRestaurantCall.setText(PhoneNumberUtils.formatNumber(restPhone));
            mMyBinding.myFullName.setText(firstName.toUpperCase() + " " + lastName.toUpperCase());
            mMyBinding.myContentPhoneTextView.setText(PhoneNumberUtils.formatNumber(phone));
            mMyBinding.myContentBirthday.setText(birthday);
            mMyBinding.myContentEmailTextView.setText(email);
            mMyBinding.myContentAddressTextView.setText(address);
            mMyBinding.myContentStartDateTv.setText(startDate);
            mMyBinding.myContentEndDateTv.setText(endDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_FROM_GALLERY:
                    imageUri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, CROP_FROM_GALLERY);
                    break;
                case CROP_FROM_GALLERY:
                    Bundle cropImage = data.getExtras();
                    if (cropImage != null) {
                        Bitmap bitmap = cropImage.getParcelable("data");
                        SharedPreferences.Editor editor = myData.edit();
                        String imgPath = BitMapToString(bitmap);
                        editor.putString("profileImg", imgPath);
                        editor.apply();
                        mMyBinding.img.setImageBitmap(bitmap);
                    }
                    try {
                        Log.d("imageUri", imageUri + "");
                        String imagePath = getRealPathFromURI(imageUri);
                        Log.d("realPath", imagePath);
                        File origin = new File(imagePath);
                        changed = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "profile" + sUser.getId() + ".jpg");
                        origin.renameTo(changed);
                        Log.d("changedPath", changed.getAbsolutePath());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DoFileUpload(getActivity().getString(R.string.update), changed.toString());
                            }
                        }).start();

                    } catch (Exception e) {
                        e.getMessage();
                    }
                    break;

            }

        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] arr = baos.toByteArray();
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.edit_icon) {
            Snackbar.make(getActivity().findViewById(R.id.bottom_menu_snack), "Edit profile", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            FragmentManager fragmentManager2 = getFragmentManager();
            FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
            MyEditFragment fragment2 = new MyEditFragment();
            fragmentTransaction2.addToBackStack("xyz");
            fragmentTransaction2.hide(MyFragment.this);
            fragmentTransaction2.replace(R.id.view_page, fragment2);
            fragmentTransaction2.commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void DoFileUpload(String apiUrl, String absolutePath) {
        HttpFileUpload(apiUrl, "", absolutePath);
    }

    public void HttpFileUpload(String urlString, String params, String fileName) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            File sourceFile = new File(fileName);
            DataOutputStream dos;
            if (!sourceFile.isFile()) {
                Log.e("uploadFile", "Source File not exist :" + fileName);
            } else {
                FileInputStream mFileInputStream = new FileInputStream(sourceFile);
                URL connectUrl = new URL(urlString);
                Log.d("Test", "mFileInputStream  is " + mFileInputStream);

                // open connection
                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                // write data
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                int bytesAvailable = mFileInputStream.available();
                int maxBufferSize = 1024 * 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];
                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                // read image
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = mFileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                mFileInputStream.close();
                dos.flush(); // finish upload...
                if (conn.getResponseCode() == 200) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    Log.d("line", stringBuffer.toString());
                }
                Log.e("Test", "File is written");
                mFileInputStream.close();
                dos.close();
            }
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            e.printStackTrace();
        }
    }
}