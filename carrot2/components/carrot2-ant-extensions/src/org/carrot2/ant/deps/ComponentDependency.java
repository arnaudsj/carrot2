
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

package org.carrot2.ant.deps;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.carrot2.ant.tasks.BringToDateTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 * A class that represents a component and the information
 * about its dependencies (other components it requires to
 * build or run).
 * 
 * @author Dawid Weiss
 */
public class ComponentDependency implements Serializable {

	/**
     * A more verbose description of the component,
     * used when printing summaries.
     */
    private String description;

	/** Name of this component. */
    private String name;

    /** 
     * A dependency list {@link DependencyElement} 
     * objects of this component. 
     */
    private ArrayList dependencies = new ArrayList();

    /** 
     * A list of objects this component provides 
     * (Provides objects) 
     */
    private ArrayList provides = new ArrayList();

    /** A file pointer to this component's xml */
    private File file;

    public ComponentDependency(Project project, File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Dependency file must not be null.");
        }

        if (file.exists() == false) {
            throw new IOException("Dependency file cannot be read: " +
                file.getAbsolutePath());
        }

        if (file.canRead() == false) {
            throw new IOException("Dependency file cannot be read: " +
                file.getAbsolutePath());
        }

        this.file = file;

        try {
            // parse the dependency file.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // TODO: validate xml before reading it.
            // this requires adding a custom error handler and adding DTD info to all component
            // descriptors (currently certain libs don't have it).
            // factory.setValidating(true);
            // factory.setErrorHandler(...);

            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            
            // switch off annoying messages...
            CatalogManager.getStaticManager().setIgnoreMissingProperties(true);

            CatalogResolver catalog = new CatalogResolver();
            catalog.getCatalog().parseCatalog(this.getClass().getResource("/res/catalog.xml"));
            catalog.getCatalog().parseAllCatalogs();
            builder.setEntityResolver(catalog);
    
            Document dependency = null;
            dependency = builder.parse(file);
    
            Element root = dependency.getDocumentElement();
            if (!"component".equals(root.getNodeName())) {
                throw new SAXException(
                    "Root element name should be: 'component'");
            }
    
            this.name = root.getAttribute("name");
            if (name == null || "".equals(name.trim())) {
                throw new SAXException("Component must have a name.");
            }
            
            this.description = root.getAttribute("description");
            if (description == null || "".equals(description.trim())) {
                description = name; 
            }
    
            NodeList list = root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node n = list.item(i);
                switch (n.getNodeType()) {
                    case Node.ELEMENT_NODE:
                        if ("provides".equals(n.getNodeName())) {
                            this.provides.add(
                                new ProvidesElement(project, this.file.getParentFile(), (Element) n, this));
                        } else if ("dependency".equals(n.getNodeName())) {
                            this.dependencies.add(
                                new DependencyElement(
                                    this.file.getParentFile(), (Element) n));
                        } else
                            throw new SAXException("Unexpected node: "
                                + n.getNodeName());
                        break;
                    case Node.TEXT_NODE:
                        if (!n.getNodeValue().trim().equals("")) {
                            throw new SAXException("Unexpected text in 'component' node.");
                        }
                        break;
                    case Node.COMMENT_NODE:
                        continue;
                    default:
                        throw new SAXException("Unexpected node: "
                            + n.getNodeName());
                }
            }
        } catch (Exception e) {
            throw new IOException("Problems parsing component descriptor " 
                    + file + " (" + e + ")");
        }
    }

    /**
     * Returns the name of the component.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the File pointer
     */
    public File getFile() {
        return file;
    }

    /**
     * Collect all {@link ComponentDependency} objects required by this
     * component.
     * 
     * @param componentsMap Name-to-ComponentDependency objects
     * map used to resolve named dependencies.
     * @return An array of ComponentDependency objects, sorted topologically from
     * left to right (rightmost objects without dependencies).
     */
    public ComponentInProfile [] getAllRequiredComponentDependencies(Map componentsMap, String profile)
        throws BuildException {
        return getAllRequiredComponentDependencies(componentsMap, profile, false);
    }

    /**
     * Collect all ComponentDependency objects required by this
     * component.
     * 
     * @param componentsMap Name-to-ComponentDependency objects
     * map used to resolve named dependencies.
     * @return An array of ComponentDependency objects, sorted topologically from
     * left to right (rightmost objects without dependencies).
     */
	public ComponentInProfile [] getAllRequiredComponentDependencies(Map componentsMap, String profile, boolean nocopy)
        throws BuildException {
        ComponentInProfile root = new ComponentInProfile(this, profile, false);
        ArrayList dependencyList = new ArrayList();
        tsort(root, componentsMap, new HashMap(), new Stack(), dependencyList, nocopy, true);

        ComponentInProfile [] dependencyArray = new ComponentInProfile [ dependencyList.size() ];
        dependencyList.toArray(dependencyArray);
        return dependencyArray;
    }

    private static final String VISITING = "VISITING";
    private static final String VISITED =  "VISITED";

    /**
     * Topological sort of the dependencies. This code borrowed
     * from the ANT project source.
     *
     * @author duncan@x180.com
     */
    private final static void tsort(ComponentInProfile root, Map targets,
                             Map state, Stack visiting,
                             ArrayList ret, boolean nocopy, boolean atroot)
        throws BuildException {
        state.put(root, VISITING);
        visiting.push(root);

        for (Iterator en = root.component.getDependencyElements().iterator(); en.hasNext();) {
            final DependencyElement dep = (DependencyElement) en.next();

            // skip profiles that don't match.
            if (dep.getProfile() != null && (root.profile == null || !root.profile.equals(dep.getProfile()))) {
                continue;
            } 

            // skip dependencies if nocopy attribute is set.
            if (nocopy && dep.isNoCopy()) {
                continue;
            }

            // skip dependencies if noexport is set and it's not the root component.
            if (atroot == false && dep.isNoExport()) {
                continue;
            }

            ComponentDependency depComponent = (ComponentDependency) targets.get(dep.getName());

            // If the target does not exist, throw an exception.
            if (depComponent == null) {
                StringBuffer sb = new StringBuffer("Component '");
                sb.append(dep.getName());
                sb.append("' does not exist. ");
                visiting.pop();
                if (!visiting.empty()) {
                    sb.append("It is used from: '");
                    sb.append(root);
                    sb.append("'.");
                }
                throw new BuildException(sb.toString());
            }

            ComponentInProfile dependency = new ComponentInProfile(depComponent, dep.getInProfile(), dep.isNoCopy());

            String m = (String) state.get(dependency);
            if (m == null) {
                // Not visited yet
                tsort(dependency, targets, state, visiting, ret, nocopy, false);
            } else if (m == VISITING) {
                // Currently visiting this node, so have a cycle
                StringBuffer sb = new StringBuffer("Circular dependency: ");
                sb.append(dependency);
                ComponentInProfile c;
                do {
                    c = (ComponentInProfile) visiting.pop();
                    sb.append(" <- ");
                    sb.append(c);
                } while (!c.equals(dependency));
                throw new BuildException(sb.toString());
            } else if (m == VISITED) {
                // Ignore this state.
            } else {
                throw new RuntimeException("Unreachable state: " + m);
            }
            root.addDependency(dependency);
        }

        ComponentInProfile p = (ComponentInProfile) visiting.pop();
        if (root != p) {
            throw new RuntimeException("Unexpected internal error: expected to "
                + "pop " + root + " but got " + p);
        }
        state.put(root, VISITED);
        ret.add(root);
    }


	protected List getDependencyElements() {
        return this.dependencies;
	}

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Component [name=\"" + getName() + "\"], dependencies: [\n");

        for (Iterator i = dependencies.iterator(); i.hasNext(); ) {
            DependencyElement dep = (DependencyElement) i.next();
            buf.append(dep);
        }

        buf.append("]\n");
        return buf.toString();
    }

	/**
	 * Brings the component up-to-date, based on 'provides'
     * and build elements.
	 */
	public void bringUpToDate(Project project, String currentProfile, BringToDateTask task) throws BuildException {
        for (Iterator p = this.provides.iterator(); p.hasNext();)
        {
            final ProvidesElement provides = (ProvidesElement) p.next();

            if (provides.getProfile() != null && (currentProfile == null || !currentProfile.equals(provides.getProfile()))) {
                continue;
            } 

            provides.bringUpToDate(project, currentProfile, task);
        }
	}

	/**
     * Returns paths to all provided files (including objects
     * in depending components).
     * Duplicates are removed.
	 */
	public File[] getAllProvidedFiles(Map components, String currentProfile, boolean buildPath) {
        ComponentInProfile [] resolvedComponents = getAllRequiredComponentDependencies(components, currentProfile);
        HashSet result = new HashSet();
        
        ComponentInProfile self = new ComponentInProfile(this, currentProfile, false);

        for (int i=0; i<resolvedComponents.length; i++) {
            if (resolvedComponents[i].equals(self)) {
                continue;
            }

            result.addAll(
                Arrays.asList(resolvedComponents[i].component.getAllProvidedFiles(components, resolvedComponents[i].profile, false)));
        }
        result.addAll(getProvidedFiles(currentProfile, buildPath));

        File [] files = new File [ result.size() ];
        result.toArray(files);
        return files;
	}

	private List getProvidedFiles(String currentProfile, boolean buildPath) {
        ArrayList result = new ArrayList();
        for (Iterator i = provides.iterator(); i.hasNext();)
        {
            ProvidesElement provides = (ProvidesElement) i.next();
            // skip profiles that don't match.
            if (provides.getProfile() != null && (currentProfile == null || !currentProfile.equals(provides.getProfile()))) {
                continue;
            } 
            result.addAll(provides.getProvidedFiles(buildPath));
        }
        return result;
	}

    private List getProvidedFileReferences(String currentProfile, boolean buildPath) {
        ArrayList result = new ArrayList();
        for (Iterator i = provides.iterator(); i.hasNext();)
        {
            ProvidesElement provides = (ProvidesElement) i.next();
            // skip profiles that don't match.
            if (provides.getProfile() != null && (currentProfile == null || !currentProfile.equals(provides.getProfile()))) {
                continue;
            } 
            result.addAll(provides.getProvidedFileReferences(buildPath));
        }
        return result;
    }

	public String getDescription() {
		return this.description == null ? this.name : this.description;
	}

    /**
     * Collects the meta headers of the given type for this component (dependencies not included).
     */
    public void collectMetas(List metas, String currentProfile, String type) {
        final int s = provides.size();
        for (int i = 0; i < s; i++) {
            final ProvidesElement pe = (ProvidesElement) provides.get(i);
            if (profileMatches(pe.getProfile(), currentProfile)) {
                pe.collectMetas(metas, type);            
            }
        }
    }
    
    private final boolean profileMatches(String profile, String currentProfile) {
        if (profile == null) profile = "";
        if (currentProfile == null) currentProfile = "";

        return profile.equals(currentProfile);
    }

	public FileReference[] getAllProvidedFileReferences(Map components, String currentProfile, boolean buildPath, boolean nocopy) {
        ComponentInProfile [] resolvedComponents =  
            getAllRequiredComponentDependencies(components, currentProfile, nocopy);
        HashMap result = new HashMap();
        
        ComponentInProfile self = new ComponentInProfile(this, currentProfile, false);

        for (int i=0; i<resolvedComponents.length; i++) {
            if (resolvedComponents[i].equals(self)) {
                continue;
            }

            List files = resolvedComponents[i].component.getProvidedFileReferences(resolvedComponents[i].profile, false);
            for (Iterator j = files.iterator(); j.hasNext();) {
                FileReference fr = (FileReference) j.next();
                File absf = fr.getAbsoluteFile();
                if (!result.containsKey(absf)) {
                    result.put( absf, fr);
                }
            }
        }

        List files = getProvidedFileReferences(currentProfile, buildPath);
        for (Iterator i=files.iterator(); i.hasNext();) {
            FileReference fr = (FileReference) i.next();
            File f = fr.getAbsoluteFile();
            if (!result.containsKey(f)) {
                result.put(f, fr);
            }
        }

        FileReference [] filesArray = new FileReference [ result.size() ];
        result.values().toArray(filesArray);
        return filesArray;
	}
}