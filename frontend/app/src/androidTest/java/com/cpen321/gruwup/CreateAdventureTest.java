package com.cpen321.gruwup;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.CheckResult;
import androidx.test.espresso.AmbiguousViewMatcherException;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateAdventureTest {

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.INTERNET",
                    "android.permission.ACCESS_WIFI_STATE");

    @Rule
    public ActivityScenarioRule<LogInActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LogInActivity.class);


    @CheckResult
    public static boolean exists(ViewInteraction interaction) {
        try {
            interaction.perform(new ViewAction() {
                @Override public Matcher<View> getConstraints() {
                    return any(View.class);
                }
                @Override public String getDescription() {
                    return "check for existence";
                }
                @Override
                public void perform(UiController uiController, View view) {
                    // no op, if this is run, then the execution will continue after .perform(...)
                }
            });
            return true;
        } catch (AmbiguousViewMatcherException ex) {
            // if there's any interaction later with the same matcher, that'll fail anyway
            return true; // we found more than one
        } catch (NoMatchingViewException ex) {
            return false;
        } catch (NoMatchingRootException ex) {
            // optional depending on what you think "exists" means
            return false;
        }
    }

    @Before
    public void stubCameraIntent() {
        // Initializes Intents and begins recording intents.
        Intents.init();

        Instrumentation.ActivityResult result = createImageCaptureActivityResultStub();

        // Stub the Intent.
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);
    }

    private Instrumentation.ActivityResult createImageCaptureActivityResultStub() {
        System.out.println("createImageCaptureActivityResultStub");
        Bundle bundle = new Bundle();
        bundle.putParcelable(String.valueOf(DiscoverFragment.TEST_IMAGE_KEY), BitmapFactory.decodeResource(
                InstrumentationRegistry.getInstrumentation().getTargetContext().getResources(),
                R.drawable.college_student));

        // Create the Intent that will include the bundle.
        Intent resultData = new Intent();
        resultData.putExtras(bundle);

        // Create the ActivityResult with the Intent.
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }

    @Test
    public void createAdventureTestSuccess() {

        ViewInteraction hj = onView(
                allOf(withText("Sign in"),
                        childAtPosition(
                                allOf(withId(R.id.sign_in_button),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                0),
                        isDisplayed()));

        if (exists(hj)){
            hj.perform(click());
        }

        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.create_adventure), withText(" +"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                1),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.create_adventure_title_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText.perform(scrollTo(), replaceText("t"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.create_adventure_description_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                5)));
        appCompatEditText2.perform(scrollTo(), replaceText("d"), closeSoftKeyboard());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.categoryName), withText("MOVIE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.create_adventure_upload_image_button), withText("Upload Image"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                9)));
        materialButton.perform(scrollTo(), click());

        //----------------------------------------------------------------------------------------------------------------------

        ViewInteraction timeButton = onView(
                allOf(withId(R.id.create_adventure_time_input), withText("Choose A Time"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                12)));
        timeButton.perform(scrollTo(), click());

        ViewInteraction timeDialogYear = onView(
                allOf(withClassName(is("com.google.android.material.textview.MaterialTextView")), withText("2022"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        timeDialogYear.perform(click());

        DataInteraction timeDialogYear2 = onData(anything())
                .inAdapterView(allOf(withClassName(is("android.widget.YearPickerView")),
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.DialogViewAnimator")),
                                1)))
                .atPosition(123);
        timeDialogYear2.perform(scrollTo(), click());

        ViewInteraction timeDialogOK = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        timeDialogOK.perform(scrollTo(), click());

        ViewInteraction timeDialogOK2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        timeDialogOK2.perform(scrollTo(), click());
        //----------------------------------------------------------------------------------------------------------------------

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.create_adventure_location_input)));
        appCompatEditText4.perform(scrollTo(), replaceText("2110 Burrard St, Vancouver, BC V6J 3H6"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.confirmButton), withText("Create Adventure"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                17)));
        materialButton2.perform(scrollTo(), click());
        SystemClock.sleep(3500);
        onView(withText("Choose and Create Adventures!")).check(matches(isDisplayed()));
    }

    @Test
    public void createAdventureTestFailNoImage() {

        ViewInteraction hj = onView(
                allOf(withText("Sign in"),
                        childAtPosition(
                                allOf(withId(R.id.sign_in_button),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                0),
                        isDisplayed()));

        if (exists(hj)){
            hj.perform(click());
        }

        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.create_adventure), withText(" +"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                1),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.create_adventure_title_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText.perform(scrollTo(), replaceText("t"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.create_adventure_description_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                5)));
        appCompatEditText2.perform(scrollTo(), replaceText("d"), closeSoftKeyboard());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.categoryName), withText("MOVIE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        //no image!

        ViewInteraction timeButton = onView(
                allOf(withId(R.id.create_adventure_time_input), withText("Choose A Time"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                12)));
        timeButton.perform(scrollTo(), click());

        ViewInteraction timeDialogYear = onView(
                allOf(withClassName(is("com.google.android.material.textview.MaterialTextView")), withText("2022"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        timeDialogYear.perform(click());

        DataInteraction timeDialogYear2 = onData(anything())
                .inAdapterView(allOf(withClassName(is("android.widget.YearPickerView")),
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.DialogViewAnimator")),
                                1)))
                .atPosition(123);
        timeDialogYear2.perform(scrollTo(), click());

        ViewInteraction timeDialogOK = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        timeDialogOK.perform(scrollTo(), click());

        ViewInteraction timeDialogOK2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        timeDialogOK2.perform(scrollTo(), click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.create_adventure_location_input)));
        appCompatEditText4.perform(scrollTo(), replaceText("2110 Burrard St, Vancouver, BC V6J 3H6"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.confirmButton), withText("Create Adventure"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                17)));
        materialButton2.perform(scrollTo(), click());
        onView(allOf(withId(R.id.create_adventure_location_input))).check(matches(isDisplayed()));
    }

    @Test
    public void createAdventureTestFailNoTime() {

        ViewInteraction hj = onView(
                allOf(withText("Sign in"),
                        childAtPosition(
                                allOf(withId(R.id.sign_in_button),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                0),
                        isDisplayed()));

        if (exists(hj)){
            hj.perform(click());
        }

        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.create_adventure), withText(" +"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                1),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.create_adventure_title_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText.perform(scrollTo(), replaceText("t"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.create_adventure_description_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                5)));
        appCompatEditText2.perform(scrollTo(), replaceText("d"), closeSoftKeyboard());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.categoryName), withText("MOVIE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.create_adventure_upload_image_button), withText("Upload Image"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                9)));
        materialButton.perform(scrollTo(), click());

        //no time!

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.create_adventure_location_input)));
        appCompatEditText4.perform(scrollTo(), replaceText("2110 Burrard St, Vancouver, BC V6J 3H6"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.confirmButton), withText("Create Adventure"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                17)));
        materialButton2.perform(scrollTo(), click());
        onView(allOf(withId(R.id.create_adventure_location_input))).check(matches(isDisplayed()));
    }

    @Test
    public void createAdventureTestCancelled() {

        ViewInteraction hj = onView(
                allOf(withText("Sign in"),
                        childAtPosition(
                                allOf(withId(R.id.sign_in_button),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                0),
                        isDisplayed()));

        if (exists(hj)){
            hj.perform(click());
        }

        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.create_adventure), withText(" +"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                1),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.create_adventure_title_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText.perform(scrollTo(), replaceText("t"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.create_adventure_description_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                5)));
        appCompatEditText2.perform(scrollTo(), replaceText("d"), closeSoftKeyboard());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.categoryName), withText("MOVIE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.create_adventure_upload_image_button), withText("Upload Image"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                9)));
        materialButton.perform(scrollTo(), click());

        ViewInteraction timeButton = onView(
                allOf(withId(R.id.create_adventure_time_input), withText("Choose A Time"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                12)));
        timeButton.perform(scrollTo(), click());

        ViewInteraction timeDialogYear = onView(
                allOf(withClassName(is("com.google.android.material.textview.MaterialTextView")), withText("2022"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        timeDialogYear.perform(click());

        DataInteraction timeDialogYear2 = onData(anything())
                .inAdapterView(allOf(withClassName(is("android.widget.YearPickerView")),
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.DialogViewAnimator")),
                                1)))
                .atPosition(123);
        timeDialogYear2.perform(scrollTo(), click());

        ViewInteraction timeDialogOK = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        timeDialogOK.perform(scrollTo(), click());

        ViewInteraction timeDialogOK2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        timeDialogOK2.perform(scrollTo(), click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.create_adventure_location_input)));
        appCompatEditText4.perform(scrollTo(), replaceText("2110 Burrard St, Vancouver, BC V6J 3H6"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.create_adventure_go_back)));
        materialButton2.perform(scrollTo(), click());

        onView(withText("Choose and Create Adventures!")).check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @After
    public void tearDown() {
        // Clears Intents state.
        Intents.release();
    }
}
