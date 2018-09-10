package com.android.example.templatechooser.api;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.android.example.templatechooser.util.LiveDataCallAdapterFactory;
import com.android.example.templatechooser.vo.Design;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.example.templatechooser.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class RestServiceTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private RestService service;

    private MockWebServer mockWebServer;

    @Before
    public void createService() throws IOException {
        mockWebServer = new MockWebServer();
        service = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(RestService.class);
    }

    @After
    public void stopService() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getDesigns() throws IOException, InterruptedException {
        enqueueResponse("designs.json");
        GetDesignUrlsResponse designs = getValue(service.getDesignUrls()).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/designs?orderby=released&direction=desc&offset=0&max=25"));

        assertThat(designs.getDesignUrls().size(), is(20));

        Design design = designs.getDesignUrls().get(0);
        assertThat(design.names.international, is("! Fishy !"));
        assertThat(design.id, is("k6qqkx6g"));

        Design.Screenshots screenshots = design.assets;
        assertThat(screenshots, notNullValue());
        assertThat(screenshots.cover.uri, is("https://www.speedrun.com/themes/fishy/cover-128.png"));
        assertThat(screenshots.coverLarge.uri, is("https://www.speedrun.com/themes/fishy/cover-256.png"));

        Design design2 = designs.getDesignUrls().get(1);
        assertThat(design2.names.international, is("&meow; (Meow)"));
    }

    @Test
    public void getRun() throws IOException, InterruptedException {
        enqueueResponse("runs.json");
        GetDesignResponse runs = getValue(service.getDesign("k6qqkx6g")).body;
        assertThat(runs.getDesigns().size(), is(1));
        Run first = runs.getDesigns().get(0);
        assertThat(first.id, is("7z0nvdem"));
        assertThat(first.gameId, is("k6qqkx6g"));
        assertThat(first.getDateString(), is("2016/05/24"));
        assertThat(first.players.get(0).id, is("mkj9nw84"));
        assertThat(first.times.seconds, is(435));
        assertThat(first.videos.links.get(0).uri, is("https://youtu.be/-Vesbd8uJzE"));
    }

    private void enqueueResponse(String fileName) throws IOException {
        enqueueResponse(fileName, Collections.emptyMap());
    }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("api-response/" + fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            mockResponse.addHeader(header.getKey(), header.getValue());
        }
        mockWebServer.enqueue(mockResponse
                .setBody(source.readString(StandardCharsets.UTF_8)));
    }
}
