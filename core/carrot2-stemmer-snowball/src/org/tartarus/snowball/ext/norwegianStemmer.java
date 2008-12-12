
// Generated file. Do not edit by hand.

package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;

 /**
  * This class was automatically generated by a Snowball to Java compiler 
  * It implements the stemming algorithm defined by a snowball script.
  */

@SuppressWarnings({ "unchecked", "unused", "serial" })
public class norwegianStemmer extends org.tartarus.snowball.SnowballStemmer {

private static final long serialVersionUID = 1L;

        private final static norwegianStemmer methodObject = new norwegianStemmer ();

                private final static Among a_0[] = {
                    new Among ( "a", -1, 1, "", methodObject ),
                    new Among ( "e", -1, 1, "", methodObject ),
                    new Among ( "ede", 1, 1, "", methodObject ),
                    new Among ( "ande", 1, 1, "", methodObject ),
                    new Among ( "ende", 1, 1, "", methodObject ),
                    new Among ( "ane", 1, 1, "", methodObject ),
                    new Among ( "ene", 1, 1, "", methodObject ),
                    new Among ( "hetene", 6, 1, "", methodObject ),
                    new Among ( "erte", 1, 3, "", methodObject ),
                    new Among ( "en", -1, 1, "", methodObject ),
                    new Among ( "heten", 9, 1, "", methodObject ),
                    new Among ( "ar", -1, 1, "", methodObject ),
                    new Among ( "er", -1, 1, "", methodObject ),
                    new Among ( "heter", 12, 1, "", methodObject ),
                    new Among ( "s", -1, 2, "", methodObject ),
                    new Among ( "as", 14, 1, "", methodObject ),
                    new Among ( "es", 14, 1, "", methodObject ),
                    new Among ( "edes", 16, 1, "", methodObject ),
                    new Among ( "endes", 16, 1, "", methodObject ),
                    new Among ( "enes", 16, 1, "", methodObject ),
                    new Among ( "hetenes", 19, 1, "", methodObject ),
                    new Among ( "ens", 14, 1, "", methodObject ),
                    new Among ( "hetens", 21, 1, "", methodObject ),
                    new Among ( "ers", 14, 1, "", methodObject ),
                    new Among ( "ets", 14, 1, "", methodObject ),
                    new Among ( "et", -1, 1, "", methodObject ),
                    new Among ( "het", 25, 1, "", methodObject ),
                    new Among ( "ert", -1, 3, "", methodObject ),
                    new Among ( "ast", -1, 1, "", methodObject )
                };

                private final static Among a_1[] = {
                    new Among ( "dt", -1, -1, "", methodObject ),
                    new Among ( "vt", -1, -1, "", methodObject )
                };

                private final static Among a_2[] = {
                    new Among ( "leg", -1, 1, "", methodObject ),
                    new Among ( "eleg", 0, 1, "", methodObject ),
                    new Among ( "ig", -1, 1, "", methodObject ),
                    new Among ( "eig", 2, 1, "", methodObject ),
                    new Among ( "lig", 2, 1, "", methodObject ),
                    new Among ( "elig", 4, 1, "", methodObject ),
                    new Among ( "els", -1, 1, "", methodObject ),
                    new Among ( "lov", -1, 1, "", methodObject ),
                    new Among ( "elov", 7, 1, "", methodObject ),
                    new Among ( "slov", 7, 1, "", methodObject ),
                    new Among ( "hetslov", 9, 1, "", methodObject )
                };

                private static final char g_v[] = {17, 65, 16, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 48, 0, 128 };

                private static final char g_s_ending[] = {119, 125, 149, 1 };

        private int I_x;
        private int I_p1;

                private void copy_from(norwegianStemmer other) {
                    I_x = other.I_x;
                    I_p1 = other.I_p1;
                    super.copy_from(other);
                }

                private boolean r_mark_regions() {
            int v_1;
            int v_2;
                    // (, line 26
                    I_p1 = limit;
                    // test, line 30
                    v_1 = cursor;
                    // (, line 30
                    // hop, line 30
                    {
                        int c = cursor + 3;
                        if (0 > c || c > limit)
                        {
                            return false;
                        }
                        cursor = c;
                    }
                    // setmark x, line 30
                    I_x = cursor;
                    cursor = v_1;
                    // goto, line 31
                    golab0: while(true)
                    {
                        v_2 = cursor;
                        lab1: do {
                            if (!(in_grouping(g_v, 97, 248)))
                            {
                                break lab1;
                            }
                            cursor = v_2;
                            break golab0;
                        } while (false);
                        cursor = v_2;
                        if (cursor >= limit)
                        {
                            return false;
                        }
                        cursor++;
                    }
                    // gopast, line 31
                    golab2: while(true)
                    {
                        lab3: do {
                            if (!(out_grouping(g_v, 97, 248)))
                            {
                                break lab3;
                            }
                            break golab2;
                        } while (false);
                        if (cursor >= limit)
                        {
                            return false;
                        }
                        cursor++;
                    }
                    // setmark p1, line 31
                    I_p1 = cursor;
                    // try, line 32
                    lab4: do {
                        // (, line 32
                        if (!(I_p1 < I_x))
                        {
                            break lab4;
                        }
                        I_p1 = I_x;
                    } while (false);
                    return true;
                }

