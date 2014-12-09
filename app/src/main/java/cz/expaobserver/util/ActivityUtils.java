/**
 *
 */
package cz.expaobserver.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;

/**
 * Instances of class {@code ActivityHelper} represent...
 *
 * @author pechanecjr
 * @version 0.0.0000 - 17. 7. 2014
 */
public class ActivityUtils {
  // == CONSTANT CLASS ATTRIBUTES ============================================
  // == VARIABLE CLASS ATTRIBUTES ============================================
  // == STATIC INITIALIZER (CLASS CONSTRUCTOR) ===============================
  // == CONSTANT INSTANCE ATTRIBUTES =========================================
  // == VARIABLE INSTANCE ATTRIBUTES =========================================
  // == CLASS GETTERS AND SETTERS ============================================
  // == OTHER NON-PRIVATE CLASS METHODS ======================================

  /**
   * Navigate to hierarchical parent of this activity. Create task stack if
   * needed.
   *
   * @param activity Activity in question.
   * @param shouldCreateParent Standard behavior on API 16+ is that parent activity is <i>NOT</i>
   * created if it is not on task stack. Should you need to create the
   * parent activity (and replicate support behavior) provide
   * {@code true}.
   * <p/>
   * This parameter has no effect on API <16 or when this activity is
   * in different task than its parent. In this case parent activity is
   * always created.
   * <p/>
   * Note: This is probably not intended behavior, but is used i.e. in
   * <ul>
   * <li>Google Play Services settings.</li>
   * </ul>
   */
  public static void navigateUp(Activity activity, boolean shouldCreateParent) {
    Intent up = NavUtils.getParentActivityIntent(activity);

    if (NavUtils.shouldUpRecreateTask(activity, up)) {
      TaskStackBuilder.create(activity).addNextIntentWithParentStack(up).startActivities();
    } else {
      if (shouldCreateParent) {
        up.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(up);
        activity.finish();
      } else {
        NavUtils.navigateUpTo(activity, up);
      }

    }
  }

  public static void setBrightness(Context context, int brightness) {
    if (brightness < 0) {
      setBrightnessAuto(context, true);
      return;
    }

    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
        Math.min(brightness, 255));
    setBrightnessAuto(context, false);
  }

  public static void setBrightnessAuto(Context context, boolean on) {
    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
        on ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
  }

  public static int getBrightness(Context context) {
    try {
      if (Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
          == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
        return -1;
      }

      return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
    } catch (Settings.SettingNotFoundException e) {
      e.printStackTrace();
      return -2;
    }
  }

  // #########################################################################
  // == CONSTUCTORS AND FACTORY METHODS ======================================

  private ActivityUtils() {
  }

  // == ABSTRACT METHODS =====================================================
  // == INSTANCE GETTERS AND SETTERS =========================================

  // == OTHER NON-PRIVATE INSTANCE METHODS ===================================
  // == PRIVATE AND AUXILIARY CLASS METHODS ==================================
  // == PRIVATE AND AUXILIARY INSTANCE METHODS ===============================
  // == EMBEDDED TYPES AND INNER CLASSES =====================================
  // == TESTING CLASSES AND METHODS ==========================================
  //
  // /**
  // * Testing method.
  // */
  // public static void test() {
  // NewClass inst = new NewClass();
  // }
  //
  // /** @param args Command line arguments - not used. */
  // public static void main( String[] args ) { test(); }
}
