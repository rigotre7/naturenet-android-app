package org.naturenet;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import junit.framework.AssertionFailedError;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.naturenet.data.model.Observation;
import org.naturenet.data.model.Project;
import org.naturenet.ui.MainActivity;
import org.naturenet.ui.ObservationAdapter;


import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public class DrawerNavigationTest {

    static String COMMUNITIES = "Communities";
    static String PROJECTS = "Projects";
    static String DESIGN_IDEAS = "Design Ideas";
    static String EXPLORE = "Explore";
    static String NATURENET = "NatureNet";

    @Rule
    public ActivityTestRule<MainActivity> mMainActivity = new ActivityTestRule<>(MainActivity.class, true, true);

    @Test
    public void navigation_test() throws InterruptedException {


        //open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(open());

        //click on design ideas link
        onView(allOf(withText(DESIGN_IDEAS), withEffectiveVisibility(VISIBLE))).perform(click());

        //make sure we are on the correct screen
        onView(withId(R.id.app_bar_main_tv)).check(matches(withText(DESIGN_IDEAS)));

        //open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(open());

        //click on communities link
        onView(withText(COMMUNITIES)).perform(click());

        //make sure we're on the correct screen
        onView(withId(R.id.app_bar_main_tv)).check(matches(withText(COMMUNITIES)));

        //open the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(open());

        //click on projects link
        onView(withText(PROJECTS)).perform(click());

        //make sure we are indeed in the projects screen
        onView(withId(R.id.app_bar_main_tv)).check(matches(withText(PROJECTS)));

        //open navigation drawer
        onView(withId(R.id.drawer_layout)).perform(open());

        //click on Explore link
        onView(withText(EXPLORE)).perform(click());

        //let map load
        Thread.sleep(2000);

        //make sure we're on the correct screen
        onView(withId(R.id.app_bar_main_tv)).check(matches(withText(NATURENET)));

    }

    @Test
    public void view_project_test() throws InterruptedException {

        //open navigation drawer
        onView(withId(R.id.drawer_layout)).perform(open());

        //click on projects link
        onView(withText(PROJECTS)).perform(click());

        Thread.sleep(2000);

        String[] sites = new String[4];
        sites[0] = "Aspen";
        sites[1] = "Anacostia";
        sites[2] = "Elsewhere";
        sites[3] = "Reedy Creek";
        int index = 0;

        while(true){

            //click through all the projects
            try{
                //if no exception is thrown, we know there is a 'show more' button on the screen
                //Log.d("gpj3", "Going to click show more button: " + index);
                onView(withIndex(withText("Load More"), 0)).perform(click());

                Thread.sleep(1000);
            }catch (NoMatchingViewException e){
                index++;
                if(index == sites.length)
                    break;


                try{
                    //check to see if there are two views with the same site text displayed
                    //if so, no exception will be thrown and we will know the user's affiliation is the same as the site we're trying to click
                    onView(withIndex(withText(sites[index]), 1)).check(matches(isDisplayed()));
                    //therefore, we click the second one which should be the one in the ExpandableListView
                    onView(withIndex(withText(sites[index]), 1)).perform(click());
                }catch (NoMatchingViewException ex){
                    //if an exception is thrown, we know there's only one view with the text we're trying to click
                    onView(withText(sites[index])).perform(click());
                }

                Thread.sleep(1000);
            }

        }


    }


    /*
    CUSTOM MATCHERS
     */

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    public static BoundedMatcher<Object, Observation> withName (final String name) {
        return new BoundedMatcher<Object, Observation>(Observation.class) {
            @Override
            protected boolean matchesSafely(Observation obs) {
                    return name.equals(obs.where);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with id: " + name);
            }
        };
    }

    public static BoundedMatcher<Object, Observation> withSiteId (final String name){
        return new BoundedMatcher<Object, Observation>(Observation.class){
            @Override
            protected boolean matchesSafely(Observation item) {
                return name.equals(item.siteId);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    public static Matcher whereObservation (final String name){
        return new BoundedMatcher<Object, Observation>(Observation.class){
            @Override
            protected boolean matchesSafely(Observation item) {
                return name.equals(item.where);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }





    }
