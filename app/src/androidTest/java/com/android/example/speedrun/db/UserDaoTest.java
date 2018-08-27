
package com.android.example.speedrun.db;

import android.support.test.runner.AndroidJUnit4;

import com.android.example.speedrun.util.TestUtil;
import com.android.example.speedrun.vo.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.android.example.speedrun.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserDaoTest extends DbTest {

    @Test
    public void insertAndLoad() throws InterruptedException {
        final User user = TestUtil.createUser("userId");
        db.userDao().insert(user);

        final User loaded = getValue(db.userDao().findById(user.id));
        assertThat(loaded.id, is("userId"));

        final User replacement = TestUtil.createUser("user2Id");
        db.userDao().insert(replacement);

        final User loadedReplacement = getValue(db.userDao().findById("user2Id"));
        assertThat(loadedReplacement.id, is("user2Id"));
    }
}
