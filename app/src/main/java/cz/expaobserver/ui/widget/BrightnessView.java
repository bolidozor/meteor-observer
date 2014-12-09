package cz.expaobserver.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.view.CollapsibleActionView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.expaobserver.R;

/**
 * Created by pechanecjr on 28. 11. 2014.
 */
public class BrightnessView extends LinearLayout implements CollapsibleActionView {

  @InjectView(R.id.seekbar)
  SeekBar mSeekBar;
  @InjectView(R.id.button)
  ImageButton mButton;

  public BrightnessView(Context context) {
    this(context, null);
  }

  public BrightnessView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0, 0);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public BrightnessView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public BrightnessView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    inflate(context, R.layout.brightness_settings, this);

    ButterKnife.inject(this);
  }

  @Override
  public void onActionViewExpanded() {
    mSeekBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onActionViewCollapsed() {
    mSeekBar.setVisibility(View.GONE);
  }
}
