/*
The MIT License (MIT)

Copyright (c) 2015 Brahim Arkni

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.ginger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Java wrapper of Ginger Proofreader which corrects spelling
 * and grammar mistakes based on the context of complete sentences
 * by comparing each sentence to billions of similar sentences from
 * the web.
 *
 * Currently, Ginger ProofReader only supports English correction.
 *
 * @author Brahim Arkni <brahim.arkni@gmail.com>
 *
 */
public class Ginger4J {
    private static List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    private static final String USER_AGENT = "Mozilla/5.0";
    private JSONObject correction;
    private String baseURL = "http://services.gingersoftware.com/Ginger/correct/json/GingerTheText";

    static {
        // Default values
        // Adding the clientVersion
        parameters.add(new BasicNameValuePair("clientVersion", "2.0"));
        // The lang
        // Currently, Ginger ProofReader only supports English correction.
        parameters.add(new BasicNameValuePair("lang", "US"));
        // & the apiKey
        parameters.add(new BasicNameValuePair("apiKey", "6ae0c3a0-afdc-4532-a810-82ded0054236"));
    }

    /**
     * Default constructor
     */
    public Ginger4J() {
    }

    /**
     * Constructor taken the base URL and a list of params.
     *
     * @param baseURL
     *            The Ginger web service url (can be null).
     * @param parameters
     *            The list of parameters (clientVersion, lang, apiKey) can be null.
     */
    public Ginger4J(String baseURL, List<NameValuePair> parameters) {
        if(baseURL != null) {
            this.baseURL = baseURL;
        }

        if (parameters != null) {
            Ginger4J.parameters = this.merge(Ginger4J.parameters, parameters);
        }
    }

    /**
     * Merge two list of NameValuePair into one new list.
     *
     * @param parameters
     *            The default list of parameters.
     * @param params
     *            The new parameters given by user.
     *
     * @return List<NameValuePair>
     *            The resulting List after merging the similar parameters.
     */
    private List<NameValuePair> merge(List<NameValuePair> parameters, List<NameValuePair> params) {
        List<NameValuePair> mergedList = new ArrayList<NameValuePair>();
        boolean found = false;

        for (NameValuePair pair : parameters) {
            found = false;
            for (NameValuePair valuePair : params) {
                if (pair.getName().equals(valuePair.getName())) {
                    mergedList.add(valuePair);
                    found = true;
                    break;
                }
            }

            if (!found) {
                mergedList.add(pair);
            }
        }

        return mergedList;
    }

    /**
     * Process the suggested correction from Ginger ProofReader.
     *
     * @param text
     *            The text that should be corrected.
     * @param suggestions
     *            The suggested correction.
     *
     * @return JSONObject
     *            A JSON object that contains the text, the result & the suggested corrections.
     */
    private JSONObject processSuggestions(String text, JSONObject suggestions) {
        int start, end;
        int i = 0;

        JSONArray corrections = new JSONArray();
        JSONArray textResult = suggestions.getJSONArray("LightGingerTheTextResult");

        JSONObject lightGinger, suggestion;

        Map<String, Object> map = new HashMap<String, Object>();

        String result = "";

        for (int index = 0; index < textResult.length(); index++) {
            lightGinger = textResult.getJSONObject(index);
            start = lightGinger.getInt("From");
            end = lightGinger.getInt("To");
            suggestion = lightGinger.getJSONArray("Suggestions").getJSONObject(0);

            if (i <= end) {
                if (start != 0) {
                    result += text.substring(i, start) ;
                }
                result += suggestion.getString("Text");

                map.put("text", text.substring(start, (end + 1)));
                map.put("correct", !suggestion.getString("Text").isEmpty() ? suggestion.getString("Text") : "");
                map.put("definition", suggestion.has("Definition") && !suggestion.getString("Definition").isEmpty() ? suggestion.getString("Definition") : "");
                map.put("start", start);
                map.put("length", (end + 1) - start);

                corrections.put(new JSONObject(map));
                map.clear();
            }

            i = end + 1;
        }

        if (i < text.length()) {
            result += text.substring(i);
        }

        map.put("text", text);
        map.put("result", result);
        map.put("corrections", corrections);

        return new JSONObject(map);
    }

    /**
     * Return the correction.
     *
     * @return String
     *            The correct text.
     */
    public String getResult() {
        return this.correction.getString("result");
    }

    /**
     * Parse the given text and return the JSON object that contains the
     * result & the suggested corrections.
     *
     * @param text
     *            The text that should be corrected.
     *
     * @return JSONObject
     */
    public JSONObject parse(String text) {
        String json = "";
        URIBuilder builder = null;

        try {
            // Build the Web Service URL
            builder = new URIBuilder(this.getBaseURL());
            builder.addParameters(parameters);
            builder.addParameter("text", text);

            // Create the HTTP client
            HttpClient client = HttpClientBuilder.create().build();
            // Create GET request
            HttpGet request = new HttpGet(builder.build());

            // Add request header
            request.addHeader("User-Agent", USER_AGENT);

            // Send request
            HttpResponse response = client.execute(request);

            // Get json response
            json = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

            // Process the suggested corrections
            this.correction = this.processSuggestions(text, new JSONObject(json));
        } catch (URISyntaxException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (JSONException e) {
            System.out.println("Error while parsing the json response: " + e.getMessage());
        }

        return this.correction;
    }

    /**
     * Get the base URL.
     *
     * @return String
     */
    public String getBaseURL() {
        return baseURL;
    }
}
