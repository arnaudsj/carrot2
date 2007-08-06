
// Generated file. Do not edit by hand.

package org.carrot2.filter.trc.carrot.tokenizer;

import java.io.StringReader;


/**
 * Tokenizer class splits a string into tokens like word, e-mail
 * address, web page address and such.
 * Instances may be quite large (memory consumption-wise)
 * so they should be reused rather than recreated.
 */
public class Tokenizer
{
    private TokenizerImpl tokenizer;

    /** Token type representing  regular terms - word, hyphenated words (alpha-beta), even single-letter conjunctions (i,a) */
    public static final int TYPE_TERM   = 0x0001;
    /** Token type representing  URLs - currently only http and file urls supported. */
    public static final int TYPE_URL    = 0x0002;
    /** Token type representing e-mail addresses */
    public static final int TYPE_EMAIL  = 0x0003;
    /**
     *  Token type representing abbreviated names, for example G. Bush or S.C.Johnson.
     *  <em>If only surname is present in the text, it will be returned as a <code>TYPE_TERM</code>
     *  token!
     */
    public static final int TYPE_PERSON = 0x0004;
    /** Token type representing sentence boundary ('.', '?' or alike) */
    public static final int TYPE_SENTENCEMARKER  = 0x0005;

    /** Token type representing phrase boundary (',', ';') */
    public static final int TYPE_PHRASEMARKER = 0x0006;

    /** HTML Entities (&amp;,  &omicron;, &#1234;, &#x1ABC;) */
    public static final int TYPE_HTMLENTITY = 0x0007;

    /** Use factory method to acquire instances of this class. */
    private Tokenizer()
    {
    }

    /**
     * Creates a new empty tokenizer, which has to be initialized using one of
     * the restart* methods.
     */
    public static Tokenizer getTokenizer()
    {
        return new Tokenizer();
    }


    /**
     * Restart token parsing for input string <code>string</code>.
     */
    public final void restartTokenizerOn( String string )
    {
        if (tokenizer != null)
        {
            tokenizer.ReInit( new StringReader( string ));
        }
        else
        {
            tokenizer = new TokenizerImpl( new StringReader(string));
        }
    }

    /**
     * Return the next token from the parsing data. The token value is returned from
     * the method, while the type of the token is stored in the zero index of the input
     * parameter <code>tokenTypeHolder</code>. If tokenTypeHolder is <code>null</code>,
     * token type is not saved anywhere.
     *
     * <code>null</code> is returned when end of the input data has been reached.
     *
     * This method is <em>not</em> synchronized.
     */
    public final String getNextToken(int [] tokenTypeHolder)
    {
        try
        {
            Token t = tokenizer.getNextToken();
            if (t.kind == TokenizerImplConstants.EOF)
                return null;

            if (tokenTypeHolder != null)
            {
                switch (t.kind)
                {
                    case TokenizerImplConstants.URL:
                        tokenTypeHolder[0] = TYPE_URL; break;

                    case TokenizerImplConstants.TERM:
                    case TokenizerImplConstants.HYPHTERM:
                    case TokenizerImplConstants.ACRONYM:
                        tokenTypeHolder[0] = TYPE_TERM; break;

                    case TokenizerImplConstants.EMAIL:
                        tokenTypeHolder[0] = TYPE_EMAIL; break;

                    case TokenizerImplConstants.PERSON:
                        tokenTypeHolder[0] = TYPE_PERSON; break;

                    case TokenizerImplConstants.SENTENCEMARKER:
                        tokenTypeHolder[0] = TYPE_SENTENCEMARKER; break;

                    case TokenizerImplConstants.PHRASEMARKER:
                        tokenTypeHolder[0] = TYPE_PHRASEMARKER; break;

                    case TokenizerImplConstants.HTMLENTITY:
                        tokenTypeHolder[0] = TYPE_HTMLENTITY; break;
                    default:
                        throw new RuntimeException("Token should not be returned separately: "
                            + TokenizerImplConstants.tokenImage[ t.kind ] + " (" + t.image + ")");
                }
            }
            return t.image;
        }
        catch (NullPointerException e)
        {
            // catching exception costs nothing
            if (tokenizer == null)
                throw new RuntimeException("Initialize tokenizer first.");
            throw e;
        }
    }
}