package org.naturenet;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.DrawerActions.open;

import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.naturenet.data.model.Idea;
import org.naturenet.data.model.Observation;
import org.naturenet.ui.MainActivity;

@RunWith(AndroidJUnit4.class)
public class DesignIdeasTests {

    final static String DESIGN_IDEAS = "Design Ideas";
    final static String NEW_IDEA = "New Idea";


    @Rule
    public ActivityTestRule<MainActivity> mMainActivity = new ActivityTestRule<>(MainActivity.class, true, true);

    @Test
    public void submit_idea_test() throws InterruptedException{

        //open nav drawer
        onView(withId(R.id.drawer_layout)).perform(open());

        wait(2000);

        //select design ideas
        onView(allOf(withText(DESIGN_IDEAS), withEffectiveVisibility(VISIBLE))).perform(click());

        //click the add idea button
        onView(withId(R.id.fabAddIdea)).perform(click());

        //type an idea in
        onView(withId(R.id.design_idea_text)).perform(typeText(NEW_IDEA), closeSoftKeyboard());

        //click send button
        onView(withId(R.id.design_idea_send_button)).perform(click());

        wait(2000);

        onData(allOf(is(instanceOf(Idea.class)), withContent(NEW_IDEA))).atPosition(0);

    }






    public static Matcher withContent(final String content){
        return new BoundedMatcher<Object, Idea>(Idea.class) {
            @Override
            protected boolean matchesSafely(Idea idea) {
                return content.equals(idea.content);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with content: " + content);
            }
        };
    }
}


