package com.omgrentbbq.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.resources.MainBundle;
import com.omgrentbbq.shared.model.Pair;
import com.omgrentbbq.shared.model.User;
import com.omgrentbbq.shared.model.UserSession;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OmgRentBbq implements EntryPoint {
    /**
     * The message displayed to the session when the server cannot be reached or
     * returns an error.
     */
    @SuppressWarnings("unused")
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    private DockPanel panel = new DockPanel();
    private VerticalPanel authPanel;


    static String GDATA_API_KEY;
    private Anchor authAnchor;

    public void onModuleLoad() {
        String host = Window.Location.getHost();
        String proto = Window.Location.getProtocol();
        boolean localHost = host.startsWith("127.0.0.1");
        boolean secure = proto.startsWith("https");
        GDATA_API_KEY = secure ?
                localHost ?
                        "ABQIAAAAWpB08GH6KmKITXI7rtGRpBREGtQZq9OFJfHndXhPP8gxXzlLARRs1Zat3MllIUzN5hpmsbfnyEF7wA" :
                        "ABQIAAAAWpB08GH6KmKITXI7rtGRpBQP9W7Y7I5qr-k1KpACLx2-LL8VZRSAmDzEx8058dg-LbfPzLfgD1bPqQ" :
                "ABQIAAAAWpB08GH6KmKITXI7rtGRpBSZ0_RId71_G7aCA6qntwd15T_WaBRjfmPbE7W4RF2InR8N8OZxXPGNTQ";
        panel.add(new Image(MainBundle.INSTANCE.logo()), DockPanel.WEST);
        RootPanel rootPanel = RootPanel.get("main");
        rootPanel.add(panel);
        LoginAsync lm = GWT.create(Login.class);

        lm.getUserSession(Window.Location.getHref(), new AsyncCallback<Pair<UserSession, String>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Pair<UserSession, String> userSessionURLPair) {

                UserSession userSession = userSessionURLPair.getFirst();
                panel.add(new Label(userSession.toString()), DockPanel.CENTER);
                authAnchor = new Anchor();
                panel.add(authAnchor, DockPanel.EAST);
                String url = userSessionURLPair.getSecond();
                authAnchor.setHref(url);
                User user = (User) userSession.properties.get("user");
                if (null != user && userSession.properties.containsKey("userLoggedIn") && Boolean.valueOf(userSession.properties.get("userLoggedIn").toString()))
                    authAnchor.setText("not " + user.properties.get("nickname") + "? Sign Out");
                else
                    authAnchor.setText("Sign in using your google account now");
            }
        });
    }
}

