
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.attribute;

import java.util.List;

import org.carrot2.core.*;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;

/**
 * Attributes shared and inherited by many clustering algorithms. Extracted for consistency.
 */
@Bindable
public final class CommonAttributes
{
    // No instances.
    private CommonAttributes()
    {
        // no instances.
    }

    /**
     * Index of the first document/ search result to fetch. The index starts at zero.
     * 
     * @label Start Index
     * @level Advanced
     * @group Search query
     */
    @Input
    @Attribute(key = "start")
    public int start;

    /**
     * Maximum number of documents/ search results to fetch. The query hint can be used
     * by clustering algorithms to avoid creating trivial clusters (combination of query words).
     * 
     * @label Results
     * @level Basic
     * @group Search query
     */
    @Input
    @Attribute(key = "results")
    public int results;

    /**
     * Query to perform.
     * 
     * @label Query
     * @level Basic
     * @group Search query
     */
    @Input
    @Attribute(key = "query")
    public String query;

    /**
     * Estimated total number of matching documents.
     * 
     * @label Total Results
     * @group Search request information
     */
    @Output
    @Attribute(key = "results-total")
    public Long resultsTotal;

    /**
     * Documents returned by the search engine/ document retrieval system or
     * documents passed as input to the clustering algorithm.
     * 
     * @label Documents
     * @level Basic
     * @group Documents
     */
    @Input
    @Output
    @Attribute(key = "documents")
    public List<Document> documents;

    /**
     * Clusters created by the clustering algorithm.
     * 
     * @label Clusters
     * @group Clusters
     * @level Basic
     */
    @Output
    @Attribute(key = "clusters")
    public List<Cluster> clusters;

    /**
     * Total processing time in milliseconds. A sum of processing times of all components in the chain.
     * Total processing time may be greater than the sum of
     * {@link #processingTimeTotal} and {@link #processingTimeAlgorithm}.
     * 
     * @label Total Processing Time
     * @group Processing status
     */
    @Output
    @Attribute(key = "processing-time-total")
    public Long processingTimeTotal;

    /**
     * Data source processing time in milliseconds. A sum of processing times of all
     * {@link org.carrot2.core.IDocumentSource}s in the chain, including the
     * {@link org.carrot2.core.IProcessingComponent#beforeProcessing()} and
     * {@link org.carrot2.core.IProcessingComponent#afterProcessing()} hooks.
     * 
     * @label Data Source Processing Time
     * @group Data source status
     */
    @Output
    @Attribute(key = "processing-time-source")
    public Long processingTimeSource;

    /**
     * Algorithm processing time in milliseconds. A sum of processing times of all
     * {@link org.carrot2.core.IClusteringAlgorithm}s in the chain, including the
     * {@link org.carrot2.core.IProcessingComponent#beforeProcessing()} and
     * {@link org.carrot2.core.IProcessingComponent#afterProcessing()} hooks.
     * 
     * @label Clustering Algorithm Processing Time
     * @group Clustering algorithm status
     */
    @Output
    @Attribute(key = "processing-time-algorithm")
    public Long processingTimeAlgorithm;

    /**
     * Processing result title. A typical title for a processing result will be the query
     * used to fetch documents from that source. For certain document sources the query
     * may not be needed (on-disk XML, feed of syndicated news); in such cases, the input
     * component should set its title properly for visual interfaces such as the
     * workbench.
     * 
     * @label Title
     * @level Advanced
     * @group Search request information
     */
    @Output
    @Attribute(key = "processing-result.title")
    public String processingResultTitle;
}
