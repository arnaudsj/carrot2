
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.tokenizer;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TokenizerImplTokenManager implements TokenizerImplConstants {
    /** DOCUMENT ME! */
    public java.io.PrintStream debugStream = System.out;

    /**
     * DOCUMENT ME!
     *
     * @param ds DOCUMENT ME!
     */
    public void setDebugStream(java.io.PrintStream ds) {
        debugStream = ds;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private final int jjMoveStringLiteralDfa0_0() {
        return jjMoveNfa_0(34, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param state DOCUMENT ME!
     */
    private final void jjCheckNAdd(int state) {
        if (jjrounds[state] != jjround) {
            jjstateSet[jjnewStateCnt++] = state;
            jjrounds[state] = jjround;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param start DOCUMENT ME!
     * @param end DOCUMENT ME!
     */
    private final void jjAddStates(int start, int end) {
        do {
            jjstateSet[jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }

    /**
     * DOCUMENT ME!
     *
     * @param state1 DOCUMENT ME!
     * @param state2 DOCUMENT ME!
     */
    private final void jjCheckNAddTwoStates(int state1, int state2) {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }

    /**
     * DOCUMENT ME!
     *
     * @param start DOCUMENT ME!
     * @param end DOCUMENT ME!
     */
    private final void jjCheckNAddStates(int start, int end) {
        do {
            jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }

    /**
     * DOCUMENT ME!
     *
     * @param start DOCUMENT ME!
     */
    private final void jjCheckNAddStates(int start) {
        jjCheckNAdd(jjnextStates[start]);
        jjCheckNAdd(jjnextStates[start + 1]);
    }

    /** DOCUMENT ME! */
    static final long[] jjbitVec0 = {
            0x1ff00000fffffffeL, 0xffffffffffffc000L, 0xffffffffL,
            0x600000000000000L
        };

    /** DOCUMENT ME! */
    static final long[] jjbitVec2 = { 0x0L, 0x0L, 0x0L, 0xff7fffffff7fffffL };

    /** DOCUMENT ME! */
    static final long[] jjbitVec3 = {
            0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
        };

    /** DOCUMENT ME! */
    static final long[] jjbitVec4 = {
            0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffL, 0x0L
        };

    /** DOCUMENT ME! */
    static final long[] jjbitVec5 = {
            0xffffffffffffffffL, 0xffffffffffffffffL, 0x0L, 0x0L
        };

    /** DOCUMENT ME! */
    static final long[] jjbitVec6 = { 0x3fffffffffffL, 0x0L, 0x0L, 0x0L };

    /** DOCUMENT ME! */
    static final long[] jjbitVec7 = { 0x1600L, 0x0L, 0x0L, 0x0L };

    /** DOCUMENT ME! */
    static final long[] jjbitVec8 = {
            0x0L, 0xffc000000000L, 0x0L, 0xffc000000000L
        };

    /** DOCUMENT ME! */
    static final long[] jjbitVec9 = {
            0x0L, 0x3ff00000000L, 0x0L, 0x3ff000000000000L
        };

    /** DOCUMENT ME! */
    static final long[] jjbitVec10 = {
            0x0L, 0xffc000000000L, 0x0L, 0xff8000000000L
        };

    /** DOCUMENT ME! */
    static final long[] jjbitVec11 = { 0x0L, 0xffc000000000L, 0x0L, 0x0L };

    /** DOCUMENT ME! */
    static final long[] jjbitVec12 = { 0x0L, 0x3ff0000L, 0x0L, 0x3ff0000L };

    /** DOCUMENT ME! */
    static final long[] jjbitVec13 = { 0x0L, 0x3ffL, 0x0L, 0x0L };

    /**
     * DOCUMENT ME!
     *
     * @param startState DOCUMENT ME!
     * @param curPos DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private final int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        jjnewStateCnt = 143;

        int i = 1;
        jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++jjround == 0x7fffffff) {
                ReInitRounds();
            }

            if (curChar < 64) {
                long l = 1L << curChar;
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 34:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddStates(0, 8);
                        } else if ((0x100002600L & l) != 0L) {
                            if (kind > 17) {
                                kind = 17;
                            }

                            jjCheckNAdd(48);
                        } else if ((0x400308000000000L & l) != 0L) {
                            if (kind > 10) {
                                kind = 10;
                            }
                        } else if ((0x8800400200000000L & l) != 0L) {
                            if (kind > 9) {
                                kind = 9;
                            }

                            jjCheckNAdd(43);
                        }

                        if ((0x3ff000000000000L & l) != 0L) {
                            if (kind > 11) {
                                kind = 11;
                            }

                            jjCheckNAddTwoStates(45, 46);
                        }

                        break;

                    case 1:

                        if (curChar == 47) {
                            jjstateSet[jjnewStateCnt++] = 2;
                        }

                        break;

                    case 2:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(9, 14);

                        break;

                    case 3:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(15, 17);

                        break;

                    case 4:

                        if (curChar == 46) {
                            jjstateSet[jjnewStateCnt++] = 5;
                        }

                        break;

                    case 5:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(18, 20);

                        break;

                    case 6:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(18, 20);

                        break;

                    case 7:

                        if (curChar == 47) {
                            jjCheckNAdd(8);
                        }

                        break;

                    case 8:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(21, 24);

                        break;

                    case 9:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(21, 24);

                        break;

                    case 10:

                        if (curChar == 46) {
                            jjstateSet[jjnewStateCnt++] = 11;
                        }

                        break;

                    case 11:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(25, 27);

                        break;

                    case 12:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(25, 27);

                        break;

                    case 13:

                        if (curChar == 63) {
                            jjstateSet[jjnewStateCnt++] = 14;
                        }

                        break;

                    case 14:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(28, 31);

                        break;

                    case 15:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(28, 31);

                        break;

                    case 16:

                        if ((0x2c00004000000000L & l) != 0L) {
                            jjstateSet[jjnewStateCnt++] = 17;
                        }

                        break;

                    case 17:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(32, 35);

                        break;

                    case 18:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(32, 35);

                        break;

                    case 19:

                        if (curChar != 47) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(36, 38);

                        break;

                    case 20:

                        if ((0x3ff200000000000L & l) != 0L) {
                            jjCheckNAddStates(39, 41);
                        }

                        break;

                    case 21:

                        if (curChar == 46) {
                            jjstateSet[jjnewStateCnt++] = 22;
                        }

                        break;

                    case 22:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddStates(42, 44);
                        }

                        break;

                    case 23:

                        if ((0x3ff200000000000L & l) != 0L) {
                            jjCheckNAddStates(42, 44);
                        }

                        break;

                    case 25:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(26, 27);

                        break;

                    case 26:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(26, 27);

                        break;

                    case 27:

                        if (curChar == 46) {
                            jjstateSet[jjnewStateCnt++] = 28;
                        }

                        break;

                    case 28:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(27, 29);

                        break;

                    case 29:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(27, 29);

                        break;

                    case 30:

                        if (curChar == 47) {
                            jjstateSet[jjnewStateCnt++] = 1;
                        }

                        break;

                    case 31:

                        if (curChar == 58) {
                            jjstateSet[jjnewStateCnt++] = 30;
                        }

                        break;

                    case 43:

                        if ((0x8800400200000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 9) {
                            kind = 9;
                        }

                        jjCheckNAdd(43);

                        break;

                    case 44:

                        if (((0x400308000000000L & l) != 0L) && (kind > 10)) {
                            kind = 10;
                        }

                        break;

                    case 45:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 11) {
                            kind = 11;
                        }

                        jjCheckNAddTwoStates(45, 46);

                        break;

                    case 46:

                        if ((0x400f00000000000L & l) != 0L) {
                            jjCheckNAdd(47);
                        }

                        break;

                    case 47:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 11) {
                            kind = 11;
                        }

                        jjCheckNAddTwoStates(46, 47);

                        break;

                    case 48:

                        if ((0x100002600L & l) == 0L) {
                            break;
                        }

                        if (kind > 17) {
                            kind = 17;
                        }

                        jjCheckNAdd(48);

                        break;

                    case 49:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddStates(0, 8);
                        }

                        break;

                    case 50:

                        if ((0x3ff200000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(50, 51);
                        }

                        break;

                    case 51:

                        if (curChar == 46) {
                            jjAddStates(45, 54);
                        }

                        break;

                    case 52:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(53, 51);
                        }

                        break;

                    case 53:

                        if ((0x3ff200000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(53, 51);
                        }

                        break;

                    case 55:

                        if (curChar == 47) {
                            jjCheckNAdd(56);
                        }

                        break;

                    case 56:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(55, 58);

                        break;

                    case 57:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(55, 58);

                        break;

                    case 58:

                        if (curChar == 46) {
                            jjstateSet[jjnewStateCnt++] = 59;
                        }

                        break;

                    case 59:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(59, 61);

                        break;

                    case 60:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(59, 61);

                        break;

                    case 61:

                        if (curChar == 63) {
                            jjstateSet[jjnewStateCnt++] = 62;
                        }

                        break;

                    case 62:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(62, 65);

                        break;

                    case 63:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(62, 65);

                        break;

                    case 64:

                        if ((0x2c00004000000000L & l) != 0L) {
                            jjstateSet[jjnewStateCnt++] = 65;
                        }

                        break;

                    case 65:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(66, 69);

                        break;

                    case 66:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(66, 69);

                        break;

                    case 67:

                        if (curChar != 47) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(70, 72);

                        break;

                    case 94:

                        if (curChar == 47) {
                            jjCheckNAdd(95);
                        }

                        break;

                    case 95:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(73, 76);

                        break;

                    case 96:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(73, 76);

                        break;

                    case 97:

                        if (curChar == 46) {
                            jjstateSet[jjnewStateCnt++] = 98;
                        }

                        break;

                    case 98:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(77, 79);

                        break;

                    case 99:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(77, 79);

                        break;

                    case 100:

                        if (curChar != 47) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(101);

                        break;

                    case 101:

                        if (curChar == 63) {
                            jjstateSet[jjnewStateCnt++] = 102;
                        }

                        break;

                    case 102:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(80, 82);

                        break;

                    case 103:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(80, 82);

                        break;

                    case 104:

                        if ((0x2c00004000000000L & l) != 0L) {
                            jjstateSet[jjnewStateCnt++] = 105;
                        }

                        break;

                    case 105:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(83, 85);

                        break;

                    case 106:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(83, 85);

                        break;

                    case 107:

                        if (curChar != 47) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(95, 101);

                        break;

                    case 108:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(108, 109);
                        }

                        break;

                    case 110:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 4) {
                            kind = 4;
                        }

                        jjstateSet[jjnewStateCnt++] = 110;

                        break;

                    case 111:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(111, 112);
                        }

                        break;

                    case 113:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(113, 114);
                        }

                        break;

                    case 114:

                        if (curChar == 38) {
                            jjCheckNAddTwoStates(115, 116);
                        }

                        break;

                    case 115:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(115, 116);
                        }

                        break;

                    case 117:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 7) {
                            kind = 7;
                        }

                        jjCheckNAddTwoStates(114, 117);

                        break;

                    case 118:

                        if ((0x3ff200000000000L & l) != 0L) {
                            jjCheckNAddStates(86, 88);
                        }

                        break;

                    case 119:

                        if (curChar == 46) {
                            jjstateSet[jjnewStateCnt++] = 120;
                        }

                        break;

                    case 120:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddStates(89, 91);
                        }

                        break;

                    case 121:

                        if ((0x3ff200000000000L & l) != 0L) {
                            jjCheckNAddStates(89, 91);
                        }

                        break;

                    case 123:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(124, 125);

                        break;

                    case 124:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(124, 125);

                        break;

                    case 125:

                        if (curChar == 46) {
                            jjstateSet[jjnewStateCnt++] = 126;
                        }

                        break;

                    case 126:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(125, 127);

                        break;

                    case 127:

                        if ((0x3ff200000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(125, 127);

                        break;

                    case 130:

                        if (curChar == 45) {
                            jjstateSet[jjnewStateCnt++] = 131;
                        }

                        break;

                    case 132:

                        if (curChar == 46) {
                            jjCheckNAddTwoStates(133, 134);
                        }

                        break;

                    case 133:

                        if ((0x100002600L & l) != 0L) {
                            jjCheckNAddTwoStates(133, 134);
                        }

                        break;

                    case 135:

                        if (curChar == 46) {
                            jjCheckNAddTwoStates(136, 137);
                        }

                        break;

                    case 136:

                        if ((0x100002600L & l) != 0L) {
                            jjCheckNAddTwoStates(136, 137);
                        }

                        break;

                    case 140:

                        if (curChar == 46) {
                            jjCheckNAdd(141);
                        }

                        break;

                    case 142:

                        if (curChar != 46) {
                            break;
                        }

                        if (kind > 7) {
                            kind = 7;
                        }

                        jjCheckNAdd(141);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 34:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            if (kind > 4) {
                                kind = 4;
                            }

                            jjCheckNAddStates(92, 98);
                        }

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddStates(0, 8);
                        }

                        if (curChar == 109) {
                            jjstateSet[jjnewStateCnt++] = 41;
                        } else if (curChar == 102) {
                            jjstateSet[jjnewStateCnt++] = 35;
                        } else if (curChar == 104) {
                            jjstateSet[jjnewStateCnt++] = 33;
                        }

                        break;

                    case 0:

                        if (curChar == 112) {
                            jjCheckNAdd(31);
                        }

                        break;

                    case 2:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(9, 14);

                        break;

                    case 3:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(15, 17);

                        break;

                    case 5:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(18, 20);

                        break;

                    case 6:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(18, 20);

                        break;

                    case 8:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(21, 24);

                        break;

                    case 9:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(21, 24);

                        break;

                    case 11:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(25, 27);

                        break;

                    case 12:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(25, 27);

                        break;

                    case 14:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(28, 31);

                        break;

                    case 15:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(28, 31);

                        break;

                    case 16:

                        if (curChar == 64) {
                            jjstateSet[jjnewStateCnt++] = 17;
                        }

                        break;

                    case 17:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(32, 35);

                        break;

                    case 18:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(32, 35);

                        break;

                    case 20:

                        if ((0x7fffffe87fffffeL & l) != 0L) {
                            jjCheckNAddStates(39, 41);
                        }

                        break;

                    case 22:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddStates(42, 44);
                        }

                        break;

                    case 23:

                        if ((0x7fffffe87fffffeL & l) != 0L) {
                            jjCheckNAddStates(42, 44);
                        }

                        break;

                    case 24:

                        if (curChar == 64) {
                            jjstateSet[jjnewStateCnt++] = 25;
                        }

                        break;

                    case 25:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(26, 27);

                        break;

                    case 26:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(26, 27);

                        break;

                    case 28:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(27, 29);

                        break;

                    case 29:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(27, 29);

                        break;

                    case 32:
                    case 35:

                        if (curChar == 116) {
                            jjCheckNAdd(0);
                        }

                        break;

                    case 33:

                        if (curChar == 116) {
                            jjstateSet[jjnewStateCnt++] = 32;
                        }

                        break;

                    case 36:

                        if (curChar == 102) {
                            jjstateSet[jjnewStateCnt++] = 35;
                        }

                        break;

                    case 37:

                        if (curChar == 111) {
                            jjCheckNAdd(31);
                        }

                        break;

                    case 38:

                        if (curChar == 116) {
                            jjstateSet[jjnewStateCnt++] = 37;
                        }

                        break;

                    case 39:

                        if (curChar == 108) {
                            jjstateSet[jjnewStateCnt++] = 38;
                        }

                        break;

                    case 40:

                        if (curChar == 105) {
                            jjstateSet[jjnewStateCnt++] = 39;
                        }

                        break;

                    case 41:

                        if (curChar == 97) {
                            jjstateSet[jjnewStateCnt++] = 40;
                        }

                        break;

                    case 42:

                        if (curChar == 109) {
                            jjstateSet[jjnewStateCnt++] = 41;
                        }

                        break;

                    case 49:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddStates(0, 8);
                        }

                        break;

                    case 50:

                        if ((0x7fffffe87fffffeL & l) != 0L) {
                            jjCheckNAddTwoStates(50, 51);
                        }

                        break;

                    case 52:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddTwoStates(53, 51);
                        }

                        break;

                    case 53:

                        if ((0x7fffffe87fffffeL & l) != 0L) {
                            jjCheckNAddTwoStates(53, 51);
                        }

                        break;

                    case 54:

                        if (curChar != 108) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(55);

                        break;

                    case 56:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(55, 58);

                        break;

                    case 57:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(55, 58);

                        break;

                    case 59:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(59, 61);

                        break;

                    case 60:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(59, 61);

                        break;

                    case 62:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(62, 65);

                        break;

                    case 63:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(62, 65);

                        break;

                    case 64:

                        if (curChar == 64) {
                            jjstateSet[jjnewStateCnt++] = 65;
                        }

                        break;

                    case 65:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(66, 69);

                        break;

                    case 66:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(66, 69);

                        break;

                    case 68:

                        if (curChar == 105) {
                            jjstateSet[jjnewStateCnt++] = 54;
                        }

                        break;

                    case 69:

                        if (curChar == 109) {
                            jjstateSet[jjnewStateCnt++] = 68;
                        }

                        break;

                    case 70:

                        if (curChar != 111) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(55);

                        break;

                    case 71:

                        if (curChar == 102) {
                            jjstateSet[jjnewStateCnt++] = 70;
                        }

                        break;

                    case 72:

                        if (curChar == 110) {
                            jjstateSet[jjnewStateCnt++] = 71;
                        }

                        break;

                    case 73:

                        if (curChar == 105) {
                            jjstateSet[jjnewStateCnt++] = 72;
                        }

                        break;

                    case 74:

                        if (curChar != 118) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(55);

                        break;

                    case 75:

                        if (curChar == 111) {
                            jjstateSet[jjnewStateCnt++] = 74;
                        }

                        break;

                    case 76:

                        if (curChar == 103) {
                            jjstateSet[jjnewStateCnt++] = 75;
                        }

                        break;

                    case 77:

                        if (curChar != 117) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(55);

                        break;

                    case 78:

                        if (curChar == 100) {
                            jjstateSet[jjnewStateCnt++] = 77;
                        }

                        break;

                    case 79:

                        if (curChar == 101) {
                            jjstateSet[jjnewStateCnt++] = 78;
                        }

                        break;

                    case 80:

                        if (curChar != 122) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(55);

                        break;

                    case 81:

                        if (curChar == 105) {
                            jjstateSet[jjnewStateCnt++] = 80;
                        }

                        break;

                    case 82:

                        if (curChar == 98) {
                            jjstateSet[jjnewStateCnt++] = 81;
                        }

                        break;

                    case 83:

                        if (curChar != 109) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(55);

                        break;

                    case 84:

                        if (curChar == 111) {
                            jjstateSet[jjnewStateCnt++] = 83;
                        }

                        break;

                    case 85:

                        if (curChar == 99) {
                            jjstateSet[jjnewStateCnt++] = 84;
                        }

                        break;

                    case 86:

                        if (curChar != 103) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(55);

                        break;

                    case 87:

                        if (curChar == 114) {
                            jjstateSet[jjnewStateCnt++] = 86;
                        }

                        break;

                    case 88:

                        if (curChar == 111) {
                            jjstateSet[jjnewStateCnt++] = 87;
                        }

                        break;

                    case 89:

                        if (curChar != 116) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(55);

                        break;

                    case 90:

                        if (curChar == 101) {
                            jjstateSet[jjnewStateCnt++] = 89;
                        }

                        break;

                    case 91:

                        if (curChar == 110) {
                            jjstateSet[jjnewStateCnt++] = 90;
                        }

                        break;

                    case 92:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjstateSet[jjnewStateCnt++] = 93;
                        }

                        break;

                    case 93:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjstateSet[jjnewStateCnt++] = 94;
                        }

                        break;

                    case 95:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(73, 76);

                        break;

                    case 96:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(73, 76);

                        break;

                    case 98:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(77, 79);

                        break;

                    case 99:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(77, 79);

                        break;

                    case 102:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(80, 82);

                        break;

                    case 103:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(80, 82);

                        break;

                    case 104:

                        if (curChar == 64) {
                            jjstateSet[jjnewStateCnt++] = 105;
                        }

                        break;

                    case 105:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(83, 85);

                        break;

                    case 106:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(83, 85);

                        break;

                    case 108:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddTwoStates(108, 109);
                        }

                        break;

                    case 109:
                    case 110:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 4) {
                            kind = 4;
                        }

                        jjCheckNAdd(110);

                        break;

                    case 111:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddTwoStates(111, 112);
                        }

                        break;

                    case 112:
                    case 113:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddTwoStates(113, 114);
                        }

                        break;

                    case 115:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjAddStates(99, 100);
                        }

                        break;

                    case 116:
                    case 117:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 7) {
                            kind = 7;
                        }

                        jjCheckNAddTwoStates(114, 117);

                        break;

                    case 118:

                        if ((0x7fffffe87fffffeL & l) != 0L) {
                            jjCheckNAddStates(86, 88);
                        }

                        break;

                    case 120:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddStates(89, 91);
                        }

                        break;

                    case 121:

                        if ((0x7fffffe87fffffeL & l) != 0L) {
                            jjCheckNAddStates(89, 91);
                        }

                        break;

                    case 122:

                        if (curChar == 64) {
                            jjstateSet[jjnewStateCnt++] = 123;
                        }

                        break;

                    case 123:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(124, 125);

                        break;

                    case 124:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(124, 125);

                        break;

                    case 126:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(125, 127);

                        break;

                    case 127:

                        if ((0x7fffffe87fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(125, 127);

                        break;

                    case 128:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 4) {
                            kind = 4;
                        }

                        jjCheckNAddStates(92, 98);

                        break;

                    case 129:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddTwoStates(129, 130);

                        break;

                    case 131:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddTwoStates(130, 131);

                        break;

                    case 134:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAddTwoStates(135, 138);
                        }

                        break;

                    case 137:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAdd(138);
                        }

                        break;

                    case 138:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjCheckNAdd(139);
                        }

                        break;

                    case 139:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 6) {
                            kind = 6;
                        }

                        jjCheckNAdd(139);

                        break;

                    case 141:

                        if ((0x7fffffe07fffffeL & l) != 0L) {
                            jjstateSet[jjnewStateCnt++] = 142;
                        }

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 077);
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 34:

                        if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                            if (kind > 11) {
                                kind = 11;
                            }

                            jjCheckNAddTwoStates(45, 46);
                        }

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddStates(0, 8);
                        }

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            if (kind > 4) {
                                kind = 4;
                            }

                            jjCheckNAddStates(92, 98);
                        }

                        break;

                    case 2:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(9, 14);

                        break;

                    case 3:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(15, 17);

                        break;

                    case 5:
                    case 6:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(18, 20);

                        break;

                    case 8:
                    case 9:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(21, 24);

                        break;

                    case 11:
                    case 12:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(25, 27);

                        break;

                    case 14:
                    case 15:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(28, 31);

                        break;

                    case 17:
                    case 18:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(32, 35);

                        break;

                    case 20:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddStates(39, 41);
                        }

                        break;

                    case 22:
                    case 23:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddStates(42, 44);
                        }

                        break;

                    case 25:
                    case 26:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(26, 27);

                        break;

                    case 28:
                    case 29:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddTwoStates(27, 29);

                        break;

                    case 45:

                        if (!jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 11) {
                            kind = 11;
                        }

                        jjCheckNAddTwoStates(45, 46);

                        break;

                    case 47:

                        if (!jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 11) {
                            kind = 11;
                        }

                        jjCheckNAddTwoStates(46, 47);

                        break;

                    case 49:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddStates(0, 8);
                        }

                        break;

                    case 50:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddTwoStates(50, 51);
                        }

                        break;

                    case 52:
                    case 53:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddTwoStates(53, 51);
                        }

                        break;

                    case 56:
                    case 57:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(55, 58);

                        break;

                    case 59:
                    case 60:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(59, 61);

                        break;

                    case 62:
                    case 63:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(62, 65);

                        break;

                    case 65:
                    case 66:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(66, 69);

                        break;

                    case 92:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjstateSet[jjnewStateCnt++] = 93;
                        }

                        break;

                    case 93:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjstateSet[jjnewStateCnt++] = 94;
                        }

                        break;

                    case 95:
                    case 96:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(73, 76);

                        break;

                    case 98:
                    case 99:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(77, 79);

                        break;

                    case 102:
                    case 103:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(80, 82);

                        break;

                    case 105:
                    case 106:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAddStates(83, 85);

                        break;

                    case 108:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddTwoStates(108, 109);
                        }

                        break;

                    case 109:
                    case 110:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 4) {
                            kind = 4;
                        }

                        jjCheckNAdd(110);

                        break;

                    case 111:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddTwoStates(111, 112);
                        }

                        break;

                    case 112:
                    case 113:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddTwoStates(113, 114);
                        }

                        break;

                    case 115:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjAddStates(99, 100);
                        }

                        break;

                    case 116:
                    case 117:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 7) {
                            kind = 7;
                        }

                        jjCheckNAddTwoStates(114, 117);

                        break;

                    case 118:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddStates(86, 88);
                        }

                        break;

                    case 120:
                    case 121:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddStates(89, 91);
                        }

                        break;

                    case 123:
                    case 124:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(124, 125);

                        break;

                    case 126:
                    case 127:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(125, 127);

                        break;

                    case 128:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 4) {
                            kind = 4;
                        }

                        jjCheckNAddStates(92, 98);

                        break;

                    case 129:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddTwoStates(129, 130);

                        break;

                    case 131:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddTwoStates(130, 131);

                        break;

                    case 134:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAddTwoStates(135, 138);
                        }

                        break;

                    case 137:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAdd(138);
                        }

                        break;

                    case 138:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjCheckNAdd(139);
                        }

                        break;

                    case 139:

                        if (!jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            break;
                        }

                        if (kind > 6) {
                            kind = 6;
                        }

                        jjCheckNAdd(139);

                        break;

                    case 141:

                        if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                            jjstateSet[jjnewStateCnt++] = 142;
                        }

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = jjnewStateCnt) == (startsAt = 143 -
                        (jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    /** DOCUMENT ME! */
    static final int[] jjnextStates = {
            50, 108, 109, 111, 112, 118, 119, 122, 51, 3, 4, 7, 20, 21, 24, 3, 4,
            7, 4, 6, 7, 9, 10, 13, 19, 12, 13, 19, 7, 13, 15, 16, 7, 13, 16, 18,
            7, 8, 13, 20, 21, 24, 21, 23, 24, 52, 69, 73, 76, 79, 82, 85, 88, 91,
            92, 57, 58, 61, 67, 60, 61, 67, 55, 61, 63, 64, 55, 61, 64, 66, 55,
            56, 61, 96, 97, 101, 107, 99, 100, 101, 101, 103, 104, 101, 104, 106,
            118, 119, 122, 119, 121, 122, 110, 129, 130, 132, 140, 113, 114, 115,
            116,
        };

    /**
     * DOCUMENT ME!
     *
     * @param hiByte DOCUMENT ME!
     * @param i1 DOCUMENT ME!
     * @param i2 DOCUMENT ME!
     * @param l1 DOCUMENT ME!
     * @param l2 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static final boolean jjCanMove_0(int hiByte, int i1, int i2,
        long l1, long l2) {
        switch (hiByte) {
        case 0:
            return ((jjbitVec2[i2] & l2) != 0L);

        case 48:
            return ((jjbitVec3[i2] & l2) != 0L);

        case 49:
            return ((jjbitVec4[i2] & l2) != 0L);

        case 51:
            return ((jjbitVec5[i2] & l2) != 0L);

        case 61:
            return ((jjbitVec6[i2] & l2) != 0L);

        default:

            if ((jjbitVec0[i1] & l1) != 0L) {
                return true;
            }

            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param hiByte DOCUMENT ME!
     * @param i1 DOCUMENT ME!
     * @param i2 DOCUMENT ME!
     * @param l1 DOCUMENT ME!
     * @param l2 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static final boolean jjCanMove_1(int hiByte, int i1, int i2,
        long l1, long l2) {
        switch (hiByte) {
        case 6:
            return ((jjbitVec9[i2] & l2) != 0L);

        case 11:
            return ((jjbitVec10[i2] & l2) != 0L);

        case 13:
            return ((jjbitVec11[i2] & l2) != 0L);

        case 14:
            return ((jjbitVec12[i2] & l2) != 0L);

        case 16:
            return ((jjbitVec13[i2] & l2) != 0L);

        default:

            if ((jjbitVec7[i1] & l1) != 0L) {
                if ((jjbitVec8[i2] & l2) == 0L) {
                    return false;
                } else {
                    return true;
                }
            }

            return false;
        }
    }

    /** DOCUMENT ME! */
    public static final String[] jjstrLiteralImages = {
            "", null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null,
        };

    /** DOCUMENT ME! */
    public static final String[] lexStateNames = { "DEFAULT", };

    /** DOCUMENT ME! */
    static final long[] jjtoToken = { 0xff3L, };

    /** DOCUMENT ME! */
    static final long[] jjtoSkip = { 0x60000L, };

    /** DOCUMENT ME! */
    private SimpleCharStream input_stream;

    /** DOCUMENT ME! */
    private final int[] jjrounds = new int[143];

    /** DOCUMENT ME! */
    private final int[] jjstateSet = new int[286];

    /** DOCUMENT ME! */
    protected char curChar;

    /**
     * Creates a new TokenizerImplTokenManager object.
     *
     * @param stream DOCUMENT ME!
     */
    public TokenizerImplTokenManager(SimpleCharStream stream) {
        if (SimpleCharStream.staticFlag) {
            throw new Error(
                "ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
        }

        input_stream = stream;
    }

    /**
     * Creates a new TokenizerImplTokenManager object.
     *
     * @param stream DOCUMENT ME!
     * @param lexState DOCUMENT ME!
     */
    public TokenizerImplTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        SwitchTo(lexState);
    }

    /**
     * DOCUMENT ME!
     *
     * @param stream DOCUMENT ME!
     */
    public void ReInit(SimpleCharStream stream) {
        jjmatchedPos = jjnewStateCnt = 0;
        curLexState = defaultLexState;
        input_stream = stream;
        ReInitRounds();
    }

    /**
     * DOCUMENT ME!
     */
    private final void ReInitRounds() {
        int i;
        jjround = 0x80000001;

        for (i = 143; i-- > 0;) {
            jjrounds[i] = 0x80000000;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param stream DOCUMENT ME!
     * @param lexState DOCUMENT ME!
     */
    public void ReInit(SimpleCharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    /**
     * DOCUMENT ME!
     *
     * @param lexState DOCUMENT ME!
     */
    public void SwitchTo(int lexState) {
        if ((lexState >= 1) || (lexState < 0)) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " +
                lexState + ". State unchanged.",
                TokenMgrError.INVALID_LEXICAL_STATE);
        } else {
            curLexState = lexState;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private final Token jjFillToken() {
        Token t = Token.newToken(jjmatchedKind);
        t.kind = jjmatchedKind;

        String im = jjstrLiteralImages[jjmatchedKind];
        t.image = (im == null) ? input_stream.GetImage() : im;
        t.beginLine = input_stream.getBeginLine();
        t.beginColumn = input_stream.getBeginColumn();
        t.endLine = input_stream.getEndLine();
        t.endColumn = input_stream.getEndColumn();

        return t;
    }

    /** DOCUMENT ME! */
    int curLexState = 0;

    /** DOCUMENT ME! */
    int defaultLexState = 0;

    /** DOCUMENT ME! */
    int jjnewStateCnt;

    /** DOCUMENT ME! */
    int jjround;

    /** DOCUMENT ME! */
    int jjmatchedPos;

    /** DOCUMENT ME! */
    int jjmatchedKind;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Token getNextToken() {
        Token matchedToken;
        int curPos = 0;

EOFLoop: 
        for (;;) {
            try {
                curChar = input_stream.BeginToken();
            } catch (java.io.IOException e) {
                jjmatchedKind = 0;
                matchedToken = jjFillToken();

                return matchedToken;
            }

            jjmatchedKind = 0x7fffffff;
            jjmatchedPos = 0;
            curPos = jjMoveStringLiteralDfa0_0();

            if ((jjmatchedPos == 0) && (jjmatchedKind > 18)) {
                jjmatchedKind = 18;
            }

            if (jjmatchedKind != 0x7fffffff) {
                if ((jjmatchedPos + 1) < curPos) {
                    input_stream.backup(curPos - jjmatchedPos - 1);
                }

                if ((jjtoToken[jjmatchedKind >> 6] &
                        (1L << (jjmatchedKind & 077))) != 0L) {
                    matchedToken = jjFillToken();

                    return matchedToken;
                } else {
                    continue EOFLoop;
                }
            }

            int error_line = input_stream.getEndLine();
            int error_column = input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;

            try {
                input_stream.readChar();
                input_stream.backup(1);
            } catch (java.io.IOException e1) {
                EOFSeen = true;
                error_after = (curPos <= 1) ? "" : input_stream.GetImage();

                if ((curChar == '\n') || (curChar == '\r')) {
                    error_line++;
                    error_column = 0;
                } else {
                    error_column++;
                }
            }

            if (!EOFSeen) {
                input_stream.backup(1);
                error_after = (curPos <= 1) ? "" : input_stream.GetImage();
            }

            throw new TokenMgrError(EOFSeen, curLexState, error_line,
                error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
        }
    }
}
