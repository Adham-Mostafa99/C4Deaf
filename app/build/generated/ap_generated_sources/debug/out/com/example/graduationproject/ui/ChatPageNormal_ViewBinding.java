// Generated code from Butter Knife. Do not modify!
package com.example.graduationproject.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.graduationproject.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChatPageNormal_ViewBinding implements Unbinder {
  private ChatPageNormal target;

  @UiThread
  public ChatPageNormal_ViewBinding(ChatPageNormal target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ChatPageNormal_ViewBinding(ChatPageNormal target, View source) {
    this.target = target;

    target.chatView = Utils.findRequiredViewAsType(source, R.id.chat_view, "field 'chatView'", RelativeLayout.class);
    target.recyclerViewChat = Utils.findRequiredViewAsType(source, R.id.recycler_view_chat, "field 'recyclerViewChat'", RecyclerView.class);
    target.btnEmoji = Utils.findRequiredViewAsType(source, R.id.btn_emoji, "field 'btnEmoji'", ImageView.class);
    target.btnSend = Utils.findRequiredViewAsType(source, R.id.btn_send, "field 'btnSend'", ImageView.class);
    target.normalRecordButton = Utils.findRequiredViewAsType(source, R.id.normal_record_button, "field 'normalRecordButton'", RecordButton.class);
    target.normalRecordView = Utils.findRequiredViewAsType(source, R.id.normal_record_view, "field 'normalRecordView'", RecordView.class);
    target.editTextSend = Utils.findRequiredViewAsType(source, R.id.edit_text_send, "field 'editTextSend'", LinearLayout.class);
    target.textSend = Utils.findRequiredViewAsType(source, R.id.text_send, "field 'textSend'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ChatPageNormal target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.chatView = null;
    target.recyclerViewChat = null;
    target.btnEmoji = null;
    target.btnSend = null;
    target.normalRecordButton = null;
    target.normalRecordView = null;
    target.editTextSend = null;
    target.textSend = null;
  }
}
