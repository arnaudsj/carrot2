
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.apache.lucene.misc;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Lucene" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Lucene", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/**
 * A class to represent a trigram
 * @author Jean-Francois Halleux
 */
class Trigram implements Comparable {
	
	private final char firstChar;
	private final char secondChar;
	private final char thirdChar;
	private final int hashCode;

	//pos determines the position in the buffer where a trigram is to be found
	public Trigram(char[] buffer,int pos) {
		firstChar=buffer[pos++];
		secondChar=buffer[pos++];
		thirdChar=buffer[pos];
		hashCode=firstChar+secondChar*292+thirdChar*471;
	}
	
	public char getFirstChar() {
		return firstChar;
	}

	public char getSecondChar() {
		return secondChar;
	}

	public char getThirdChar() {
		return thirdChar;
	}
	
	public int compareTo(Object o) {
		Trigram other=(Trigram)o;
		if (firstChar < other.firstChar) return -1;
		if (firstChar > other.firstChar) return 1;
		if (secondChar < other.secondChar) return -1;
		if (secondChar > other.secondChar) return 1;
		if (thirdChar < other.thirdChar) return -1;
		if (thirdChar > other.thirdChar) return 1;
		return 0;
	}
	
	public boolean equals(Object o) {
		Trigram other=(Trigram)o;
		if (firstChar==other.firstChar && secondChar==other.secondChar && thirdChar==other.thirdChar) return true;
		return false;
	}
	
	public int hashCode() {
		return hashCode;
	}
	
	public String toString() {
		char[] ca=new char[]{firstChar,secondChar,thirdChar};
		return new String(ca);
	}

}
