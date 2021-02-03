// Generated code from Butter Knife. Do not modify!
package com.example.graduationproject.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.graduationproject.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChatPageDeaf_ViewBinding implements Unbinder {
  private ChatPageDeaf target;

  @UiThread
  public ChatPageDeaf_ViewBinding(ChatPageDeaf target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ChatPageDeaf_ViewBinding(ChatPageDeaf target, View source) {
    this.target = target;

    target.recyclerViewChatDeaf = Utils.findRequiredViewAsType(source, R.id.recycler_view_chat_deaf, "field 'recyclerViewChatDeaf'", RecyclerView.class);
    target.btnEmoji = Utils.findRequiredViewAsType(source, R.id.btn_emoji, "field 'btnEmoji'", ImageView.class);
    target.textSend = Utils.findRequiredViewAsType(source, R.id.text_send, "field 'textSend'", EditText.class);
    target.btnSend = Utils.findRequiredViewAsType(source, R.id.btn_send, "field 'btnSend'", ImageView.class);
    target.deafRecord = Utils.findRequiredViewAsType(source, R.id.deaf_record, "field 'deafRecord'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ChatPageDeaf target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerViewChatDeaf = null;
    target.btnEmoji = null;
    target.textSend = null;
    target.btnSend = null;
    target.deafRecord = null;
  }
}
