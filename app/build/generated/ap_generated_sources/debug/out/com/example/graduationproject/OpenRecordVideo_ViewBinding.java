// Generated code from Butter Knife. Do not modify!
package com.example.graduationproject;

import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class OpenRecordVideo_ViewBinding implements Unbinder {
  private OpenRecordVideo target;

  @UiThread
  public OpenRecordVideo_ViewBinding(OpenRecordVideo target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public OpenRecordVideo_ViewBinding(OpenRecordVideo target, View source) {
    this.target = target;

    target.surfaceView = Utils.findRequiredViewAsType(source, R.id.surface_view, "field 'surfaceView'", SurfaceView.class);
    target.stopVideoIcon = Utils.findRequiredViewAsType(source, R.id.btn_stop_video, "field 'stopVideoIcon'", ImageView.class);
    target.startVideoIcon = Utils.findRequiredViewAsType(source, R.id.btn_start_video, "field 'startVideoIcon'", ImageView.class);
    target.changeCameraIcon = Utils.findRequiredViewAsType(source, R.id.btn_change_camera, "field 'changeCameraIcon'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    OpenRecordVideo target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.surfaceView = null;
    target.stopVideoIcon = null;
    target.startVideoIcon = null;
    target.changeCameraIcon = null;
  }
}
