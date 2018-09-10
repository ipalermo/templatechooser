package com.android.example.templatechooser.db;

import android.arch.lifecycle.LiveData;
import android.database.sqlite.SQLiteException;
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
        Design design = TestUtil.createGame("id", "bar");
        db.designDao().insert(design);
        Design loaded = getValue(db.designDao().load("id"));
        assertThat(loaded, notNullValue());
        assertThat(loaded.id, is("id"));
        assertThat(loaded.names, notNullValue());
        assertThat(loaded.names.international, is("bar"));
    }

    @Test
    public void insertRunWithoutGame() {
        Run run = TestUtil.createRun("runId", "playerId", "uri", "gameId");
        try {
            db.runDao().insert(run);
            throw new AssertionError("must fail because game does not exist");
        } catch (SQLiteException ex) {
        }
    }

    @Test
    public void insertRun() throws InterruptedException {
        Design design = TestUtil.createGame("gameId", "name");
        Run r1 = TestUtil.createRun("runId1", "playerId", "uri", "gameId");
        db.beginTransaction();
        try {
            db.designDao().insert(design);
            db.runDao().insert(r1);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Run run = getValue(db.runDao().findRunForGame("gameId"));
        assertThat(run, notNullValue());
        assertThat(run.id, is("runId1"));
        assertThat(run.players.get(0).id, is("playerId"));
        assertThat(run.videos.links.get(0).uri, is("uri"));
        assertThat(run.gameId, is("gameId"));
    }

    @Test
    public void createIfNotExists_exists() throws InterruptedException {
        Design design = TestUtil.createGame("Id2", "name");
        db.designDao().insert(design);
        assertThat(db.designDao().createGameIfNotExists(design), is(-1L));
    }

    @Test
    public void createIfNotExists_doesNotExist() {
        Design design = TestUtil.createGame("Id2", "name");
        assertThat(db.designDao().createGameIfNotExists(design), is(1L));
    }

    @Test
    public void insertRunThenUpdateGame() throws InterruptedException {
        Design design = TestUtil.createGame("gameId", "name");
        db.designDao().insert(design);
        Run run = TestUtil.createRun("runId", "playerId", "videoUri", "gameId");
        db.runDao().insert(run);
        LiveData<Run> data = db.runDao().findRunForGame("gameId");
        assertThat(getValue(data), notNullValue());

        Design update = TestUtil.createGame("gameId", "name");
        db.designDao().insert(update);
        data = db.runDao().findRunForGame("gameId");
        assertThat(getValue(data).id, is("runId"));
        assertThat(getValue(data).gameId, is("gameId"));
    }
}
