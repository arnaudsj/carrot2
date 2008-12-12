
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

package org.carrot2.text.suffixtrees2;

import java.util.*;

/**
 * This class implements Esko Ukkonnen's algorithm for creating a suffix tree. The
 * implementation has been derived from the C-version by Mark Nelson:
 * <p>
 * <b>Fast String Searching With Suffix Trees <i>Mark Nelson </i></b> Dr Dobb's Journal,
 * August 1996 http://softlab.od.ua/algo/data/suftrees/suffixt.htm (2000)
 */
public class SuffixTree<T extends Node> implements Iterable<T>
{
    /**
     * {@link Node} factory for internal tree nodes.
     */
    private final INodeFactory<? extends T> nodeFactory;

    /**
     * Number of created nodes.
     */
    private int nodesCount;

    /**
     * A hash map of edges leaving each node.
     */
    final HashMap<NodeEdge, Edge> edges = new HashMap<NodeEdge, Edge>();

    /**
     * This is the root node of the tree.
     */
    T rootNode;

    /**
     * Sequence of elements to consider.
     */
    ISequence input;

    /*
     * Temporary reusable object for lookups.
     */
    private NodeEdge temp = new NodeEdge();

    /*
     * 
     */
    public SuffixTree(INodeFactory<? extends T> nodeFactory)
    {
        this.nodeFactory = nodeFactory;
    }

    /**
     * Recreates this suffix tree for another list of elements. Returns the root node of
     * the resulting tree.
     */
    public Node build(ISequence input)
    {
        // Reset internal structures.
        this.input = input;
        edges.clear();
        nodesCount = 0;
        rootNode = nodeFactory.createNode(this);

        // Loop through all prefixes of the input.
        final Suffix activePoint = new Suffix(rootNode, 0, -1);
        final int maxIndex = this.input.size();
        for (int i = 0; i < maxIndex; i++)
        {
            insertPrefix(activePoint, i);
        }

        return rootNode;
    }

    /**
     * @return Returns <code>true</code> if this tree has a suffix (path from the root
     *         node to a leaf node) matching the given sequence. Note that object codes in
     *         both sequences must match each other.
     */
    public boolean hasSuffix(ISequence seq)
    {
        final Node n = getMatchingNode(seq);
        return n.isLeaf();
    }

    /**
     * @return Returns an iterator over all nodes of this tree.
     */
    public Iterator<T> iterator()
    {
        return new DepthFirstNodeIterator<T>(rootNode);
    }

    /**
     * Returns a {@link Node}-based suffix tree.
     */
    public static SuffixTree<Node> newSuffixTree()
    {
        return newSuffixTree(new DefaultNodeFactory());
    }

    /**
     * Returns a suffix tree on top of an arbitrary node factory.
     */
    public static <E extends Node> SuffixTree<E> newSuffixTree(INodeFactory<E> factory)
    {
        return new SuffixTree<E>(factory);
    }

    /**
     * @return If <code>seq</code> exists in the tree (either as an explicit node or an
     *         implicit node along the edge), then this method returns the node closest to
     *         the end of the sequence (maximum prefix of <code>seq</code> that ends at
     *         an explicit node in the tree). Otherwise this method returns
     *         <code>null</code>.
     */
    private Node getMatchingNode(ISequence seq)
    {
        Node node = this.rootNode;
        int index = 0;

        while (index < seq.size())
        {
            final Edge e = node.getEdge(seq.objectAt(index));
            if (e == null)
            {
                /* Not found. */
                return null;
            }

            // Ensure all objects along this edge really match.
            final int endIndex = e.getEndIndex();
            for (int i = e.getStartIndex(); i <= endIndex; i++, index++)
            {
                if (index == seq.size())
                {
                    return node;
                }

                if (input.objectAt(i) != seq.objectAt(index))
                {
                    /* Not found. */
                    return null;
                }
            }

            node = e.endNode;
        }

        return node;
    }

