package com.android.example.speedrun.db;

import android.arch.lifecycle.LiveData;
import android.database.sqlite.SQLiteException;
import android.support.test.runner.AndroidJUnit4;

import com.android.example.speedrun.util.TestUtil;
import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.Run;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.android.example.speedrun.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class GameDaoTest extends DbTest {
    @Test
    public void insertAndRead() throws InterruptedException {
        Game game = TestUtil.createGame("id", "bar");
        db.gameDao().insert(game);
        Game loaded = getValue(db.gameDao().load("id"));
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
        Game game = TestUtil.createGame("gameId", "name");
        Run r1 = TestUtil.createRun("runId1", "playerId", "uri", "gameId");
        db.beginTransaction();
        try {
            db.gameDao().insert(game);
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
        Game game = TestUtil.createGame("Id2", "name");
        db.gameDao().insert(game);
        assertThat(db.gameDao().createGameIfNotExists(game), is(-1L));
    }

    @Test
    public void createIfNotExists_doesNotExist() {
        Game game = TestUtil.createGame("Id2", "name");
        assertThat(db.gameDao().createGameIfNotExists(game), is(1L));
    }

    @Test
    public void insertRunThenUpdateGame() throws InterruptedException {
        Game game = TestUtil.createGame("gameId", "name");
        db.gameDao().insert(game);
        Run run = TestUtil.createRun("runId", "playerId", "videoUri", "gameId");
        db.runDao().insert(run);
        LiveData<Run> data = db.runDao().findRunForGame("gameId");
        assertThat(getValue(data), notNullValue());

        Game update = TestUtil.createGame("gameId", "name");
        db.gameDao().insert(update);
        data = db.runDao().findRunForGame("gameId");
        assertThat(getValue(data).id, is("runId"));
        assertThat(getValue(data).gameId, is("gameId"));
    }
}
