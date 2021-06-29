package com.example.graduationproject.sign_language;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class ConvertIconToText implements View.OnClickListener {
    private OnPressKey onPressKey;
    private Context context;
    private ImageView a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, space, backSpace;

    public ConvertIconToText(Context context, OnPressKey onPressKey
            , ImageView a, ImageView b, ImageView c, ImageView d, ImageView e, ImageView f
            , ImageView g, ImageView h, ImageView i, ImageView j, ImageView k, ImageView l, ImageView m, ImageView n, ImageView o
            , ImageView p, ImageView q, ImageView r, ImageView s, ImageView t, ImageView u, ImageView v, ImageView w
            , ImageView x, ImageView y, ImageView z, ImageView space, ImageView backSpace) {
        this.context = context;
        this.onPressKey = onPressKey;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
        this.j = j;
        this.k = k;
        this.l = l;
        this.m = m;
        this.n = n;
        this.o = o;
        this.p = p;
        this.q = q;
        this.r = r;
        this.s = s;
        this.t = t;
        this.u = u;
        this.v = v;
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        this.space = space;
        this.backSpace = backSpace;
    }


    public void initWithClick() {
        a.setOnClickListener(this);
        b.setOnClickListener(this);
        c.setOnClickListener(this);
        d.setOnClickListener(this);
        e.setOnClickListener(this);
        f.setOnClickListener(this);
        g.setOnClickListener(this);
        h.setOnClickListener(this);
        i.setOnClickListener(this);
        j.setOnClickListener(this);
        k.setOnClickListener(this);
        l.setOnClickListener(this);
        m.setOnClickListener(this);
        n.setOnClickListener(this);
        o.setOnClickListener(this);
        p.setOnClickListener(this);
        q.setOnClickListener(this);
        r.setOnClickListener(this);
        s.setOnClickListener(this);
        t.setOnClickListener(this);
        u.setOnClickListener(this);
        v.setOnClickListener(this);
        w.setOnClickListener(this);
        x.setOnClickListener(this);
        y.setOnClickListener(this);
        z.setOnClickListener(this);
        backSpace.setOnClickListener(this);
        space.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View v) {
        onPressKey.pressKey(v.getId());
    }

    public interface OnPressKey {
        void pressKey(int id);
    }
}
