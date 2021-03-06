/*
 *
 *  * Copyright (c) 2017 Alexander Grün
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package de.unknownreality.dataframe.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 15.03.2016.
 */
public class StringUtil {

    private StringUtil() {
    }

    /**
     * Puts a string in quotes.
     * All occurrences of quotes chars in the string are escaped.
     *
     * @param input     string to put in quotes
     * @param quoteChar quote char
     * @return string between quote chars
     */
    public static String putInQuotes(String input, Character quoteChar) {
        return quoteChar + input.replace(quoteChar.toString(), "\\" + quoteChar) + quoteChar;
    }

    /**
     * Split an input string at a specified split-character  into several parts.
     * <tt>"</tt> and <tt>'</tt> are considered during the process.
     * <p><code>"testA    testB   testB" -&gt; [testA,testB,testC]</code></p>
     * <p><code>"'testA    testB'   testB" -&gt; [testA    testB,testC]</code></p>
     *
     * @param input input string
     * @param split char used to split
     * @return string array containing all splitted parts
     */
    public static String[] splitQuoted(String input, Character split) {
        List<String> parts = new ArrayList<>();
        splitQuoted(input, split, new ListParts(parts));
        String[] result = new String[parts.size()];
        return parts.toArray(result);
    }

    /**
     * Split an input string at a specified split-character  into several parts.
     * <tt>"</tt> and <tt>'</tt> are considered during the process.
     * <p><code>"testA    testB   testB" -&gt; [testA,testB,testC]</code></p>
     * <p><code>"'testA    testB'   testB" -&gt; [testA    testB,testC]</code></p>
     *
     * @param input input string
     * @param split char used to split
     * @param parts string array that is filled with the resulting parts
     */
    public static void splitQuoted(String input, Character split, String[] parts) {
        splitQuoted(input, split, new ArrayParts(parts));
    }

    /**
     * Split an input string at a specified split-character  into several parts.
     * <tt>"</tt> and <tt>'</tt> are considered during the process.
     * <p><code>"testA    testB   testB" -&gt; [testA,testB,testC]</code></p>
     * <p><code>"'testA    testB'   testB" -&gt; [testA    testB,testC]</code></p>
     *
     * @param input input string
     * @param split char used to split
     * @param parts list filled with the resulting parts
     */
    @SuppressWarnings("ConstantConditions")
    public static void splitQuoted(String input, Character split, Parts parts) {
        if (input.length() == 0) {
            return;
        }
        boolean inQuotation = false;
        boolean inDoubleQuotation = false;
        boolean escapeNext = false;
        char c;
        boolean startOrSplit = true;
        final StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            if (escapeNext) {
                sb.append(c);
                escapeNext = false;
                continue;
            } else if (c == '\\') {
                escapeNext = true;
                continue;
            } else if (c == '\'') {
                if (inQuotation) {
                    inQuotation = false;
                } else if (!inDoubleQuotation && startOrSplit) {
                    inQuotation = true;
                    startOrSplit = false;
                } else {
                    sb.append(c);
                }
                continue;
            } else if (c == '\"') {
                if (inDoubleQuotation) {
                    inDoubleQuotation = false;
                } else if (!inDoubleQuotation && startOrSplit) {
                    inDoubleQuotation = true;
                    startOrSplit = false;
                } else {
                    sb.append(c);
                }
                continue;
            } else if (c == split && !inDoubleQuotation && !inQuotation) {

                parts.add(sb.toString());
                sb.setLength(0);
                startOrSplit = true;
                continue;
            } else {
                startOrSplit = false;
            }
            sb.append(c);

        }
        parts.add(sb.toString());

    }

    private interface Parts {
        void add(String part);
    }

    private static class ListParts implements Parts {
        private List<String> list;

        public ListParts(List<String> list) {
            this.list = list;
        }

        @Override
        public void add(String part) {
            list.add(part);
        }
    }

    private static class ArrayParts implements Parts {
        private String[] array;
        private int p = 0;

        public ArrayParts(String[] array) {
            this.array = array;
        }

        @Override
        public void add(String part) {
            array[p++] = part;
        }
    }
}
