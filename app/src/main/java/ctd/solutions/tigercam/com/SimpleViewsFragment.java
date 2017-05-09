package ctd.solutions.tigercam.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.cleveroad.splittransformation.SquareViewPagerIndicator;
import com.cleveroad.splittransformation.TransformationAdapterWrapper;

/**
 * Fragment with inner view pager and implementation of pager adapter with views.
 */
public class SimpleViewsFragment extends Fragment {

    public static SimpleViewsFragment instance() {
        return new SimpleViewsFragment();
    }

    private ViewPager viewPager;
   // private SquareViewPagerIndicator indicator;
    private Button btnSkip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
      //  indicator = (SquareViewPagerIndicator) view.findViewById(R.id.indicator);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSkip = (Button) view.findViewById(R.id.skip);

        SimplePagerAdapter adapter = new SimplePagerAdapter(getContext());
        TransformationAdapterWrapper wrapper = TransformationAdapterWrapper
                .wrap(getContext(), adapter)
                .rows(10)
                .columns(7)
                .marginTop(getResources().getDimensionPixelSize(R.dimen.margin_top))
                .bitmapScale(1f)
                .build();
        viewPager.setAdapter(wrapper);
        viewPager.setPageTransformer(false, wrapper);
      //  indicator.initializeWith(viewPager);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               skipToMainActivity();
            }
        });
    }

    private void skipToMainActivity() {
//        Intent goToMainActivityIntent = new Intent(getActivity(), MainActivity.class);
        Intent goToMainActivityIntent = new Intent(getActivity(), CameraActivity.class);
        goToMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goToMainActivityIntent);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
       // indicator.reset();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.simple_views);
    }

    private static class SimplePagerAdapter extends PagerAdapter {

        private final int[] drawables = new int[] {
                R.drawable.android_tutorial
              };

        private final Context context;
        private final LayoutInflater inflater;

        public SimplePagerAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return drawables.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageDrawable(ContextCompat.getDrawable(context, drawables[position]));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}