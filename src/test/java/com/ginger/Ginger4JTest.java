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

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * Ginger4j unit test.
 *
 * @author Brahim Arkni <brahim.arkni@gmail.com>
 *
 */
public class Ginger4JTest {

    private Ginger4J ginger;

    @Before
    public void setUp() {
        ginger = new Ginger4J();
    }

    @After
    public void tearDown() {
        ginger = null;
    }

    @Test
    public void testParsedResults() {
        String text = "The smelt of fliwers bring back memories.";
        JSONObject result = ginger.parse(text);

        assertEquals(text, result.getString("text"));
        assertEquals("The smell of flowers brings back memories.", result.getString("result"));
        assertEquals("The smell of flowers brings back memories.", ginger.getResult());
        assertEquals(3, result.getJSONArray("corrections").length());
        assertEquals(4, result.getJSONArray("corrections").getJSONObject(0).getInt("start"));
        assertEquals(5, result.getJSONArray("corrections").getJSONObject(0).getInt("length"));

    }
}