                private boolean r_main_suffix() {
            int among_var;
            int v_1;
            int v_2;
            int v_3;
                    // (, line 37
                    // setlimit, line 38
                    v_1 = limit - cursor;
                    // tomark, line 38
                    if (cursor < I_p1)
                    {
                        return false;
                    }
                    cursor = I_p1;
                    v_2 = limit_backward;
                    limit_backward = cursor;
                    cursor = limit - v_1;
                    // (, line 38
                    // [, line 38
                    ket = cursor;
                    // substring, line 38
                    among_var = find_among_b(a_0, 29);
                    if (among_var == 0)
                    {
                        limit_backward = v_2;
                        return false;
                    }
                    // ], line 38
                    bra = cursor;
                    limit_backward = v_2;
                    switch(among_var) {
                        case 0:
                            return false;
                        case 1:
                            // (, line 44
                            // delete, line 44
                            slice_del();
                            break;
                        case 2:
                            // (, line 46
                            // or, line 46
                            lab0: do {
                                v_3 = limit - cursor;
                                lab1: do {
                                    if (!(in_grouping_b(g_s_ending, 98, 122)))
                                    {
                                        break lab1;
                                    }
                                    break lab0;
                                } while (false);
                                cursor = limit - v_3;
                                // (, line 46
                                // literal, line 46
                                if (!(eq_s_b(1, "k")))
                                {
                                    return false;
                                }
                                if (!(out_grouping_b(g_v, 97, 248)))
                                {
                                    return false;
                                }
                            } while (false);
                            // delete, line 46
                            slice_del();
                            break;
                        case 3:
                            // (, line 48
                            // <-, line 48
                            slice_from("er");
                            break;
                    }
                    return true;
                }

                private boolean r_consonant_pair() {
            int v_1;
            int v_2;
            int v_3;
                    // (, line 52
                    // test, line 53
                    v_1 = limit - cursor;
                    // (, line 53
                    // setlimit, line 54
                    v_2 = limit - cursor;
                    // tomark, line 54
                    if (cursor < I_p1)
                    {
                        return false;
                    }
                    cursor = I_p1;
                    v_3 = limit_backward;
                    limit_backward = cursor;
                    cursor = limit - v_2;
                    // (, line 54
                    // [, line 54
                    ket = cursor;
                    // substring, line 54
                    if (find_among_b(a_1, 2) == 0)
                    {
                        limit_backward = v_3;
                        return false;
                    }
                    // ], line 54
                    bra = cursor;
                    limit_backward = v_3;
                    cursor = limit - v_1;
                    // next, line 59
                    if (cursor <= limit_backward)
                    {
                        return false;
                    }
                    cursor--;
                    // ], line 59
                    bra = cursor;
                    // delete, line 59
                    slice_del();
                    return true;
                }

                private boolean r_other_suffix() {
            int among_var;
            int v_1;
            int v_2;
                    // (, line 62
                    // setlimit, line 63
                    v_1 = limit - cursor;
                    // tomark, line 63
                    if (cursor < I_p1)
                    {
                        return false;
                    }
                    cursor = I_p1;
                    v_2 = limit_backward;
                    limit_backward = cursor;
                    cursor = limit - v_1;
                    // (, line 63
                    // [, line 63
                    ket = cursor;
                    // substring, line 63
                    among_var = find_among_b(a_2, 11);
                    if (among_var == 0)
                    {
                        limit_backward = v_2;
                        return false;
                    }
                    // ], line 63
                    bra = cursor;
                    limit_backward = v_2;
                    switch(among_var) {
                        case 0:
                            return false;
                        case 1:
                            // (, line 67
                            // delete, line 67
                            slice_del();
                            break;
                    }
                    return true;
                }

                public boolean stem() {
            int v_1;
            int v_2;
            int v_3;
            int v_4;
                    // (, line 72
                    // do, line 74
                    v_1 = cursor;
                    lab0: do {
                        // call mark_regions, line 74
                        if (!r_mark_regions())
                        {
                            break lab0;
                        }
                    } while (false);
                    cursor = v_1;
                    // backwards, line 75
                    limit_backward = cursor; cursor = limit;
                    // (, line 75
                    // do, line 76
                    v_2 = limit - cursor;
                    lab1: do {
                        // call main_suffix, line 76
                        if (!r_main_suffix())
                        {
                            break lab1;
                        }
                    } while (false);
                    cursor = limit - v_2;
                    // do, line 77
                    v_3 = limit - cursor;
                    lab2: do {
                        // call consonant_pair, line 77
                        if (!r_consonant_pair())
                        {
                            break lab2;
                        }
                    } while (false);
                    cursor = limit - v_3;
                    // do, line 78
                    v_4 = limit - cursor;
                    lab3: do {
                        // call other_suffix, line 78
                        if (!r_other_suffix())
                        {
                            break lab3;
                        }
                    } while (false);
                    cursor = limit - v_4;
                    cursor = limit_backward;                    return true;
                }

        public boolean equals( Object o ) {
            return o instanceof norwegianStemmer;
        }

        public int hashCode() {
            return norwegianStemmer.class.getName().hashCode();
        }



}

