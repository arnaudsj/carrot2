
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

package org.carrot2.clustering.stc;

import org.carrot2.text.suffixtrees.Edge;
import org.carrot2.text.suffixtrees.ExtendedBitSet;
import org.carrot2.text.suffixtrees.GSTNode;
import org.carrot2.text.suffixtrees.ISuffixableElement;

/**
 * Extends Generalized Suffix Tree in order to provide count of suffixed documents in each
 * node.
 */
public class PhraseNode extends GSTNode
{
    /** Suffixed documents */
    private final ExtendedBitSet docs;

    public ExtendedBitSet getInternalDocumentsRepresentation()
    {
        return docs;
    }

    public int getSuffixedDocumentsCount()
    {
        return docs.numberOfSetBits();
    }

    public int getSuffixedElementsCount()
    {
        throw new RuntimeException("CANNOT CALL THIS IN PHRASENODE");
    }

    public ExtendedBitSet getInternalSuffixedElementsRepresentation()
    {
        throw new RuntimeException("CANNOT CALL THIS IN PHRASENODE");
    }

    /**
     * Public constructor
     */
    public PhraseNode(STCTree t)
    {
        super(t);

        docs = new ExtendedBitSet(t.getCurrentDocumentIndex());

        docs.set(t.getCurrentDocumentIndex());
    }

    protected STCTree getSTCContainer()
    {
        return (STCTree) super.getContainer();
    }

    /**
     * adds a new indexed element to this node
     */
    public void addIndexedElement(int elementIndex)
    {
        docs.set(getSTCContainer().map(elementIndex));
        super.addIndexedElement(elementIndex);
    }

    /**
     * Propagates the element of some index up the nodes' hierarchy.
     */
    protected void propagateIndexedElementUp(int elementIndex)
    {
        Edge edge;
        PhraseNode parent = this;

        while ((edge = parent.getEdgeToParent()) != null)
        {
            parent = (PhraseNode) edge.getStartNode();

            if (parent.elementsInNode.get(elementIndex) == true)
            {
                break;
            }
            else
            {
                parent.elementsInNode.or(elementsInNode);
                parent.docs.or(docs);
            }
        }
    }

    /**
     * Detects 'eos-marker-only' nodes.
     */
    public boolean isEOSOnly()
    {
        return getSuffixableElement().get(getEdgeToParent().getStartIndex()) == ISuffixableElement.END_OF_SUFFIX;
    }
}
