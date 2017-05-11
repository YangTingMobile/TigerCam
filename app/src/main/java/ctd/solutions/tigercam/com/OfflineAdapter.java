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
public class OfflineAdapter extends FragmentStatePagerAdapter {

    Bundle fragmentBundle;
    private ArrayList<Integer> itemData;


    public OfflineAdapter(FragmentManager fragmentManager, Bundle data, ArrayList<Integer> itemData) {
        super(fragmentManager);

        fragmentBundle = data;
        this.itemData = itemData;
    }


    @Override
    public int getCount() {
        return itemData.size();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will shw image
                OfflineImageFragment imageFragment = new OfflineImageFragment();
                imageFragment.setImageList(itemData.get(0));
                imageFragment.setArguments(fragmentBundle);
                return imageFragment;

            case 1: // Fragment # 1 - This will show image
                OfflineImageFragment imageFragment1 = new OfflineImageFragment();
                imageFragment1.setImageList(itemData.get(1));
                imageFragment1.setArguments(this.fragmentBundle);
                return imageFragment1;

            case 2: // Fragment # 1 - This will show image
                OfflineImageFragment imageFragment2 = new OfflineImageFragment();
                imageFragment2.setImageList(itemData.get(2));
                imageFragment2.setArguments(this.fragmentBundle);
                return imageFragment2;

            case 3: // Fragment # 1 - This will show image
                OfflineImageFragment imageFragment3 = new OfflineImageFragment();
                imageFragment3.setImageList(itemData.get(3));
                imageFragment3.setArguments(this.fragmentBundle);
                return imageFragment3;

            default:
                Log.d("", "no case");
                OfflineImageFragment imageFragment18 =  new OfflineImageFragment();
                Random random = new Random();
                int n = random.nextInt(3)+ 1;
                imageFragment18.setImageList(itemData.get(0));
                imageFragment18.setArguments(this.fragmentBundle);
                return imageFragment18;
        }
    }
}
