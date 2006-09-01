
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

package org.carrot2.input.odp;

import java.io.*;

/**
 * Represents a single ODP external page.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class MutableExternalPage implements ExternalPage, Serializable
{
    /** This ExtenralPage's title */
    private String title;

    /** This MutableExternalPage's description */
    private String description;
    
    /** This MutableExternalPage's url */
    private String url;

    /**
     * Creates a new empty MutableExternalPage.
     */
    public MutableExternalPage()
    {
    }

    /**
     * @param title
     * @param description
     */
    public MutableExternalPage(String title, String description)
    {
        this.title = title;
        this.description = description;
    }
    
    /**
     * @param title
     * @param description
     */
    public MutableExternalPage(String title, String description, String url)
    {
        this.title = title;
        this.description = description;
        this.url = url;
    }
    
    /**
     * Returns this MutableExternalPage's <code>description</code>.
     * 
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets this MutableExternalPage's <code>description</code>.
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns this MutableExternalPage's <code>title</code>.
     * 
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets this MutableExternalPage's <code>title</code>.
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns this MutableExternalPage's <code>url</code>.
     * 
     */
    public String getUrl()
    {
        return url;
    }
    /**
     * Sets this MutableExternalPage's <code>url</code>.
     * 
     * @param url
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (obj.getClass() != getClass())
        {
            return false;
        }

        return title.equals(((MutableExternalPage) obj).title)
            && description.equals(((MutableExternalPage) obj).description);
    }
}