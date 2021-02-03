// Generated code from Butter Knife. Do not modify!
package com.example.graduationproject.ui;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.graduationproject.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SignUpActivity_ViewBinding implements Unbinder {
  private SignUpActivity target;

  @UiThread
  public SignUpActivity_ViewBinding(SignUpActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SignUpActivity_ViewBinding(SignUpActivity target, View source) {
    this.target = target;

    target.signUpProfileImage = Utils.findRequiredViewAsType(source, R.id.sign_up_profile_image, "field 'signUpProfileImage'", CircleImageView.class);
    target.signUpUploadImage = Utils.findRequiredViewAsType(source, R.id.sign_up_upload_image, "field 'signUpUploadImage'", ImageView.class);
    target.signUpEditFirstName = Utils.findRequiredViewAsType(source, R.id.sign_up_edit_first_name, "field 'signUpEditFirstName'", EditText.class);
    target.signUpEditLastName = Utils.findRequiredViewAsType(source, R.id.sign_up_edit_last_name, "field 'signUpEditLastName'", EditText.class);
    target.signUpEditEmail = Utils.findRequiredViewAsType(source, R.id.sign_up_edit_email, "field 'signUpEditEmail'", EditText.class);
    target.signUpEditPass = Utils.findRequiredViewAsType(source, R.id.sign_up_edit_pass, "field 'signUpEditPass'", EditText.class);
    target.signUpCountryCode = Utils.findRequiredViewAsType(source, R.id.sign_up_country_code, "field 'signUpCountryCode'", CountryCodePicker.class);
    target.signUpEditPhone = Utils.findRequiredViewAsType(source, R.id.sign_up_edit_phone, "field 'signUpEditPhone'", EditText.class);
    target.signUpSpinnerGender = Utils.findRequiredViewAsType(source, R.id.sign_up_spinner_select_gender, "field 'signUpSpinnerGender'", Spinner.class);
    target.signUpSpinnerState = Utils.findRequiredViewAsType(source, R.id.sign_up_spinner_state, "field 'signUpSpinnerState'", Spinner.class);
    target.signUpCheckAcceptedRules = Utils.findRequiredViewAsType(source, R.id.sign_up_check_accepted_rules, "field 'signUpCheckAcceptedRules'", CheckBox.class);
    target.signUpButton = Utils.findRequiredViewAsType(source, R.id.sign_up_button, "field 'signUpButton'", Button.class);
    target.signUpDateOfBirthDay = Utils.findRequiredViewAsType(source, R.id.sign_up_date_birth_day, "field 'signUpDateOfBirthDay'", Button.class);
    target.errorNoDate = Utils.findRequiredViewAsType(source, R.id.error_no_date, "field 'errorNoDate'", TextView.class);
    target.signUpHaveAccount = Utils.findRequiredViewAsType(source, R.id.sign_up_have_account, "field 'signUpHaveAccount'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SignUpActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.signUpProfileImage = null;
    target.signUpUploadImage = null;
    target.signUpEditFirstName = null;
    target.signUpEditLastName = null;
    target.signUpEditEmail = null;
    target.signUpEditPass = null;
    target.signUpCountryCode = null;
    target.signUpEditPhone = null;
    target.signUpSpinnerGender = null;
    target.signUpSpinnerState = null;
    target.signUpCheckAcceptedRules = null;
    target.signUpButton = null;
    target.signUpDateOfBirthDay = null;
    target.errorNoDate = null;
    target.signUpHaveAccount = null;
  }
}
