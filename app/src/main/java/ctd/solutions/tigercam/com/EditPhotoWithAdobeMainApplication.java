package ctd.solutions.tigercam.com;

import android.app.Application;
import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/*
*
 * Created by ash on 3/11/16.
*/


public class EditPhotoWithAdobeMainApplication extends Application implements IAdobeAuthClientCredentials {

/*
 Be sure to fill in the two strings below.
*/

    private static final String CREATIVE_SDK_CLIENT_ID = "fda4f70f903241c79ad353c6215f197f";
    private static final String CREATIVE_SDK_CLIENT_SECRET = "8fd2c8ca-c54f-4a70-b9ba-3f6a12a7ce67";

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

}
