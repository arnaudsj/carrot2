
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.simplexml;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.util.MapUtils;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persist;

/**
 * A wrapper for serializing maps with {@link String}s as keys.
 */
@Root(name = "map")
@SuppressWarnings(
{
    "unchecked", "unused"
})
class MapSimpleXmlWrapper implements ISimpleXmlWrapper<Map>
{
    private Map map;

    @ElementMap(name = "map", attribute = true)
    private HashMap<String, SimpleXmlWrapperValue> forSerialization;

    public Map getValue()
    {
        return map;
    }

    public void setValue(Map value)
    {
        map = value;
    }

    @Persist
    private void beforeSerialization()
    {
        forSerialization = MapUtils.asHashMap(SimpleXmlWrappers.wrap(map));
    }

    @Commit
    private void afterSerialization()
    {
        map = SimpleXmlWrappers.unwrap(forSerialization);
    }
}
