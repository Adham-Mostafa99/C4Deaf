// Generated code from Butter Knife. Do not modify!
package com.example.graduationproject.ui;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.graduationproject.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SignInActivity_ViewBinding implements Unbinder {
  private SignInActivity target;

  @UiThread
  public SignInActivity_ViewBinding(SignInActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SignInActivity_ViewBinding(SignInActivity target, View source) {
    this.target = target;

    target.profileImage = Utils.findRequiredViewAsType(source, R.id.sign_up_profile_image, "field 'profileImage'", CircleImageView.class);
    target.bottomBackground = Utils.findRequiredViewAsType(source, R.id.bottom_background, "field 'bottomBackground'", LinearLayout.class);
    target.signIn = Utils.findRequiredViewAsType(source, R.id.sign_in_button, "field 'signIn'", Button.class);
    target.signInEmail = Utils.findRequiredViewAsType(source, R.id.sign_in_email, "field 'signInEmail'", EditText.class);
    target.signInPassword = Utils.findRequiredViewAsType(source, R.id.sign_in_password, "field 'signInPassword'", EditText.class);
    target.signInForgotPass = Utils.findRequiredViewAsType(source, R.id.sign_in_forgot_pass, "field 'signInForgotPass'", TextView.class);
    target.signInByGoogleButton = Utils.findRequiredViewAsType(source, R.id.sign_in_by_google_button, "field 'signInByGoogleButton'", Button.class);
    target.signInByFbButton = Utils.findRequiredViewAsType(source, R.id.sign_in_by_fb_button, "field 'signInByFbButton'", Button.class);
    target.signInCreateAccount = Utils.findRequiredViewAsType(source, R.id.sign_in_create_account, "field 'signInCreateAccount'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SignInActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.profileImage = null;
    target.bottomBackground = null;
    target.signIn = null;
    target.signInEmail = null;
    target.signInPassword = null;
    target.signInForgotPass = null;
    target.signInByGoogleButton = null;
    target.signInByFbButton = null;
    target.signInCreateAccount = null;
  }
}
