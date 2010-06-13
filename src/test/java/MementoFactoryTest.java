import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.omgrentbbq.server.MementoFactory;
import com.omgrentbbq.server.spi.LoginSpi;
import com.omgrentbbq.shared.model.*;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.io.Serializable;
import java.util.Map;

import static com.omgrentbbq.server.MementoFactory.update;

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

        final Serializable serializable = memento.$$();
        final long aLong = LONG;
        assertEquals(serializable, aLong);
        assertEquals(memento.$.get("x"), "x");
        assertEquals(memento.$.size(), 1);

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
        assertNull(memento.$("theSpecialId"));
        assertFalse(memento.$("x").equals("x"));
        assertEquals(memento.$("x"), ("!x"));
        assertEquals(memento.$.size(), 1);

    }

    public void testInvitationResponse() {
        final User from = makeFromUser();

        final Group group = makeTestGroup();

        makeTestMembership(from, group);


        final User to = new User();
        String toEmail = "jim@example.com";
        to.setUserId(System.currentTimeMillis());
        MementoFactory.update(to);

        final LoginSpi loginSpi = new LoginSpi();
        loginSpi.assignMembership(to, from, group); 
    }

    private void makeTestMembership(User from, Group group) {
        final Membership membership = new Membership(from, group);
        update(membership);
    }

    private Group makeTestGroup() {
        final Group group = new Group();
        group.setName(""+System.currentTimeMillis());
        MementoFactory.update(group);
        return group;
    }

    private User makeFromUser() {
        final User from = new User();
        from.setUserId(System.nanoTime());
        from.setEmail("jimn235@site1.com");
        update(from);
        return from;
    }
}
