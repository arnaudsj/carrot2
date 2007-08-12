
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

package org.carrot2.webapp;

import java.io.*;
import java.net.SocketException;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.sf.ehcache.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayInputComponent;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.core.profiling.Profile;
import org.carrot2.core.profiling.ProfiledRequestContext;
import org.carrot2.util.RollingWindowAverage;
import org.carrot2.util.StringUtils;
import org.carrot2.webapp.serializers.C2XMLSerializer;

/**
 * Query processor servlet.
 *
 * @author Dawid Weiss
 */
public final class QueryProcessorServlet extends HttpServlet {
    /** Logger for activities and information */
    private Logger logger;

    /** Logger for queries */
    private volatile Logger queryLogger;

    /**
     * Define this system property to enable statistical information from
     * the query processor. A GET request to {@link QueryProcessorServlet}
     * with parameter <code>type=s</code> and <code>key</code> equal
     * to the value of this property will return plain text information
     * about the processing state.
     */
    private final static String STATISTICS_KEY = "stats.key";

    public static final String PARAM_Q = "q";
    public static final String PARAM_INPUT = "in";
    public static final String PARAM_ALG = "alg";
    public static final String PARAM_SIZE = "s";
    public static final String PARAM_XML_FEED_KEY = "xmlkey";
    public static final String PARAM_TYPE = "type";
    
    public static final String TYPE_CLUSTERS = "c";
    public static final String TYPE_DOCUMENTS = "d";
    public static final String TYPE_STATS = "s";
    public static final String TYPE_XML = "xml";

    private static final int DOCUMENT_REQUEST = 1;
    private static final int CLUSTERS_REQUEST = 2;
    private static final int PAGE_REQUEST = 3;
    private static final int STATS_REQUEST = 4;
    private static final int DOCUMENTS_CLUSTERS_REQUEST = 5;

    /** All available search settings */
    private SearchSettings searchSettings;

    /**
     * A map of {@link Broadcaster}s.
     */
    private final HashMap bcasters = new HashMap();

    /**
     * A process controller for input tabs. Each tab's name ({@link TabSearchInput#getShortName()})
     * corresponds to an identifier of a process defined in this controller.
     */
    private LocalControllerBase tabsController;

    /**
     * A process controller for algorithms. Each algorithm's name ({@link TabAlgorithm#getShortName()})
     * corresponds to an identifier of a process defined in this controller.
     */
    private LocalControllerBase algorithmsController;

    /** Cache of recent queries */
    private Cache ehcache;

    /** Serializer factory used to emit documents and clusters. */
    private SerializersFactory serializerFactory;

    /** A key required to get the XML feed with clusters and documents */
    private String xmlFeedKey;

    /** A counter for the number of executed queries. */
    private long executedQueries;

    /** Total time spent in clustering routines. */
    private long totalTime;

    /** Total number of successfully processed clustering queries. */
    private long goodQueries;

    /**
     * Average processing time for clustering requests (5 minutes, granularity of 10 seconds).
     */
    private final RollingWindowAverage averageProcessingTime = new RollingWindowAverage(
        5 * RollingWindowAverage.MINUTE, 10 * RollingWindowAverage.SECOND);

    /**
     * Average processing time for any request (5 minutes, granularity of 10 seconds).
     */
    private final RollingWindowAverage averageRequestTime = new RollingWindowAverage(
        5 * RollingWindowAverage.MINUTE, 10 * RollingWindowAverage.SECOND);

    /** If <code>true</code> the processes and components have been successfully read. */
    private boolean initialized;

    /** A bundle with localized messages */
    private ResourceBundle localizedMessages;

    /** Query expander (may be null) */
    private QueryExpander queryExpander;

    /**
     * Configure inputs.
     */
    public void init() throws ServletException {
        this.logger = Logger.getLogger(this.getServletName());

        // Run initial process and components configuration.
        try {
            initialize();
            logger.info("Initialized and accepting requests.");
        } catch (Throwable t) {
            logger.error("Could not initialize query processor servlet: " + StringUtils.chainExceptionMessages(t), t);
        }
    }

