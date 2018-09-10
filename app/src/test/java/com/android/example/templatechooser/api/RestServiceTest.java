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
import java.util.List;
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
    public void getDesignUrls() throws IOException, InterruptedException {
        enqueueResponse("designs.json");
        List<String> designs = getValue(service.getDesignUrls()).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/api/published_designs"));

        assertThat(designs, notNullValue());
        assertThat(designs.size(), is(40));

        String designUrl = designs.get(0);
        assertThat(designUrl, is(""));

        String designUrl1 = designs.get(1);
        assertThat(designUrl1, is(""));
    }

    @Test
    public void getDesign() throws IOException, InterruptedException {
        enqueueResponse("design.json");
        Design design = getValue(service.getDesign("357")).body;
        assertThat(design.id, is(357));
        assertThat(design.name, is("Amsterdam"));
        assertThat(design.screenshots.medium, is("https://screenshots.dmp.jimdo-server.com?format=medium&ressource=http%3A%2F%2Fapi.dmp.jimdo-server.com%2Fdesigns%2F357%2Fversions%2F2.0.26"));
        assertThat(design.variations.get(0).name, is("Zuidoost"));
        assertThat(design.variations.get(0).screenshots.medium, is("https://screenshots.dmp.jimdo-server.com?format=medium&ressource=http%3A%2F%2Fapi.dmp.jimdo-server.com%2Fdesigns%2F357%2Fversions%2F2.0.26&variation=css%2Fvariation-normal-img-subs.min.css"));
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
