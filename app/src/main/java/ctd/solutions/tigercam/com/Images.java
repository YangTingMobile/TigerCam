package ctd.solutions.tigercam.com;

import java.util.ArrayList;
import java.util.Random;

public class Images {

	private Random randNo;
	private ArrayList<Integer> imageId;

	public Images() {
		imageId = new ArrayList<Integer>();
		imageId.add(R.mipmap.bg_mili_cam_1);
		imageId.add(R.mipmap.bg_mili_cam_2);
		imageId.add(R.mipmap.bg_mili_cam_3);
		imageId.add(R.mipmap.bg_mili_cam_4);
		/*imageId.add(R.drawable.a5);
		imageId.add(R.drawable.a6);
		imageId.add(R.drawable.a7);
		imageId.add(R.drawable.a8);
		imageId.add(R.drawable.a9);*/

	}

	public int getImageId() {
		randNo = new Random();
		return imageId.get(randNo.nextInt(imageId.size()));
	}

	public ArrayList<Integer> getImageItem() {
		return imageId;
	}
}
