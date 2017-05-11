package ctd.solutions.tigercam.com;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class OfflineImageFragment extends Fragment {
    private Integer itemData;
    ImageView imageView;
    Bitmap imageBitmap;
    ImageView imageView1;
    private Bitmap myBitmap;
    Bitmap orientedBitmap;
    View layoutView;
    private ImageButton btnRestart, btnSave, btnShare;
    private static final String KEY_DRAWABLE = "resId";
    private int mImageNum;



  /*  static ImageFragment newInstance(int imageNum){
        ImageFragment f = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_DRAWABLE, imageNum);
        f.setArguments(args);
        return f;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

      //  mImageNum = getArguments() != null ? getArguments().getInt(KEY_DRAWABLE) : -1;

    }

    /*public void setImageList(Integer integer) {
        this.itemData = integer;
    }*/

    public void setImageList(Integer integer) {
        this.itemData = integer;
    }

    /*public void setImageInViewPager() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            myBitmap = BitmapFactory.decodeResource(getResources(), itemData, options);


            if (options.outWidth > 3000 || options.outHeight > 2000) {
                options.inSampleSize = calculateInSampleSize(options, options.outWidth, options.outHeight);
            } else if (options.outWidth > 2000 || options.outHeight > 1500) {
              //  options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);
            *//*    Display display = getActivity().getWindowManager().getDefaultDisplay();
                int stageWidth = display.getWidth();
                int stageHeight = display.getHeight();

                final int REQUIRED_SIZE= stageHeight*stageWidth;
                int width_tmp=options.outWidth, height_tmp=options.outHeight;
                int scale=1;
                while(true){
                    if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                        break;
                    width_tmp/=2;
                    height_tmp/=2;
                    scale++;
                }*//*

                options.inSampleSize = 1;
            } else if (options.outWidth > 1000 || options.outHeight > 1000) {
                options.inSampleSize = 4;
            }
            options.inJustDecodeBounds = false;
            myBitmap = BitmapFactory.decodeResource(getResources(), itemData,
                    options);
            if (myBitmap != null) {
                try {
                    if (imageView1 != null) {
                        imageView1.setImageBitmap(myBitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
        }
    }*/

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle argsFromMainActivityAfterUserTakesPhoto = getArguments();

        String imagePathStringFromCameraArgs = argsFromMainActivityAfterUserTakesPhoto.getString("photo");

        File fileFromPhotoTakenByUserImagePath = new File(imagePathStringFromCameraArgs);

       // if (fileFromPhotoTakenByUserImagePath.exists())

            Log.d("My ImagePath", "Is Not Null" + fileFromPhotoTakenByUserImagePath.toString());

        Bitmap imageFromCameraBitmap = decodeFile(fileFromPhotoTakenByUserImagePath);

        String imagePath = fileFromPhotoTakenByUserImagePath.getAbsolutePath();
        orientedBitmap = ExifUtil.rotateBitmap(imagePath, imageFromCameraBitmap);

        layoutView = inflater.inflate(R.layout.activity_image_fragment, container,false);
        imageView = (ImageView) layoutView.findViewById(R.id.imageView);
        imageView1 = (ImageView) layoutView.findViewById(R.id.imageView1);
        imageView.setImageBitmap(orientedBitmap);


        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (MainActivity.class.isInstance(getActivity())) {
            final int resId = itemData;
            // Call out to MainActivity to load the bitmap in a background thread
            ((MainActivity) getActivity()).loadBitmap(resId, imageView1);
        }
    }



    private Bitmap decodeFile(File fileFromCameraPhotoTakenByUser) {
        try{
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(fileFromCameraPhotoTakenByUser),null,o);
            //Find the correct scale value. It should be the power of 2.

            Display display = getActivity().getWindowManager().getDefaultDisplay();
           int stageWidth = display.getWidth();
           int stageHeight = display.getHeight();


            final int REQUIRED_SIZE= stageHeight*stageWidth;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(fileFromCameraPhotoTakenByUser), null, o2);
        } catch (FileNotFoundException e) {}


        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myBitmap != null) {
            myBitmap.recycle();
            myBitmap = null;
        }
    }
}