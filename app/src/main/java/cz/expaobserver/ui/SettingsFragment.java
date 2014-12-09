/**
 * 
 */
package cz.expaobserver.ui;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import cz.expaobserver.R;

/**
 * Instances of class {@code SettingsFragment} represent...
 * 
 * @author pechanecjr
 * @version 0.0.0000 - 5. 9. 2014
 */
public class SettingsFragment extends PreferenceFragment {
  // == CONSTANT CLASS ATTRIBUTES ============================================
  // == VARIABLE CLASS ATTRIBUTES ============================================
  // == STATIC INITIALIZER (CLASS CONSTRUCTOR) ===============================
  // == CONSTANT INSTANCE ATTRIBUTES =========================================
  // == VARIABLE INSTANCE ATTRIBUTES =========================================
  // == CLASS GETTERS AND SETTERS ============================================
  // == OTHER NON-PRIVATE CLASS METHODS ======================================
  // #########################################################################
  // == CONSTUCTORS AND FACTORY METHODS ======================================

  public static SettingsFragment newInstance() {
    SettingsFragment f = new SettingsFragment();
    return f;
  }
  
  public SettingsFragment() {
  }
  
  @Override
  public void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);

    addPreferencesFromResource(R.xml.preferences);
  }

  // == ABSTRACT METHODS =====================================================
  // == INSTANCE GETTERS AND SETTERS =========================================
  // == OTHER NON-PRIVATE INSTANCE METHODS ===================================
  // == PRIVATE AND AUXILIARY CLASS METHODS ==================================
  // == PRIVATE AND AUXILIARY INSTANCE METHODS ===============================
  // == EMBEDDED TYPES AND INNER CLASSES =====================================
  // == TESTING CLASSES AND METHODS ==========================================
  //
  //    /**
  //     * Testing method.
  //     */
  //    public static void test() {
  //        NewClass inst = new NewClass();
  //    }
  //
  //    /** @param args Command line arguments - not used. */
  //    public static void main( String[] args )  {  test();  }
}
