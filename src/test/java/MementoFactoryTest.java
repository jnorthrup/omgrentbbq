import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.omgrentbbq.server.MementoFactory;
import com.omgrentbbq.shared.model.KeyProperty;
import com.omgrentbbq.shared.model.Memento;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.io.Serializable;
import java.util.Map;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 5, 2010
 * Time: 11:20:37 AM
 */
public class MementoFactoryTest extends TestCase {


    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private static final long LONG = 890123901823l;

    public static class MockUser {
        private String x = "x";

        private long theSpecialId = LONG;

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public long getTheSpecialId() {
            return theSpecialId;
        }

        public void setTheSpecialId(long theSpecialId) {
            this.theSpecialId = theSpecialId;
        }
    }

    @KeyProperty("theSpecialId")
    public static class MockUserMemento extends Memento {

    }

    public void testAddMementoByClass() {

        final MockUserMemento memento = MementoFactory.writeMemento(new MockUser(), MockUserMemento.class);

        final Serializable serializable = memento.getKey();
        final long aLong = LONG;
        assertEquals(serializable, aLong);
        assertEquals(memento.properties.get("x"), "x");
        assertEquals(memento.properties.size(), 1);

    }


    public void testCreateMap() {
        final Map<String, Serializable> map = MementoFactory.createMap(new MockUser());

        assertEquals(map.get("x"), "x");
        assertEquals(map.get("theSpecialId"), LONG);
        assertEquals(map.size(), 2);
    }

    public void testCreateMemento() {
        final Entity entity = new Entity("MockuserMemento");
        entity.setProperty("x", "!x");
        final Memento memento = MementoFactory.$(entity, MockUserMemento.class);
        assertNull(memento.properties.get("theSpecialId"));
        assertFalse(memento.properties.get("x").equals("x"));
        assertEquals(memento.properties.get("x"),("!x"));
        assertEquals(memento.properties.size(), 1);

    }
}