    /**
     * The heart of Ukkonnen's algorithm. Inserts a single prefix to the tree while
     * retaining its structure. The source code to this function has comments from Mark
     * Nelson's excellent paper published in Dr Dobb's journal.
     * 
     * @param active Active point in the suffix tree.
     * @param lastElementIndex The index of currently inserted element from
     *            getCurrentElement().
     * @return current end point (becomes active point if more elements are to be
     *         inserted).
     */
    final Suffix insertPrefix(Suffix active, int lastElementIndex)
    {
        Node parent_node = null;
        Node last_parent_node = null;

        while (true)
        {
            Edge edge;
            parent_node = active.originNode;

            /*
             * Step 1 is to try and find a matching edge for the given node. If a matching
             * edge exists, we are done adding edges, so we break out of this big loop.
             */
            if (active.isExplicit())
            {
                /*
                 * Explicit node, check if it has an edge starting with current element.
                 */
                edge = active.originNode.getEdge(input.objectAt(lastElementIndex));

                /*
                 * If it does, do nothing (path compression, an implicit node is created).
                 */
                if (edge != null) break;
            }
            else
            {
                /*
                 * implicit node, a little more complicated because we have to split the
                 * edge and create a new node IF the implicit node doesn't match inserted
                 * element.
                 */
                edge = active.originNode
                    .getEdge(input.objectAt(active.firstElementIndex));

                int span = active.lastElementIndex - active.firstElementIndex;

                if (input.objectAt(edge.firstElementIndex + span + 1) == input
                    .objectAt(lastElementIndex))
                {
                    break;
                }

                /*
                 * We must split the edge. The newly created node becomes parent node.
                 */
                parent_node = edge.splitEdge(active);
            }

            /*
             * We didn't find a matching edge, so we create a new one, add it to the tree
             * at the parent node position, and insert it into the hash table. When we
             * create a new node, it also means we need to create a suffix link to the new
             * node from the last node we visited.
             */
            parent_node.createChildNode(lastElementIndex, input.size() - 1);

            if (last_parent_node != null && last_parent_node != rootNode)
            {
                last_parent_node.suffixNode = parent_node;
            }

            last_parent_node = parent_node;

            /* This final step is where we move to the next smaller suffix */
            if (active.originNode == rootNode)
            {
                active.firstElementIndex++;
            }
            else
            {
                active.originNode = active.originNode.suffixNode;
            }

            active.canonize();
        }

        if (last_parent_node != null && last_parent_node != rootNode)
        {
            last_parent_node.suffixNode = parent_node;
        }

        /* Now the end point is the next active point. */
        active.lastElementIndex++;
        active.canonize();

        return active;
    }

    /*
     * 
     */
    final NodeEdge removeEdge(Node node, int objectCode)
    {
        this.temp.node = node;
        this.temp.objectCode = objectCode;

        this.edges.remove(temp);
        return temp;
    }

    /*
     * 
     */
    final Edge getEdge(Node node, int objectCode)
    {
        this.temp.node = node;
        this.temp.objectCode = objectCode;
        return this.edges.get(temp);
    }

    /*
     * 
     */
    final Node createNode()
    {
        nodesCount++;
        return nodeFactory.createNode(this);
    }

    /**
     * Empty sequence.
     */
    private final static ISequence EMPTY_SEQUENCE = new IntSequence(new int [0]);
    
    /**
     * Returns a {@link ISequence} of elements from the node's parent edge to the
     * node.
     */
    public ISequence getSequenceToParent(Node node)
    {
        if (node.container != this)
        {
            throw new IllegalArgumentException();
        }
        
        if (node.edgeToParent == null)
        {
            return EMPTY_SEQUENCE;
        }

        final int length = node.edgeToParent.length();
        final int [] array = new int [length];

        int i = 0;
        int j = node.edgeToParent.getStartIndex();
        for (; j <= node.edgeToParent.getEndIndex(); i++, j++)
        {
            array[i] = input.objectAt(j);
        }

        return new IntSequence(array);
    }

    /**
     * Returns a {@link ISequence} of elements from the root node to this node (full path).
     */
    public ISequence getSequenceToRoot(Node node)
    {
        if (node.container != this)
        {
            throw new IllegalArgumentException();
        }
        
        final int start = node.getSuffixStartIndex();
        final int end = node.getSuffixEndIndex();
        final int length = end - start + 1;
        final int [] array = new int [length];

        int i = 0;
        int j = start;
        for (; j <= end; i++, j++)
        {
            array[i] = input.objectAt(j);
        }

        return new IntSequence(array);
    }
    
    /*
     * 
     */
    @Override
    public String toString()
    {
        return "SuffixTree[edges=" + edges.size() + ", nodes created=" + nodesCount + "]";
    }
}
