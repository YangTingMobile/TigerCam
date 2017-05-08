package ctd.solutions.tigercam.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private static final int PERMISSIONS_REQUEST = 200;
    private static final int SELECT_PHOTO = 300;
    MyAdapter mAdapter;
    MyAdapter cAdapter;
    MyAdapter eAdapter;
    ViewPager mPager;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private Images imageId;
    private ArrayList<Integer> itemData;
    private int position = 0, totalImage;
    private ImageButton btnRestart, btnSave, btnShare, btnEdit;
    private ImageView ivPhoto;
    private Button btnSelectFromGallery;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    public static final String TAG = "MainActivity";
    File image;
    String strMethod, strImagePath;

    private View mContentView;
    private final Handler mHideHandler = new Handler();

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContentView = findViewById(R.id.layout);

        if (checkPermissionsAndRequest()) {
//            dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
        }

        getData();

        imageId = new Images();
        itemData = imageId.getImageItem();
        totalImage = itemData.size();

        btnRestart = (ImageButton) findViewById(R.id.btnRestart);
        btnSave = (ImageButton) findViewById(R.id.btnSave);
        btnShare = (ImageButton) findViewById(R.id.btnShare);

        //  btnSelectFromGallery = (Button) findViewById(R.id.btnSelectFromGallery);
        btnEdit = (ImageButton) findViewById(R.id.btnEdit);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    saveImageToGallery();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Intent intent = getIntent();
                finish();
                startActivity(intent);*/

