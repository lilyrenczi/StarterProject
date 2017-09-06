package org.czi.kb.service_example.example;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.czi.rato.api.cluster.service.MetaServiceContext;
import org.junit.Test;

/**
 * Unit testing template.
 * Created by amradawi on 2016-11-12.
 */
public class ServiceExampleTest {
    private static final ExampleComponent exampleComponent = mock(ExampleComponent.class);
    private static final ServiceExample serviceExample = new ServiceExample(exampleComponent);

    @Test
    public void test() throws InterruptedException {

        // mock mock beans
        when(exampleComponent.getInfo()).thenReturn("actually im a mock!");

        // mock service context
        final MetaServiceContext metaServiceContext = mock(MetaServiceContext.class);

        // run service with mock context
        serviceExample.doService(metaServiceContext);

        // example test verification
        assertEquals(serviceExample.getClass(), ServiceExample.class);
    }

}