    /**
     * Wrap entire request processing in time counter.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        final long requestStart = System.currentTimeMillis();
        try
        {
            doGet0(request, response);
        }
        finally
        {
            synchronized (getServletContext())
            {
                this.averageRequestTime.add(System.currentTimeMillis(), System.currentTimeMillis() - requestStart);
            }
        }
    }

    /**
     * Process a HTTP GET request.
     */
    private void doGet0(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // check if initialized.
        synchronized (this) {
            if (!initialized) {
                // Attempt to repeat initialization procedure.
                logger.info("Repeating initialization.");
                try {
                    initialize();
                } catch (Throwable t) {
                    servletError(request, response, response.getOutputStream(), "Initialization error occurred." , t);
                    return;
                }
                logger.info("Initialization successful.");
            }
        }

        // identify request type first.
        final String type = request.getParameter(PARAM_TYPE);
        final SearchRequest searchRequest = searchSettings.parseRequest(request
            .getParameterMap(), request.getCookies(), queryExpander);

        // Initialize loggers depending on the application context.
        if (this.queryLogger == null) {
            synchronized (this) {
                // initialize query logger.
                String contextPath = request.getContextPath();

                contextPath = contextPath.replaceAll("[^a-zA-Z0-9]", "");
                if (contextPath == null || "".equals(contextPath)) {
                    contextPath = "ROOT";
                }

                this.queryLogger = Logger.getLogger("queryLog." + contextPath);
            }
        }

        request.setAttribute(Constants.RESOURCE_BUNDLE_KEY, localizedMessages);

        // Determine request type and redirect control
        final int requestType;
        if (TYPE_DOCUMENTS.equals(type)) {
            requestType = DOCUMENT_REQUEST;
        } else if (TYPE_CLUSTERS.equals(type)) {
            requestType = CLUSTERS_REQUEST;
        } else if (TYPE_STATS.equals(type)) {
            requestType = STATS_REQUEST;
        } else if (TYPE_XML.equals(type)) {
            requestType = DOCUMENTS_CLUSTERS_REQUEST;
        } else {
            requestType = PAGE_REQUEST;
        }

        final OutputStream os = response.getOutputStream();
        try {
            if (requestType == DOCUMENT_REQUEST || requestType == CLUSTERS_REQUEST || requestType == DOCUMENTS_CLUSTERS_REQUEST) {
                // request for documents or clusters
                if (searchRequest.query.length() == 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
    
                // If the key is missing or wrong
                if (requestType == DOCUMENTS_CLUSTERS_REQUEST
                    && ("disabled".equals(xmlFeedKey) || !xmlFeedKey.equals(request
                        .getParameter(PARAM_XML_FEED_KEY))))
                {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
    
                processSearchQuery(os, requestType, searchRequest, request, response);
            } else if (requestType == STATS_REQUEST) {
                // request for statistical information about the engine.
                // we check if the request contains a special token known to the
                // administrator of the engine (so that statistics are available
                // only to certain people).
                final String statsKey = System.getProperty(STATISTICS_KEY);
                if (statsKey != null && statsKey.equals(request.getParameter("key"))) {
                    synchronized (getServletContext()) {
                        processStatsQuery(os, response);
                    }
                } else {
                    // unauthorized stats request.
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                final PageSerializer serializer = serializerFactory.createPageSerializer(request);
                response.setContentType(serializer.getContentType());
                serializer.writePage(os, searchSettings, searchRequest);
            }
        } catch (IOException e) {
            if (e instanceof SocketException
                || e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException")
                || e.getClass().getName().equals("org.mortbay.jetty.EofException")) {
                logger.debug("Client abort when writing response: "
                    + StringUtils.chainExceptionMessages(e));
            } else {
                logger.warn("Unrecognized I/O exception when writing response: " 
                    + StringUtils.chainExceptionMessages(e), e);
            }
        }
    }

    /**
     * 
     */
    private void processStatsQuery(OutputStream os, HttpServletResponse response)
        throws IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final Writer output = new OutputStreamWriter(os, "UTF-8");

        output.write("clustering-total-queries: " + executedQueries + "\n");
        output.write("clustering-good-queries: " + goodQueries + "\n");

        final long average = (long) this.averageProcessingTime.getCurrentAverage();
        if (average > 0)
        {
            output.write("clustering-ms-per-query: " + average + "\n");
            output.write("clustering-updates-in-window: " 
                + this.averageProcessingTime.getUpdatesInWindow() + "\n");
        }

        final long requestAverage = (long) averageRequestTime.getCurrentAverage();
        if (requestAverage > 0)
        {
            output.write("all-ms-per-request: " + requestAverage + "\n");
            output.write("all-updates-in-window: " 
                + this.averageRequestTime.getUpdatesInWindow() + "\n");
        }

        output.write("jvm.freemem: " + Runtime.getRuntime().freeMemory() + "\n");
        output.write("jvm.totalmem: " + Runtime.getRuntime().totalMemory() + "\n");

        final Statistics stats = this.ehcache.getStatistics();
        output.write("ehcache.hits: " + stats.getCacheHits() + "\n");
        output.write("ehcache.misses: " + stats.getCacheMisses() + "\n");
        output.write("ehcache.memhits: " + stats.getInMemoryHits() + "\n");
        output.write("ehcache.diskhits: " + stats.getOnDiskHits() + "\n");

        // Return server configuration for remote testing.
        output.write("algorithms: " + StringUtils.toString(algorithmsController.getProcessIds(), ",") + "\n");
        output.write("inputs: " + StringUtils.toString(this.tabsController.getProcessIds(), ",") + "\n");

        output.flush();
    }

    /**
     * A thread that fetches search results and caches them.
     */
    private class SearchResultsDownloaderThread extends Thread {
        private final SearchRequest searchRequest;
        private final Broadcaster bcaster;
        private final Serializable queryHash;

        public SearchResultsDownloaderThread(SearchRequest searchRequest, Serializable queryHash, Broadcaster bcaster) {
            this.searchRequest = searchRequest;
            this.bcaster = bcaster;
            this.queryHash = queryHash;
        }

        public void run() {
            final HashMap props = new HashMap();
            props.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
                    Integer.toString(searchRequest.getInputSize()));
            props.put(BroadcasterPushOutputComponent.BROADCASTER, bcaster);
            try {
                tabsController.query(searchRequest.getInputTab().getShortName(), searchRequest.getActualQuery(), props);
            } catch (Exception e) {
                logger.warn("Error running input query.", e);
                this.bcaster.endProcessingWithError(e);
                return;
            }

            try {
                // add documents to the cache
                ehcache.put(
                        new net.sf.ehcache.Element(queryHash,
                                new SearchResults(bcaster.getDocuments())));
            } catch (CacheException e) {
                logger.error("Could not save results to cache.", e);
            }
        }
    }

    /**
     * Process a document or cluster search query.
     */
    private void processSearchQuery(OutputStream os, final int requestType,
            SearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        final TabAlgorithm algorithmTab = searchRequest.getAlgorithm();
        final String queryHash = searchRequest.getInputAndSizeHashCode();
        final Broadcaster bcaster;

        // check if we can process this request and return immediately if not.
        if (false == serializerFactory.acceptRequest(request, response)) {
            logger.debug("Request unacceptable (denied by factory).");
            return;
        }

        synchronized (getServletContext()) {
            final Broadcaster existingbcaster = (Broadcaster ) bcasters.get(queryHash);
            if (existingbcaster != null) {
                //
                // Existing broadcaster is reused.
                //
                bcaster = existingbcaster;
                logger.debug("Broadcaster reused: " + searchRequest.query);
            } else {
                final net.sf.ehcache.Element value = ehcache.get(queryHash);
                if (value != null) {
                    //
                    // Recreate a broadcaster from the cache.
                    //
                    logger.debug("Broadcaster recovered from cache: " + searchRequest.query);
                    bcaster = new Broadcaster((SearchResults) value.getObjectValue());
                    bcasters.put(queryHash, bcaster);
                } else {
                    //
                    // A new broadcaster is needed.
                    //
                    logger.debug("Broadcaster created: " + searchRequest.query);
                    bcaster = new Broadcaster();
                    bcasters.put(queryHash, bcaster);
                    // Start a background thread for pulling documents from the input.
                    new SearchResultsDownloaderThread(searchRequest, queryHash, bcaster).start();
                }
            }
            // attach current thread to the broadcaster.
            bcaster.attach();
        }

        long filtersProcessingTime = 0;
        boolean requestGood = false;  // Set to true after a successful clustering request. 
        try {
            final Iterator docIterator = bcaster.docIterator();
            if (requestType == DOCUMENT_REQUEST) {
                //
                // document request
                //
                final long start = System.currentTimeMillis();
                final RawDocumentsSerializer serializer = serializerFactory.createRawDocumentSerializer(request);
                response.setContentType(serializer.getContentType());
                serializer.startResult(os, searchRequest.getActualQuery());
                try {
                    while (docIterator.hasNext()) {
                        serializer.write((RawDocument) docIterator.next());
                    }
                } catch (BroadcasterException e) {
                    serializer.processingError(e.getCause());
                }
                final long totalTime = System.currentTimeMillis() - start;

                // Technically, the total time includes also the serialization time,
                // but I guess this is negligible compared to fetching anyway
                serializer.endResult(totalTime);
            } else {
                //
                // clustering request.
                //
                final HashMap props = new HashMap();
                props.put(ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, docIterator);
                props.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
                        Integer.toString(searchRequest.getInputSize()));

                RawClustersSerializer serializer;
                if (requestType == CLUSTERS_REQUEST) {
                    serializer = serializerFactory.createRawClustersSerializer(request);
                }
                else {
                    // DOCUMENTS_CLUSTERS_REQUEST
                    serializer = new C2XMLSerializer();
                }
                response.setContentType(serializer.getContentType());
                try {
                    logger.info("Clustering results using: " + algorithmTab.getShortName());
                    final ProcessingResult result =
                        algorithmsController.query(algorithmTab.getShortName(),
                                searchRequest.getActualQuery(), props);
                    final ArrayOutputComponent.Result collected =
                        (ArrayOutputComponent.Result) result.getQueryResult();
                    final List clusters = collected.clusters;
                    final List documents = bcaster.getDocuments();

                    serializer.startResult(os, documents, request, searchRequest.getActualQuery());
                    for (Iterator i = clusters.iterator(); i.hasNext();) {
                        serializer.write((RawCluster) i.next());
                    }

                    List profiles = ((ProfiledRequestContext) result
                        .getRequestContext()).getProfiles();
                    Profile [] array = (Profile []) profiles
                        .toArray(new Profile [profiles.size()]);

                    // Count only filters.
                    for (int i = 1; i < array.length - 1; i++)
                    {
                        filtersProcessingTime += array[i].getTotalTimeElapsed();
                    }

                    serializer.endResult(filtersProcessingTime);

                    requestGood = true; // Mark good request here.

                    if (queryLogger.isEnabledFor(Level.INFO)) {
                        logQuery(searchRequest, filtersProcessingTime);
                    }
                } catch (BroadcasterException e) {
                    // broadcaster exceptions are shown in the documents iframe,
                    // so we simply emit no clusters.
                    serializer.startResult(os, Collections.EMPTY_LIST, request, searchRequest.query);
                    serializer.processingError(e);
                    serializer.endResult(filtersProcessingTime);
                } catch (Exception e) {
                    logger.warn("Error running input query.", e);
                    serializer.startResult(os, Collections.EMPTY_LIST, request, searchRequest.query);
                    serializer.processingError(e);
                    serializer.endResult(filtersProcessingTime);
                }
            }
        } finally {
            synchronized (getServletContext()) {
                if (requestType == CLUSTERS_REQUEST) {
                    this.executedQueries++;
                    if (requestGood) {
                        this.goodQueries++;
                        this.totalTime += filtersProcessingTime;
                        this.averageProcessingTime.add(System.currentTimeMillis(), filtersProcessingTime);
                    }
                }

                // detach current thread from the broadcaster and
                // remove it if necessary.
                bcaster.detach();
                if (!bcaster.inUse()) {
                    bcasters.remove(queryHash);
                    logger.debug("Broadcaster removed: " + searchRequest.query);
                }
            }
        }
    }