//                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
//                startActivity(intent);

                try {
                    if (strMethod.equals("0")) {
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

 /*       btnSelectFromGallery.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                try {
                    selectFromGallery();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/

        btnEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editPhotowithAdobe(mCurrentPhotoPath);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String d = null;
                    shareImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        if (strMethod.equals("0")) {
            mCurrentPhotoPath = strImagePath;
            if (mCurrentPhotoPath != null) {
                Log.d("This is the Path/n/n", mCurrentPhotoPath);
                getPhotoFile(mCurrentPhotoPath);

                String file = getPhotoFile(mCurrentPhotoPath);

                Bundle bundle = new Bundle();
                bundle.putString("photo", file);

                mAdapter = new MyAdapter(getSupportFragmentManager(), bundle, itemData);
                mPager = (ViewPager) findViewById(R.id.pager);
                mPager.setAdapter(mAdapter);
            }
        } else {
            mCurrentPhotoPath = strImagePath;
            Bundle bundle1 = new Bundle();
            bundle1.putString("photo", strImagePath);

            cAdapter = new MyAdapter(getSupportFragmentManager(), bundle1, itemData);
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(cAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        delayedHide(10);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(10);
    }

    private void getData() {
        Intent receiveIntent = getIntent();
        strMethod = receiveIntent.getStringExtra("Method");
        strImagePath = receiveIntent.getStringExtra("Path");
    }

    public void loadBitmap(int resId, ImageView mImageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(mImageView);
        task.execute(resId);
    }

    private void shareImage() throws IOException {

        /*Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image");

        // Make sure you put example png image named myImage.png in your
        // directory

        String imagePath = Environment.getExternalStorageDirectory() + "/Pictures/TigerCam";
        File imageFileToShare = new File(imagePath);

        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(share, "Share Image!"));*/

        Bitmap mBitmap = null;
        mBitmap = ResultHolder.getImage();
        if (mBitmap == null) {
            mBitmap = getPhotoBitmap();
        }
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Laurie Hernandez", null);
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
        emailIntent1.setType("image/png");
        startActivity(emailIntent1);
    }

    private void editPhotowithAdobe(String photoPath) {

         /* 1) Make a new Uri object (Replace this with a real image on your device) */
//        Uri imageUri = Uri.parse(photoPath);

        File fSdcard = Environment.getExternalStorageDirectory();
        File fFolder = new File(fSdcard.getAbsoluteFile(), "TigerCam");
        String strPath = fFolder.getAbsoluteFile() + "/Capture.jpg";
        File fPhoto = new File(strPath);

        Uri imageUri = Uri.fromFile(fPhoto);
        /* 2) Create a new Intent */
        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                .setData(imageUri)
                .build();

        /* 3) Start the Image Editor with request code 1 */
        startActivityForResult(imageEditorIntent, 4);
    }

    private void selectFromGallery() throws IOException {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
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

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            Log.d("This is the Path/n/n", mCurrentPhotoPath);
            getPhotoFile(mCurrentPhotoPath);

            String file = getPhotoFile(mCurrentPhotoPath);

            Bundle bundle = new Bundle();
            bundle.putString("photo", file);

            mAdapter = new MyAdapter(getSupportFragmentManager(), bundle, itemData);
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);
        }
    }

    public String getPhotoFile(String picFile) {
        return picFile;
    }

    private Bitmap getPhotoBitmap() {
        Bitmap mBitmap = null;
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        System.out.println(root + " Root value in saveImage Function");
        File myDir = new File(root + "/TigerCam");

        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String iname = "Image-" + n + ".jpg";

        File file = new File(myDir, iname);
        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);


            View v1 = mPager.getRootView().findViewById(R.id.pager);
            v1.setDrawingCacheEnabled(true);
            mBitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

            out.flush();
            out.close();

            Toast.makeText(this, "Image is Saved in Tiger Cam Album in Gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    private File saveImageToGallery() throws IOException {

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        System.out.println(root + " Root value in saveImage Function");
        File myDir = new File(root + "/TigerCam");

        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String iname = "Image-" + n + ".jpg";

        File file = new File(myDir, iname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);


            Bitmap bitmap;
            View v1 = mPager.getRootView().findViewById(R.id.pager);
            v1.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

            out.flush();
            out.close();

            Toast.makeText(this, "Image is Saved in Tiger Cam Album in Gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);

                    }
                });
        String Image_path = Environment.getExternalStorageDirectory() + "/Pictures/TigerCam/" + iname;

        File[] files = myDir.listFiles();
        int numberOfImages = files.length;
        System.out.println("Total images in Folder " + numberOfImages);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String fileName = "image" + timeStamp;

        File direct = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        image = File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                direct      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
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
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "CAMERA & WRITE EXTERNAL STORAGE permission granted");
                        // process the normal flow
                        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
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
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        setPage(position);
    }

    private void setPage(int page) {
        if (page == 0 && totalImage > 0) {
        } else if (page == totalImage - 1 && totalImage > 0) {
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ACTION_TAKE_PHOTO_B) {
            handleBigCameraPhoto();
        }

        if (resultCode == RESULT_OK && requestCode == SELECT_PHOTO) {
            String selectedImagePath;
            Uri selectedImage = data.getData();
            selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImage);
            Log.d("", "Image Path" + selectedImagePath);
            handleSelectPhoto(selectedImagePath);
        }

        if (resultCode == RESULT_OK && requestCode == 4) {
            Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
            String adobeImagePath = ImageFilePath.getPath(getApplicationContext(), editedImageUri);
            handleAdobePhoto(adobeImagePath);
        }
    }

    private void handleAdobePhoto(String path) {
        Bundle bundle2 = new Bundle();
        bundle2.putString("photo", path);
        eAdapter = new MyAdapter(getSupportFragmentManager(), bundle2, itemData);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(eAdapter);
    }

    private void handleSelectPhoto(String path) {
        Bundle bundle1 = new Bundle();
        bundle1.putString("photo", path);

        cAdapter = new MyAdapter(getSupportFragmentManager(), bundle1, itemData);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(cAdapter);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        Bitmap b;


        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            Display display = getWindowManager().getDefaultDisplay();
            int stageWidth = display.getWidth();
            int stageHeight = display.getHeight();

            return decodeSampledBitmapFromResource(getResources(), data, stageWidth, stageHeight);
        }

        public Bitmap decodeSampledBitmapFromResource(Resources res,int resId, int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(res, resId, options);

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeResource(res, resId, options);

        }

        private int calculateInSampleSize(BitmapFactory.Options options, int imageWidth, int imageHeight) {

            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > imageHeight || width > imageWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= imageHeight
                        && (halfWidth / inSampleSize) >= imageWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        Display display = getWindowManager().getDefaultDisplay();
        int stageWidth = display.getWidth();
        int stageHeight = display.getHeight();

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    getResizedBitmap(bitmap, stageHeight, stageWidth);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

            int width = bm.getWidth();

            int height = bm.getHeight();

            float scaleWidth = ((float) newWidth) / width;

            float scaleHeight = ((float) newHeight) / height;

// CREATE A MATRIX FOR THE MANIPULATION

            Matrix matrix = new Matrix();

// RESIZE THE BIT MAP

            matrix.postScale(scaleWidth, scaleHeight);

// RECREATE THE NEW BITMAP

            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

            return resizedBitmap;

        }

    }

    private void hide() {
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, 10);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}