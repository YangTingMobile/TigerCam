package ctd.solutions.tigercam.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends AppCompatActivity implements View.OnLayoutChangeListener {

    private CameraView mCamera;

    private ImageView ivFlashOff, ivFlashOn, ivFlashAuto, ivGallery, ivShutter, ivSelfie;


    private int mCameraWidth;

    private int mCameraHeight;

    private View mContentView, mFlashView;

    private final Handler mHideHandler = new Handler();

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private static final int PERMISSIONS_REQUEST = 200;

    private static final int SELECT_PHOTO = 300;

    private int iFlash, iSelfie = 0;

    private String strImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        checkPermissionsAndRequest();
        initUI();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(10);
    }

    private void initUI() {
        mContentView = findViewById(R.id.rel_camera);
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });

        mCamera = (CameraView) findViewById(R.id.camera);
        mCamera.addOnLayoutChangeListener(this);

        ivFlashOff = (ImageView) findViewById(R.id.flash_off);
        ivFlashOn = (ImageView) findViewById(R.id.flash_on);
        ivFlashAuto= (ImageView) findViewById(R.id.flash_auto);

        mFlashView = (View) findViewById(R.id.flash);
        mFlashView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iFlash == 0) {
                    iFlash = 1;
                    ivFlashOff.setVisibility(View.GONE);
                    ivFlashOn.setVisibility(View.VISIBLE);
                    ivFlashAuto.setVisibility(View.GONE);
                } else if (iFlash == 1) {
                    iFlash = 2;
                    ivFlashOff.setVisibility(View.GONE);
                    ivFlashOn.setVisibility(View.GONE);
                    ivFlashAuto.setVisibility(View.VISIBLE);
                } else if (iFlash == 2) {
                    iFlash = 0;
                    ivFlashOff.setVisibility(View.VISIBLE);
                    ivFlashOn.setVisibility(View.GONE);
                    ivFlashAuto.setVisibility(View.GONE);
                }

                switch (mCamera.toggleFlash()) {
                    case CameraKit.Constants.FLASH_ON:
                        Toast.makeText(CameraActivity.this, "Flash on!", Toast.LENGTH_SHORT).show();
                        break;

                    case CameraKit.Constants.FLASH_OFF:
                        Toast.makeText(CameraActivity.this, "Flash off!", Toast.LENGTH_SHORT).show();
                        break;

                    case CameraKit.Constants.FLASH_AUTO:
                        Toast.makeText(CameraActivity.this, "Flash auto!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        ivGallery = (ImageView) findViewById(R.id.gallery);
        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        ivShutter = (ImageView) findViewById(R.id.shutter);
        ivShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long startTime = System.currentTimeMillis();
                mCamera.setCameraListener(new CameraListener() {
                    @Override
                    public void onPictureTaken(byte[] jpeg) {
                        super.onPictureTaken(jpeg);
                        long callbackTime = System.currentTimeMillis();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                        if (iSelfie == 1) {
                            Matrix matrix = new Matrix();
                            matrix.preScale(-1.0f, 1.0f);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        } else {

                        }
                        ResultHolder.dispose();
                        ResultHolder.setImage(bitmap);
                        ResultHolder.setNativeCaptureSize(mCamera.getCaptureSize());
                        ResultHolder.setTimeToCallback(callbackTime - startTime);

                        if (bitmap != null) {
                            if (bitmap.getWidth() > bitmap.getHeight()) {
                                Bitmap bMapRotate = null;
                                Matrix mat = new Matrix();
                                mat.postRotate(90);
                                bMapRotate = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), mat, true);
                                bitmap.recycle();
                                bitmap = null;
                                onCaptureImageResult(bMapRotate);
                            } else {
                                onCaptureImageResult(bitmap);
                            }
                        }
                    }
                });
                mCamera.captureImage();
                finishTakePicture();
            }
        });

        ivSelfie = (ImageView) findViewById(R.id.selfie);
        ivSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCamera.toggleFacing()) {
                    case CameraKit.Constants.FACING_BACK:
                        iSelfie = 0;
                        break;
                    case CameraKit.Constants.FACING_FRONT:
                        iSelfie = 1;
                        break;
                }
            }
        });

        updateCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        delayedHide(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mCamera.start();
    }

    @Override
    protected void onPause() {
        mCamera.stop();
        super.onPause();
    }

    private void updateCamera() {
        ViewGroup.LayoutParams cameraLayoutParams = mCamera.getLayoutParams();
        int width = cameraLayoutParams.width;
        int height = cameraLayoutParams.height;

        width = ViewGroup.LayoutParams.MATCH_PARENT;
        height = ViewGroup.LayoutParams.MATCH_PARENT;

        cameraLayoutParams.width = width;
        cameraLayoutParams.height = height;

        mCamera.addOnLayoutChangeListener(this);
        mCamera.setLayoutParams(cameraLayoutParams);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        mCameraWidth = right - left;
        mCameraHeight = bottom - top;

        mCamera.removeOnLayoutChangeListener(this);
    }

    private void hide() {
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, 10);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private boolean checkPermissionsAndRequest() {
        int permissionToUseCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionToUseCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow

                        //else any one or both the permissions are not granted
                    } else {
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Camera and Write External Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkPermissionsAndRequest();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == SELECT_PHOTO) && (resultCode == Activity.RESULT_OK)) {
            Uri selectedImage = data.getData();
            String selectedImagePath;
            try {
                selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImage);
                strImagePath = selectedImagePath;

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ResultHolder.setImage(bitmap);
                    onSelectImageResult(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                intent.putExtra("Method", Integer.toString(1));
                intent.putExtra("Path", strImagePath);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void finishTakePicture() {
        MediaActionSound soundShutter = new MediaActionSound();
        soundShutter.play(MediaActionSound.SHUTTER_CLICK);
    }

    private void onCaptureImageResult(Bitmap thumbnail) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File fSdcard = Environment.getExternalStorageDirectory();
        File fFolder = new File(fSdcard.getAbsoluteFile(), "TigerCam");//the dot makes this directory hidden to the user
        if (!fFolder.exists()) {
            fFolder.mkdir();
        }
        File destination = new File(fFolder.getAbsoluteFile(), "Capture.jpg");
        if (destination.exists()) {
            destination.delete();
        }

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        strImagePath = fFolder.getAbsoluteFile() + "/Capture.jpg";

        Intent intent = new Intent(CameraActivity.this, MainActivity.class);
        intent.putExtra("Method", Integer.toString(0));
        intent.putExtra("Path", strImagePath);
        startActivity(intent);
    }

    private void onSelectImageResult(Bitmap thumbnail) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File fSdcard = Environment.getExternalStorageDirectory();
        File fFolder = new File(fSdcard.getAbsoluteFile(), "TigerCam");//the dot makes this directory hidden to the user
        if (!fFolder.exists()) {
            fFolder.mkdir();
        }
        File destination = new File(fFolder.getAbsoluteFile(), "Capture.jpg");
        if (destination.exists()) {
            destination.delete();
        }

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
