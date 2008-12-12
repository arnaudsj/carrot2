
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

package org.carrot2.source.boss;

import org.carrot2.util.attribute.constraint.IValueHintMapping;

/**
 * Yahoo Boss region and language codes.
 * 
 * @see <a href="http://developer.yahoo.com/search/boss/boss_guide/supp_regions_lang.html">Supported regions</a>
 */
public enum BossLanguageCodes implements IValueHintMapping
{
    ARGENTINA("Argentina", "ar", "es"),
    AUSTRIA("Austria", "at", "de"),
    AUSTRALIA("Australia", "au", "en"),
    BRAZIL("Brazil", "br", "pt"),
    CANADA_ENGLISH("Canada - English", "ca", "en"),
    CANADA_FRENCH("Canada - French", "ca", "fr"),
    CATALAN("Catalan", "ct", "ca"),
    CHILE("Chile", "cl", "es"),
    COLUMBIA("Columbia", "co", "es"),
    DENMARK("Denmark", "dk", "da"),
    FINLAND("Finland", "fi", "fi"),
    INDONESIA_ENGLISH("Indonesia - English", "id", "en"),
    INDONESIA_INDONESIAN("Indonesia - Indonesian", "id", "id"),
    INDIA("India", "in", "en"),
    JAPAN("Japan", "jp", "jp"),
    KOREA("Korea", "kr", "kr"),
    MEXICO("Mexico", "mx", "es"),
    MALAYSIA_ENGLISH("Malaysia - English", "my", "en"),
    MALAYSIA_MALAYSIAN("Malaysia", "my", "ms"),
    NETHERLANDS("Netherlands", "nl", "nl"),
    NORWAY("Norway", "no", "no"),
    NEW_ZEALAND("New Zealand", "nz", "en"),
    PERU("Peru", "pe", "es"),
    PHILIPPINES("Philippines", "ph", "tl"),
    PHILIPPINES_ENGLISH("Philippines - English", "ph", "en"),
    RUSSIA("Russia", "ru", "ru"),
    SWEDEN("Sweden", "se", "sv"),
    SINGAPORE("Singapore", "sg", "en"),
    THAILAND("Thailand", "th", "th"),
    SWITZERLAND_GERMAN("Switzerland - German", "ch", "de"),
    SWITZERLAND_FRENCH("Switzerland - French", "ch", "fr"),
    SWITZERLAND_ITALIAN("Switzerland - Italian", "ch", "it"),
    GERMAN("German", "de", "de"),
    SPANISH("Spanish", "es", "es"),
    FRENCH("French", "fr", "fr"),
    ITALIAN("Italian", "it", "it"),
    UNITED_KINGDOM("United Kingdom", "uk", "en"),
    UNITED_STATES("United States - English", "us", "en"),
    UNITED_STATES_SPANISH("United States - Spanish", "us", "es"),
    VIETNAM("Vietnam", "vn", "vi"),
    VENEZUELA("Venezuela", "ve", "es");

    public final String friendlyName;
    public final String langCode;
    public final String regionCode;

    private BossLanguageCodes(String friendlyName, String regionCode, String langCode)
    {
        this.friendlyName = friendlyName;
        this.langCode = langCode;
        this.regionCode = regionCode;
    }

    public String getAttributeValue()
    {
        return name();
    }

    public String getUserFriendlyName()
    {
        return friendlyName;
    }
    
    @Override
    public String toString()
    {
        return getUserFriendlyName();
    }
}
