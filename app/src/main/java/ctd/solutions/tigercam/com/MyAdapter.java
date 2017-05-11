package ctd.solutions.tigercam.com;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by steph on 9/1/2016.
 */
public class MyAdapter extends FragmentStatePagerAdapter {

    Bundle fragmentBundle;
//    private ArrayList<Integer> itemData;
    ArrayList<String> itemData = new ArrayList<String>();


    /*public MyAdapter(FragmentManager fragmentManager, Bundle data, ArrayList<Integer> itemData) {
        super(fragmentManager);

        fragmentBundle = data;
        this.itemData = itemData;
    }*/

    public MyAdapter(FragmentManager fragmentManager, Bundle data, ArrayList<String> itemData) {
        super(fragmentManager);

        fragmentBundle = data;
        this.itemData = itemData;
    }

    @Override
    public int getCount() {
        return 1000;
    }

    @Override
    public Fragment getItem(int position) {
        /*if (position > itemData.size()) position = 0;

        ImageFragment imageFragment = new ImageFragment();
        imageFragment.setImageList(itemData.get(position));
        imageFragment.setArguments(fragmentBundle);
        return imageFragment;*/

        switch (position) {
            case 0: // Fragment # 0 - This will shw image
                ImageFragment imageFragment = new ImageFragment();
                imageFragment.setImageList(itemData.get(0));
                imageFragment.setArguments(fragmentBundle);
                return imageFragment;

            case 1: // Fragment # 1 - This will show image
                ImageFragment imageFragment1 = new ImageFragment();
                imageFragment1.setImageList(itemData.get(1));
                imageFragment1.setArguments(this.fragmentBundle);
                return imageFragment1;

            case 2: // Fragment # 1 - This will show image
                ImageFragment imageFragment2 = new ImageFragment();
                imageFragment2.setImageList(itemData.get(2));
                imageFragment2.setArguments(this.fragmentBundle);
                return imageFragment2;

            case 3: // Fragment # 1 - This will show image
                ImageFragment imageFragment3 = new ImageFragment();
                imageFragment3.setImageList(itemData.get(3));
                imageFragment3.setArguments(this.fragmentBundle);
                return imageFragment3;

            /*case 4: // Fragment # 1 - This will show image
                ImageFragment imageFragment4 =  new ImageFragment();
                imageFragment4.setImageList(itemData.get(4));
                imageFragment4.setArguments(this.fragmentBundle);
                return imageFragment4;

            case 5: // Fragment # 1 - This will show image
                ImageFragment imageFragment5 =  new ImageFragment();
                imageFragment5.setImageList(itemData.get(5));
                imageFragment5.setArguments(this.fragmentBundle);
                return imageFragment5;

            case 6: // Fragment # 1 - This will show image
                ImageFragment imageFragment6 =  new ImageFragment();
                imageFragment6.setImageList(itemData.get(6));
                imageFragment6.setArguments(this.fragmentBundle);
                return imageFragment6;

            case 7: // Fragment # 1 - This will show image
                ImageFragment imageFragment7 =  new ImageFragment();
                imageFragment7.setImageList(itemData.get(7));
                imageFragment7.setArguments(this.fragmentBundle);
                return imageFragment7;

            case 8: // Fragment # 1 - This will show image
                ImageFragment imageFragment8 =  new ImageFragment();
                imageFragment8.setImageList(itemData.get(8));
                imageFragment8.setArguments(this.fragmentBundle);
                return imageFragment8;

            case 9: // Fragment # 1 - This will show image
                ImageFragment imageFragment9 =  new ImageFragment();
                imageFragment9.setImageList(itemData.get(9));
                imageFragment9.setArguments(this.fragmentBundle);
                return imageFragment9;*/

            default:
                Log.d("", "no case");
                ImageFragment imageFragment18 =  new ImageFragment();
                Random random = new Random();
//                int n = random.nextInt(itemData.size() - 1)+ 1;
                int n = position % 4;
                imageFragment18.setImageList(itemData.get(n));
                imageFragment18.setArguments(this.fragmentBundle);
                return imageFragment18;
        }
    }
}
