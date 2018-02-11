package org.briarproject.briar.android.profile;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.briarproject.bramble.api.system.Scheduler;
import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.android.login.SetupActivity;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

/**
 * Created by Laxman on 2/9/2018.
 */

public class ProfileDbTest {


    private ProfileDb profileDb;
    SharedPreferences sharedPrefs = Mockito.mock(SharedPreferences.class);
    private Context context;
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.context = Mockito.mock(Context.class);
        Mockito.when(context.getSharedPreferences("profile_data_file",Context.MODE_PRIVATE)).thenReturn(sharedPrefs);
    }


    @Test
    public void testProfileInfoUpload()
    {
        Mockito.when(context.getSharedPreferences("profile_data_file",Context.MODE_PRIVATE)).thenReturn(sharedPrefs);

        profileDb = new ProfileDb(new MockContext());
        profileDb.writeProfileInfo("John", "Doe", "JD@hotmail.com", "I am a journalist");
        Map<String, String> map = profileDb.readProfileInfo();
        assertEquals(map.get("firstName"), "John");
        assertEquals(map.get("lastName"), "Doe");
        assertEquals(map.get("email"), "JD@hotmail.com");
        assertEquals(map.get("description"), "I am a journalist");
    }
}
