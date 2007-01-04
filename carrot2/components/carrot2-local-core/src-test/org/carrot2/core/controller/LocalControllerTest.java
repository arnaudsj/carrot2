
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

package org.carrot2.core.controller;

import java.util.*;

import org.carrot2.core.*;


/**
 * Local controller tests.
 */
public class LocalControllerTest extends junit.framework.TestCase {

    public LocalControllerTest(String s) {
        super(s);
    }

    public void testCorrectComponentFactoryAddition() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();

        StubInputComponentFactory factory = new StubInputComponentFactory();
        controller.addLocalComponentFactory("key", factory);
        assertTrue(controller.isComponentFactoryAvailable("key"));

        LocalComponent instance = null;

        if ((instance = controller.borrowComponent("key")) != null) {
            controller.returnComponent("key", instance);
        }

        assertNotNull(instance);
    }

    public void testDuplicatedKeyInComponentFactoryAddition()
        throws Exception {
        LocalControllerBase controller = new LocalControllerBase();

        StubInputComponentFactory factory = new StubInputComponentFactory();
        controller.addLocalComponentFactory("key", factory);

        try {
            controller.addLocalComponentFactory("key", factory);
            fail("Should have failed.");
        } catch (DuplicatedKeyException e) {
            // this is expected behavior.
        }
    }

    public void testContextPassedOnInitialize() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();

        StubInputComponentFactory factory = new StubInputComponentFactory();
        controller.addLocalComponentFactory("key", factory);

        List l = factory.getCreatedInstances();

        for (int i = 0; i < l.size(); i++) {
            StubInputComponent component = (StubInputComponent) l.get(i);
            assertNotNull(component.getLocalControllerContext());
        }
    }

    public void testProcessAddition() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();

        controller.addLocalComponentFactory("input",
            new StubInputComponentFactory());
        controller.addLocalComponentFactory("filter",
            new StubFilterComponentFactory());
        controller.addLocalComponentFactory("output",
            new StubOutputComponentFactory());

        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter");
        process.addFilter("filter");

        controller.addProcess("process", process);
    }

    public void testProcessAdditionAndQuerying() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();

        controller.addLocalComponentFactory("input",
            new StubInputComponentFactory());
        controller.addLocalComponentFactory("filter1",
            new StubFilterComponentFactory("f1"));
        controller.addLocalComponentFactory("filter2",
            new StubFilterComponentFactory("f2"));
        controller.addLocalComponentFactory("output",
            new StubOutputComponentFactory());

        final LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter1");
        process.addFilter("filter2");

        controller.addProcess("process", process);

        ProcessingResult result = controller.query("process", "query",
                java.util.Collections.EMPTY_MAP);

        // check the expected output: every component
        // simply adds a single letter and a comma.
        System.out.println(result.toString());

        assertEquals("i:begin,f1:begin,f2:begin,o:begin,i:end,f1:end,f2:end,o:end,",
            result.getQueryResult().toString());
    }

    public void testVerifierIncompatibleComponents() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();

        Set a = new HashSet();
        Set b = new HashSet();
        a.add("capabilityA");
        b.add("capabilityB");

        controller.addLocalComponentFactory("input",
            new StubInputComponentFactory());
        controller.addLocalComponentFactory("filter1",
            new StubFilterComponentFactory("f1", a, Collections.EMPTY_SET, b));
        controller.addLocalComponentFactory("filter2",
            new StubFilterComponentFactory("f2", Collections.EMPTY_SET, a,
                Collections.EMPTY_SET));
        controller.addLocalComponentFactory("output",
            new StubOutputComponentFactory());

        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter1");
        process.addFilter("filter2");

        try {
            controller.addProcess("process", process);
            fail("Components incompatibility not detected.");
        } catch (Exception e) {
            // ok, this is expected behavior.
        }
    }

    public void testVerifierCompatibleComponents() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();

        Set a = new HashSet();
        Set b = new HashSet();
        a.add("capabilityA");
        b.add("capabilityB");

        controller.addLocalComponentFactory("input",
            new StubInputComponentFactory());
        controller.addLocalComponentFactory("filter1",
            new StubFilterComponentFactory("f1", a, Collections.EMPTY_SET, b));
        controller.addLocalComponentFactory("filter2",
            new StubFilterComponentFactory("f2", b, a, Collections.EMPTY_SET));
        controller.addLocalComponentFactory("output",
            new StubOutputComponentFactory());

        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter1");
        process.addFilter("filter2");

        controller.addProcess("process", process);
    }

    /*
     */
    public void testSetNextInvocationContract()
        throws DuplicatedKeyException, Exception {
        LocalControllerBase controller = new LocalControllerBase();

        Set a = new HashSet();
        Set b = new HashSet();
        a.add("capabilityA");
        b.add("capabilityB");

        controller.addLocalComponentFactory("input",
            new StubInputComponentFactory());
        controller.addLocalComponentFactory("filter1",
            new StubFilterComponentFactory("f1", a, Collections.EMPTY_SET, b));
        controller.addLocalComponentFactory("filter2",
            new StubFilterComponentFactory("f2", b, a, Collections.EMPTY_SET));
        controller.addLocalComponentFactory("output",
            new StubOutputComponentFactory());

        final boolean[] states = new boolean[2];

        LocalProcessBase process = new LocalProcessBase() {
                protected void afterProcessingStartedHook(
                    LocalComponent[] components) {
                    try {
                        ((LocalInputComponent) components[0]).setNext(components[1]);
                    } catch (IllegalStateException e) {
                        // component should have returned illegal state here.
                        states[0] = true;
                    }

                    try {
                        ((LocalFilterComponent) components[1]).setNext(components[2]);
                    } catch (IllegalStateException e) {
                        // component should have returned illegal state here.
                        states[1] = true;
                    }
                }
            };

        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter1");
        process.addFilter("filter2");

        controller.addProcess("process", process);

        assertFalse("Stub Input component contract on setNext() not respected.",
            states[0]);
        assertFalse("Stub Filter component contract on setNext() not respected.",
            states[1]);
    }
}
