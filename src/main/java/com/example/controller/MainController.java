package com.example.controller;


import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.utils.IOUtility.getByteArrayFromInputStream;

@Controller
@RequestMapping(path = "/")
public class MainController {

    public static final String UPLOAD_AUDIO_LINK = "https://mirror-chat.mybluemix.net/api/uploadAudio";
    private byte[] response;

    /**
     * Method to get index page
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    /**
     * Method to upload user's speech to STT
     *
     * @param wav
     *            bytearray of wav with user's speech
     */
    @RequestMapping(path = "upload", method = RequestMethod.POST)
    public @ResponseBody void sendWAV(@RequestBody byte[] wav) throws IOException {
        URL url = new URL(UPLOAD_AUDIO_LINK);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);

        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Content-Type", "audio/wav");
        DataOutputStream request = new DataOutputStream(httpConn.getOutputStream());
        request.write(wav);
        request.flush();
        request.close();
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new BufferedInputStream(httpConn.getInputStream());
            // saving bytearray of response-wav
            response = getByteArrayFromInputStream(responseStream);
        }
        // TODO: add exception handling
    }


    /**
     * Method to return bot's answer to UI on demand
     *
     */
    @RequestMapping(path = "getResponseAudio", method = RequestMethod.GET)
    public void getTranslationAudio(HttpServletResponse servletResponse) throws IOException {
        // I know that it is not the best implementation, obviously. I just wasn't able to make js
        // playing wav from bytes properly. I believe that with some extra time I would be able to do this.
        IOUtils.copy(new ByteArrayInputStream(response), servletResponse.getOutputStream());
        servletResponse.flushBuffer();
    }

}
