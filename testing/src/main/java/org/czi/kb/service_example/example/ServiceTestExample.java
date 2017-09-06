package org.czi.kb.service_example.example;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.czi.rato.api.data.DataRepository;
import org.czi.rato.api.data.Datastreamer;
import org.czi.rato.api.data.SequenceGenerator;
import org.czi.rato.api.data.Session;
import org.czi.rato.api.data.SessionFactory;
import org.czi.rato.test.data.Person;
import org.czi.rato.testing.service.After;
import org.czi.rato.testing.service.Before;
import org.czi.rato.testing.service.ServiceTest;
import org.czi.rato.testing.service.Test;
import org.czi.rato.testing.service.TestEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by alice on 20/01/17.
 * A ServiceTest example. The methods run in the following order:
 * first @Before method, all @Test methods, finally @After methods.
 * "SERVICE_TEST" profile needs to be specified under META_PROFILES for service tests to run
 * Rato nodes collect all classes with @ServiceTest annotation
 */
@ServiceTest(serviceClass = ServiceExample.class, environment = TestEnvironment.SILVER)
public class ServiceTestExample {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTestExample.class);
    private static final Set<Person> personSet = new HashSet<>();
    private Long personid = 1L;

    // Anything defined in the class will be injected/autowired
    @Autowired
    private SessionFactory factory;
    @Autowired
    private SequenceGenerator generator;
    @Autowired
    private DataRepository<Long, Person> personRepository;


    /**
     * Before method is supposed to do all the set-up and generate data needed for the tests.
     */
    @Before
    public void setup() {

        try (Session session = factory.getOrCreateSession()) {

            logger.info("Setting up tests");

            try (Datastreamer personDataStreamer = session.getStreamer()) {

                final Person person = session.newInstance(Person.class);

                personid = generator.nextLong(person.getClass());
                person.setId(personid);
                person.setName("Random Person");
                person.setGender(Person.Gender.MALE);
                person.setEmail("Random@meta.com");
                person.setAge(88);

                personSet.add(person);
                personDataStreamer.add(person);
                personDataStreamer.flush();
                personDataStreamer.close(false);

            }

        } catch (final Exception exception) {
            logger.error("Exception caught when generating person data", exception);
        }

    }

    /**
     * Clean up everything.
     */
    @After
    public void teardown() {
        logger.info("Cleaning up data generated in this service test example");
        final Iterator personItr = personRepository.iterator();
        while (personItr.hasNext()) {
            personItr.next();
            personItr.remove();
        }
        logger.info("There are {} person records on the cluster after cleaning up", personRepository.size());
    }

    /**
     * Do the actual testing in these @Test methods. Each of the @Test methods runs independently in an arbitrary order.
     * ServiceTestRunner LOCKS across the cluster so only ONE test can run at a time.
     */
    @Test
    public void testName() {

        logger.info("testName running");
        logger.info("The name of the person is {}", personRepository.get(personid).getName());
        assertEquals("Name", personSet.iterator().next().getName(), personRepository.get(personid).getName());

    }

    @Test
    public void testAge() {
        logger.info("testAge running");
        logger.info("The age of the person is {}", personRepository.get(personid).getAge());
        assertEquals("Age", personSet.iterator().next().getAge(), personRepository.get(personid).getAge());
    }

    @Test
    public void testGender() {
        logger.info("testGender running");
        logger.info("The gender of the person is {}", personRepository.get(personid).getGender());
        assertEquals("Gender", personSet.iterator().next().getGender().toString(),
                personRepository.get(personid).getGender().toString());
    }

    @Test
    public void testEmail() {
        logger.info("testEmail running");
        logger.info("The email of the person is {}", personRepository.get(personid).getEmail());
        assertEquals("Email", personSet.iterator().next().getEmail(), personRepository.get(personid).getEmail());
    }

}
