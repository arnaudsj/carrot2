
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

package org.carrot2.core;

import org.carrot2.core.ControllerTestsBase.ComponentWithInitParameter;
import org.carrot2.util.attribute.Bindable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.google.common.collect.ImmutableMap;

/**
 * Runs matrix tests on {@link Controller} in all realistic configurations.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{
    ControllerTest.ComponentManagerIndependentTests.class,
    ControllerTest.SimpleControllerCommonTests.class,
    ControllerTest.PoolingControllerCommonTests.class,
    ControllerTest.PoolingControllerPoolingTests.class,
    ControllerTest.CachingPoolingControllerCachingOffCommonTests.class,
    ControllerTest.CachingPoolingControllerCachingOffPoolingTests.class,
    ControllerTest.CachingPoolingControllerCachingOnCommonTests.class,
    ControllerTest.CachingPoolingControllerCachingOnPoolingTests.class,
    ControllerTest.CachingPoolingControllerCachingOnCachingTests.class,
    ControllerTest.CachingControllerCachingOffCommonTests.class,
    ControllerTest.CachingControllerCachingOnCommonTests.class,
    ControllerTest.CachingControllerCachingOnCachingTests.class
})
@SuppressWarnings("unchecked")
public class ControllerTest
{
    public static class ComponentManagerIndependentTests
    {
        @Test
        public void testAutomaticInitialization()
        {
            Controller controller = null;
            try
            {
                controller = new Controller();
                controller.process(ImmutableMap.<String, Object> of(), ComponentWithInitParameter.class);
            }
            finally
            {
                controller.dispose();
            }
            // If we don't get an exception here, the test is passed
        }

        @Test(expected = IllegalStateException.class)
        public void testUsingSimpleManagerWithMoreThanOneController()
        {
            checkManagerWithMultipleControllers(new SimpleProcessingComponentManager());
        }

        @Test(expected = IllegalStateException.class)
        public void testUsingPoolingManagerWithMoreThanOneController()
        {
            checkManagerWithMultipleControllers(new PoolingProcessingComponentManager());
        }

        @Test(expected = IllegalStateException.class)
        public void testUsingCachingManagerWithMoreThanOneController()
        {
            checkManagerWithMultipleControllers(new CachingProcessingComponentManager(
                new SimpleProcessingComponentManager()));
        }

        @Test(expected = IllegalArgumentException.class)
        public void testUnknownComponentId()
        {
            processAndDispose("nonexistent-component");
        }

        @Test(expected = IllegalArgumentException.class)
        public void testUnexpectedComponentClass()
        {
            processAndDispose(Integer.class);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testUnexpectedComponentDesignatorType()
        {
            processAndDispose(42);
        }

        @Test(expected = IllegalStateException.class)
        public void testMultipleInitialization()
        {
            Controller controller = null;
            try
            {
                controller = new Controller();
                controller.init();
                controller.init();
            }
            finally
            {
                controller.dispose();
            }
        }

        @Test
        public void testMultipleDisposal()
        {
            final Controller controller = new Controller();
            controller.init();
            controller.dispose();
            controller.dispose();
        }

        private void checkManagerWithMultipleControllers(
            final IProcessingComponentManager manager)
        {
            Controller controller1 = null;
            Controller controller2 = null;
            try
            {
                controller1 = new Controller(manager);
                controller2 = new Controller(manager);
                controller1.init();
                controller2.init();
            }
            finally
            {
                controller1.dispose();
                controller2.dispose();
            }
        }

        private void processAndDispose(final Object designator)
        {
            Controller controller = null;
            try
            {
                controller = new Controller();
                controller.init();
                controller.process(ImmutableMap.<String, Object> of(), designator);
            }
            finally
            {
                controller.dispose();
            }
        }
        
    }
    
    public static class SimpleControllerCommonTests extends ControllerTestsCommon
    {
        @Override
        public Controller getSimpleController()
        {
            return ControllerFactory.createSimple();
        }
    }

    @Bindable
    public static class TestProcessingComponent1 extends ProcessingComponentBase
    {
        @Override
        public void process() throws ProcessingException
        {
            try
            {
                Thread.sleep(Long.MAX_VALUE);
            }
            catch (InterruptedException e)
            {
                // fall through.
            }
        }
    }

    public static class PoolingControllerWithFixedPoolCommonTests extends ControllerTestsCommon
    {
        private static final int EAGERLY_INITIALIZED_INSTANCES = 6;

        @Override
        public Controller getSimpleController()
        {
            return ControllerFactory.createPooling(EAGERLY_INITIALIZED_INSTANCES);
        }

        @Override
        public boolean hasPooling()
        {
            return true;
        }

        @Override
        public int eagerlyInitializedInstances()
        {
            return EAGERLY_INITIALIZED_INSTANCES;
        }
    }
    
    public static class PoolingControllerWithFixedPoolPoolingTests extends ControllerTestsPooling
    {
        private static final int EAGERLY_INITIALIZED_INSTANCES = 4;

        @Override
        public Controller getPoolingController()
        {
            return ControllerFactory.createPooling(EAGERLY_INITIALIZED_INSTANCES);
        }

        @Override
        public int eagerlyInitializedInstances()
        {
            return EAGERLY_INITIALIZED_INSTANCES;
        }
    }
    
    public static class PoolingControllerCommonTests extends ControllerTestsCommon
    {
        @Override
        public Controller getSimpleController()
        {
            return ControllerFactory.createPooling();
        }

        @Override
        public boolean hasPooling()
        {
            return true;
        }
    }

    public static class PoolingControllerPoolingTests extends ControllerTestsPooling
    {
        @Override
        public Controller getPoolingController()
        {
            return ControllerFactory.createPooling();
        }
    }

    public static class CachingPoolingControllerCachingOffCommonTests extends
        ControllerTestsCommon
    {
        @Override
        public Controller getSimpleController()
        {
            return ControllerFactory.createCachingPooling();
        }

        @Override
        public boolean hasPooling()
        {
            return true;
        }
    }

    public static class CachingPoolingControllerCachingOffPoolingTests extends
        ControllerTestsPooling
    {
        @Override
        public Controller getPoolingController()
        {
            return ControllerFactory.createCachingPooling();
        }
    }

    public static class CachingPoolingControllerCachingOnCommonTests extends
        ControllerTestsCommon
    {
        @Override
        public Controller getSimpleController()
        {
            return ControllerFactory.createCachingPooling(IProcessingComponent.class);
        }

        @Override
        public boolean hasCaching()
        {
            return true;
        }

        @Override
        public boolean hasPooling()
        {
            return true;
        }
    }

    public static class CachingPoolingControllerCachingOnPoolingTests extends
        ControllerTestsPooling
    {
        @Override
        public Controller getPoolingController()
        {
            return ControllerFactory.createCachingPooling(IProcessingComponent.class);
        }

        @Override
        public boolean hasCaching()
        {
            return true;
        }
    }

    public static class CachingPoolingControllerCachingOnCachingTests extends
        ControllerTestsCaching
    {
        @Override
        public Controller getCachingController(
            Class<? extends IProcessingComponent>... cachedComponentClasses)
        {
            return ControllerFactory.createCachingPooling(cachedComponentClasses);
        }

        @Override
        public boolean hasPooling()
        {
            return true;
        }
    }

    public static class CachingControllerCachingOffCommonTests extends
        ControllerTestsCommon
    {
        @Override
        public Controller getSimpleController()
        {
            return ControllerFactory.createCaching();
        }
    }

    public static class CachingControllerCachingOnCommonTests extends
        ControllerTestsCommon
    {
        @Override
        public Controller getSimpleController()
        {
            return ControllerFactory.createCaching(IProcessingComponent.class);
        }

        @Override
        public boolean hasCaching()
        {
            return true;
        }
    }

    public static class CachingControllerCachingOnCachingTests extends
        ControllerTestsCaching
    {
        @Override
        public Controller getCachingController(
            Class<? extends IProcessingComponent>... cachedComponentClasses)
        {
            return ControllerFactory.createCaching(cachedComponentClasses);
        }
    }
}
