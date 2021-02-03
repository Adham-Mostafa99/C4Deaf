// Generated code from Butter Knife. Do not modify!
package com.example.graduationproject.ui;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.graduationproject.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChatMenuActivity_ViewBinding implements Unbinder {
  private ChatMenuActivity target;

  @UiThread
  public ChatMenuActivity_ViewBinding(ChatMenuActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ChatMenuActivity_ViewBinding(ChatMenuActivity target, View source) {
    this.target = target;

    target.menu = Utils.findRequiredViewAsType(source, R.id.menu, "field 'menu'", ImageView.class);
    target.searchButton = Utils.findRequiredViewAsType(source, R.id.search_button, "field 'searchButton'", ImageView.class);
    target.newChatButton = Utils.findRequiredViewAsType(source, R.id.new_chat_button, "field 'newChatButton'", ImageView.class);
    target.recyclerViewChat = Utils.findRequiredViewAsType(source, R.id.recycler_view_chat, "field 'recyclerViewChat'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ChatMenuActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.menu = null;
    target.searchButton = null;
    target.newChatButton = null;
    target.recyclerViewChat = null;
  }
}
