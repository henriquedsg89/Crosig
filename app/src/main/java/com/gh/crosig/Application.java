package com.gh.crosig;

import com.gh.crosig.model.Comment;
import com.gh.crosig.model.Problem;
import com.gh.crosig.model.ProblemFollow;
import com.gh.crosig.model.SuggestedStatus;
import com.gh.crosig.model.UserNotification;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * Created by henrique on 21/05/15.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Problem.class);
        ParseObject.registerSubclass(SuggestedStatus.class);
        ParseObject.registerSubclass(UserNotification.class);
        ParseObject.registerSubclass(ProblemFollow.class);
        ParseObject.registerSubclass(Comment.class);

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true); //objects created are writable
        ParseACL.setDefaultACL(defaultACL, true);

        Parse.initialize(this, "k4C4iDQPonsBtWzZeOyzsxQrYpfn7ODBilu5v2XC", "80AjPLWpKuTZs0oI4A8Tb9wOXuWPzzHWoCS40ZGd");
        ParseFacebookUtils.initialize(getApplicationContext());
    }
}
