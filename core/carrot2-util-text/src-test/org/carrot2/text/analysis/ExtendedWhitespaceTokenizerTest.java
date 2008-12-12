
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.junit.Test;

/**
 * Test {@link ExtendedWhitespaceTokenizer}.
 */
public class ExtendedWhitespaceTokenizerTest
{
    /**
     * Internal class for comparing sequences of tokens.
     */
    private static class TokenImage
    {
        final int type;
        final String image;

        public TokenImage(String image, int type)
        {
            this.type = type;
            this.image = image;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof TokenImage)
            {
                return (((TokenImage) o).image.equals(this.image) && (((TokenImage) o).type == this.type));
            }
            else
            {
                return false;
            }
        }

        @Override
        public int hashCode()
        {
            return image != null ? image.hashCode() ^ type : type;
        }

        public String toString()
        {
            final String rawType = "0x" + Integer.toHexString(type);
            return "[" + rawType + "] " + this.image;
        }
    }

    @Test
    public void TERM()
    {
        String test = " simple simple's simples` terms simpleterm 9numterm numerm99x \"quoted string\"";
        TokenImage [] tokens =
        {
            new TokenImage("simple", ITokenType.TT_TERM),
            new TokenImage("simple's", ITokenType.TT_TERM),
            new TokenImage("simples`", ITokenType.TT_TERM),
            new TokenImage("terms", ITokenType.TT_TERM),
            new TokenImage("simpleterm", ITokenType.TT_TERM),
            new TokenImage("9numterm", ITokenType.TT_TERM),
            new TokenImage("numerm99x", ITokenType.TT_TERM),
            new TokenImage("quoted", ITokenType.TT_TERM),
            new TokenImage("string", ITokenType.TT_TERM)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void SYMBOL()
    {
        String test = " ...  S_NI_P token";
        TokenImage [] tokens =
        {
            new TokenImage("...", ITokenType.TT_PUNCTUATION
                | ITokenType.TF_SEPARATOR_SENTENCE),
            new TokenImage("S_NI_P", ITokenType.TT_FILE),
            new TokenImage("token", ITokenType.TT_TERM)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void EMAIL()
    {
        String test = "e-mails dweiss@go2.pl dawid.weiss@go2.com.pl bubu@some-host.com me@me.org bubu99@yahoo.com";
        TokenImage [] tokens =
        {
            new TokenImage("e-mails", ITokenType.TT_HYPHTERM),
            new TokenImage("dweiss@go2.pl", ITokenType.TT_EMAIL),
            new TokenImage("dawid.weiss@go2.com.pl", ITokenType.TT_EMAIL),
            new TokenImage("bubu@some-host.com", ITokenType.TT_EMAIL),
            new TokenImage("me@me.org", ITokenType.TT_EMAIL),
            new TokenImage("bubu99@yahoo.com", ITokenType.TT_EMAIL)
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void URL()
    {
        String test = " urls http://www.google.com http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term "
            + " ftp://ftp.server.pl www.google.com   not.an.url   go2.pl/mail http://www.digimine.com/usama/datamine/.";
        TokenImage [] tokens =
        {
            new TokenImage("urls", ITokenType.TT_TERM),
            new TokenImage("http://www.google.com", ITokenType.TT_FULL_URL),
            new TokenImage(
                "http://www.cs.put.poznan.pl/index.jsp?query=term&query2=term",
                ITokenType.TT_FULL_URL),
            new TokenImage("ftp://ftp.server.pl", ITokenType.TT_FULL_URL),
            new TokenImage("www.google.com", ITokenType.TT_BARE_URL),

            new TokenImage("not.an.url", ITokenType.TT_FILE),

            new TokenImage("go2.pl/mail", ITokenType.TT_FULL_URL),

            new TokenImage("http://www.digimine.com/usama/datamine/.",
                ITokenType.TT_FULL_URL),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void ACRONYM()
    {
        String test = " acronyms I.B.M. S.C. z o.o. AT&T garey&johnson&willet";
        TokenImage [] tokens =
        {
            new TokenImage("acronyms", ITokenType.TT_TERM),
            new TokenImage("I.B.M.", ITokenType.TT_ACRONYM),
            new TokenImage("S.C.", ITokenType.TT_ACRONYM),

            new TokenImage("z", ITokenType.TT_TERM),
            new TokenImage("o.o.", ITokenType.TT_ACRONYM),

            new TokenImage("AT&T", ITokenType.TT_ACRONYM),
            new TokenImage("garey&johnson&willet", ITokenType.TT_ACRONYM),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void NUMERIC()
    {
        String test = " numeric 127 0 12.87 12,12 12-2003/23 term2003 2003term ";
        TokenImage [] tokens =
        {
            new TokenImage("numeric", ITokenType.TT_TERM),

            new TokenImage("127", ITokenType.TT_NUMERIC),
            new TokenImage("0", ITokenType.TT_NUMERIC),
            new TokenImage("12.87", ITokenType.TT_NUMERIC),
            new TokenImage("12,12", ITokenType.TT_NUMERIC),
            new TokenImage("12-2003/23", ITokenType.TT_NUMERIC),
            new TokenImage("term2003", ITokenType.TT_TERM),
            new TokenImage("2003term", ITokenType.TT_TERM)

        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void NASTY_URL_1()
    {
        String test = "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole "
            + "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218";
        TokenImage [] tokens =
        {
            new TokenImage(
                "http://r.office.microsoft.com/r/rlidLiveMeeting?p1=7&amp;p2=en_US&amp;p3=LMInfo&amp;p4=DownloadWindowsConsole",
                ITokenType.TT_FULL_URL),
            new TokenImage(
                "https://www.livemeeting.com/cc/askme/join?id=58937J&amp;role=present&amp;pw=mNjC%27%25%3D%218",
                ITokenType.TT_FULL_URL),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testKoreanWordSplit()
    {
        String test = "안녕하세요 한글입니다";
        TokenImage [] tokens =
        {
            new TokenImage("안녕하세요", ITokenType.TT_TERM),
            new TokenImage("한글입니다", ITokenType.TT_TERM),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void punctuationAndSentenceMarkers()
    {
        String test = "Dawid Weiss, Data Mining!";
        TokenImage [] tokens =
        {
            new TokenImage("Dawid", ITokenType.TT_TERM),
            new TokenImage("Weiss", ITokenType.TT_TERM),
            new TokenImage(",", ITokenType.TT_PUNCTUATION),
            new TokenImage("Data", ITokenType.TT_TERM),
            new TokenImage("Mining", ITokenType.TT_TERM),
            new TokenImage("!", ITokenType.TT_PUNCTUATION
                | ITokenType.TF_SEPARATOR_SENTENCE)
        };

        assertEqualTokens(test, tokens);
    }

    /**
     * Compare expected and produced token sequences.
     */
    private static void assertEqualTokens(String testString, TokenImage [] expectedTokens)
    {
        try
        {
            final Tokenizer tokenizer = new ExtendedWhitespaceTokenizer(new StringReader(
                testString));

            final ArrayList<TokenImage> tokens = new ArrayList<TokenImage>();
            Token token;
            while ((token = tokenizer.next()) != null)
            {
                final String image = new String(token.termBuffer(), 0, token.termLength());
                final ITokenType payload = (ITokenType) token.getPayload();

                tokens.add(new TokenImage(image, payload.getRawFlags()));
            }

            org.junit.Assert.assertArrayEquals(expectedTokens, tokens.toArray());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
