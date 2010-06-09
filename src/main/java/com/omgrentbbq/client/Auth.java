package com.omgrentbbq.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.omgrentbbq.shared.model.Pair;
import com.omgrentbbq.shared.model.User;
import com.omgrentbbq.shared.model.UserSession;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Auth implements EntryPoint {
    /**
     * The message displayed to the session when the server cannot be reached or
     * returns an error.
     */
    @SuppressWarnings("unused")
    public static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    VerticalPanel panel = new VerticalPanel();

    Anchor authAnchor;

    public LoginAsync lm = GWT.create(Login.class);
    public User user;

    public void onModuleLoad() {
        RootPanel rootPanel = RootPanel.get("auth");
        rootPanel.add(panel);

        lm.getUserSession(Window.Location.getHref(), new AsyncCallback<Pair<UserSession, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                authAnchor.setText(SERVER_ERROR);
            }

            @Override
            public void onSuccess(Pair<UserSession, String> userSessionURLPair) {

                final UserSession userSession = userSessionURLPair.getFirst();
                authAnchor = new Anchor();
                panel.add(authAnchor);
                String url = userSessionURLPair.getSecond();
                authAnchor.setHref(url);
                user = (User) userSession.$("user");

                if (null != user && Boolean.valueOf(String.valueOf(userSession.$("userLoggedIn")))) {
                    authAnchor.setText("not " + user.$("nickname") + "? Sign Out");

                } else {
                    authAnchor.setText("Sign in using your google account now");
                }
            }
        });
    }

}