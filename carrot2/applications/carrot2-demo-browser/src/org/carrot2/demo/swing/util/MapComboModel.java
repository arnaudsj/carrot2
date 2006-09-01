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

package org.carrot2.demo.swing.util;

import java.util.*;

import javax.swing.DefaultComboBoxModel;

/**
 * A combo box model which displays a set of values of a given hashmap, but also
 * remembers the currently selected key.
 * 
 * @author Dawid Weiss
 */
public final class MapComboModel extends DefaultComboBoxModel
{
    private final List ids;
    private final List values;
    int selected = 0;

    public MapComboModel(Map processIdToProcessNameMap)
    {
        final int size = processIdToProcessNameMap.size();
        this.ids = new ArrayList(size);
        this.values = new ArrayList(size);

        List entryList = new ArrayList(processIdToProcessNameMap.entrySet());
        Collections.sort(entryList, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                Map.Entry e1 = (Map.Entry) o1;
                Map.Entry e2 = (Map.Entry) o2;
                return e1.getValue().toString().compareTo(
                    e2.getValue().toString());
            }
        });

        for (Iterator iter = entryList.iterator(); iter.hasNext();)
        {
            final Map.Entry entry = (Map.Entry) iter.next();
            ids.add(entry.getKey());
            values.add(entry.getValue());
        }
    }

    public void setSelectedItem(Object anItem)
    {
        // We assume there is a small number of values,
        // so this inefficient lookup doesn't really matter.
        final int index = values.indexOf(anItem);
        if (index == -1)
            throw new IllegalArgumentException(
                "Process description is not defined: " + anItem);
        selected = index;
    }

    public Object getSelectedItem()
    {
        return values.get(selected);
    }

    public int getSize()
    {
        return ids.size();
    }

    public Object getElementAt(int index)
    {
        return values.get(index);
    }

    public Object getSelectedKey()
    {
        return ids.get(selected);
    }

    public Object getSelectedValue()
    {
        return getSelectedItem();
    }
}