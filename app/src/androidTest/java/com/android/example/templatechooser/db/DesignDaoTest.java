package com.android.example.templatechooser.db;

import android.support.test.runner.AndroidJUnit4;

import com.android.example.templatechooser.util.TestUtil;
import com.android.example.templatechooser.vo.Design;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.android.example.templatechooser.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DesignDaoTest extends DbTest {
    @Test
    public void insertAndRead() throws InterruptedException {
        Design design = TestUtil.createDesign(1, "bar");
        db.designDao().insert(design);
        Design loaded = getValue(db.designDao().load("1"));
        assertThat(loaded, notNullValue());
        assertThat(loaded.id, is("1"));
        assertThat(loaded.name, notNullValue());
        assertThat(loaded.name, is("bar"));
    }


    @Test
    public void insertDesign() throws InterruptedException {
        Design design = TestUtil.createDesign(1, "name");
        db.beginTransaction();
        try {
            db.designDao().insert(design);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        assertThat(design, notNullValue());
        assertThat(design.id, is(1));
        assertThat(design.name, is("name"));
    }

    @Test
    public void createIfNotExists_exists() throws InterruptedException {
        Design design = TestUtil.createDesign(1, "name");
        db.designDao().insert(design);
        assertThat(db.designDao().createDesignIfNotExists(design), is(-1L));
    }

    @Test
    public void createIfNotExists_doesNotExist() {
        Design design = TestUtil.createDesign(2, "name");
        assertThat(db.designDao().createDesignIfNotExists(design), is(1L));
    }
}