    /**
     * Logs the query for further analysis
     */
    private void logQuery(final SearchRequest searchRequest, long clusteringTime) {
        queryLogger.info(
                  searchRequest.getAlgorithm().getShortName()
                + ","
                + searchRequest.getInputTab().getShortName()
                + ","
                + searchRequest.getInputSize()
                + ","
                + clusteringTime
                + ","
                + searchRequest.query);
    }

    /**
     * Initializes components and processes. Sets a flag if successful.
     */
    private void initialize() throws ServletException {
        final ServletContext context = super.getServletContext();

        this.searchSettings = new SearchSettings();

        // Initialize default input size and allowed input sizes.
        int defaultInputSize = 100;
        try {
            defaultInputSize = Integer.parseInt(getServletConfig()
                .getInitParameter("inputSize.default"));
        }
        catch (Exception e){
            logger.warn("Could not parse inputSize.default: " + getServletConfig()
                .getInitParameter("inputSize.default"));
        }

        // Initialize the allowed input sizes
        int [] allowedInputSize = new int [] {50, 100, 200, 400};
        String sizesString = getServletConfig().getInitParameter("inputSize.choices");
        if (sizesString != null)
        {
            try
            {
                String [] split = sizesString.split(",");
                allowedInputSize = new int [split.length];
                for (int i = 0; i < split.length; i++)
                {
                    allowedInputSize[i] = Integer.parseInt(split[i]);
                }
            }
            catch (NumberFormatException e)
            {
                logger.warn("Could not parse inputSize.choices: " + getServletConfig()
                    .getInitParameter("inputSize.default"));
                allowedInputSize = new int [] {50, 100, 200, 400};
            }
        }
        searchSettings.setAllowedInputSizes(allowedInputSize, defaultInputSize);

        // Initialize XML feed key
        this.xmlFeedKey = InitializationUtils.initializeXmlFeedKey(getServletConfig());

        // Initialize serializers.
        this.serializerFactory = InitializationUtils.initializeSerializers(logger, getServletConfig());

        // Initialize cache.
        this.ehcache = InitializationUtils.initializeCache(getServletConfig());

        // Create processes for collecting documents from input tabs.
        String inputsPath = this.getInitParameter("inputs.path");
        if (inputsPath == null || inputsPath.trim().equals(""))
        {
        	inputsPath = "/inputs";
        }
        final File inputScripts = new File(context.getRealPath(inputsPath));
        this.tabsController = InitializationUtils.initializeInputs(logger, inputScripts, searchSettings);

        // Create processes for algorithms.
        String algorithmsPath = this.getInitParameter("algorithms.path");
        if (algorithmsPath == null || algorithmsPath.trim().equals(""))
        {
        	algorithmsPath = "/algorithms";
        }
        final File algorithmScripts = new File(context.getRealPath(algorithmsPath));
        this.algorithmsController = InitializationUtils.initializeAlgorithms(logger, algorithmScripts, searchSettings);

        // Initialize the bundle for localized messages
        this.localizedMessages = InitializationUtils.initializeResourceBundle(
            getServletConfig());

        // Initialize query expander
        this.queryExpander = InitializationUtils.initializeQueryExpander(logger, getServletConfig());

        // Mark as initialized.
        this.initialized = true;
    }

    /**
     * Attempts to send an internal server error HTTP error, if possible.
     * Otherwise simply pushes the exception message to the output stream.

     * @param message Message to be printed to the logger and to the output stream.
     * @param t Exception that caused the error.
     */
    protected void servletError(HttpServletRequest origRequest, HttpServletResponse origResponse, OutputStream os, String message, Throwable t) {
        logger.error(message, t);

        final Writer writer;
        try {
            writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
        } catch (UnsupportedEncodingException e) {
            final String msg = Constants.ENCODING_UTF + " must be supported.";
            logger.fatal(msg);
            throw new RuntimeException(msg);
        }

        if (false == origResponse.isCommitted()) {
            // Reset the buffer and previous status code.
            origResponse.reset();
            origResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            origResponse.setContentType(Constants.MIME_HTML_CHARSET_UTF);
        }

        // Response committed. Just push the error to the output stream.
        try {
            writer.write("<h1 style=\"color: red; margin-top: 1em;\">");
            writer.write("Internal server error");
            writer.write("</h1>");
            writer.write("<b>URI</b>: " + origRequest.getRequestURI() + "\n<br/><br/>");
            serializeException(writer, t);
            writer.flush();
        } catch (IOException e) {
            // not much to do in such case (connection broken most likely).
            logger.warn("Exception info could not be returned to client (I/O).");
        }
    }

    /**
     * Utility method to serialize an exception and its stack trace to simple HTML.
     */
    private final static void serializeException(Writer osw, Throwable t) throws IOException {
        Throwable temp = t;
        osw.write("<table border=\"0\" cellspacing=\"4\">");
        while (temp != null) {
            osw.write("<tr>");
            osw.write("<td style=\"text-align: right\">" + (temp != t ? "caused by &rarr;" : "Exception:") + "</td>");
            osw.write("<td style=\"color: gray; font-weight: bold;\">" + temp.getClass().getName() + "</td>");
            osw.write("<td style=\"color: red; font-weight: bold;\">" + (temp.getMessage() != null ? temp.getMessage() : "(no message)") + "</td>");
            osw.write("</tr>");

            if (temp instanceof ServletException) {
                temp = ((ServletException) temp).getRootCause();
            } else {
                temp = temp.getCause();
            }
        }
        osw.write("</table>");

        osw.write("<br/><br/><b>Stack trace:</b>");
        osw.write("<pre style=\"border-left: 1px solid red; padding: 3px; font-family: monospace;\">");
        final PrintWriter pw = new PrintWriter(osw);
        t.printStackTrace(pw);
        pw.flush();
        osw.write("</pre>");
    }
}
