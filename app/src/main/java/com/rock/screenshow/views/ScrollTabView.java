package com.rock.screenshow.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rock.screenshow.R;


public class ScrollTabView extends HorizontalScrollView {

    private RecyclerView recyclerView;
    private LinearLayout tabContainer;
    private int totalItemCount = 400;
    private int itemsPerPage = 20;
    private int color = Color.WHITE;
    private int initialOffset = 0;

    public ScrollTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollTabView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setHorizontalScrollBarEnabled(false);
        tabContainer = new LinearLayout(context);
        tabContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        addView(tabContainer);
    }

    public void setupWithRecyclerView(@NonNull RecyclerView recyclerView, int totalItems) {
        this.recyclerView = recyclerView;
        this.totalItemCount = totalItems;

        populateTabs(getContext());
    }

    private void populateTabs(Context context) {
        tabContainer.removeAllViews();
        int tabCount = (int) Math.ceil((double) totalItemCount / itemsPerPage);

        for (int i = 0; i < tabCount; i++) {
            int start = i * itemsPerPage + 1;
            int end = Math.min((i + 1) * itemsPerPage, totalItemCount);
            String rangeText = (start + initialOffset) + " - " + (initialOffset + end);

            Button tabButton = createTabButton(context, rangeText, i);
            tabContainer.addView(tabButton);
        }
    }

    private Button createTabButton(Context context, String rangeText, int tabIndex) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);
        button.setText(rangeText);
        button.setTextColor(color);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setGravity(Gravity.CENTER);
        button.setPadding(20,25,20,20);

        button.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                button.setBackgroundResource(R.drawable.selected);
                button.setTextColor(Color.BLACK);
            } else {
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setTextColor(Color.WHITE);
            }
        });

        button.setOnClickListener(v -> {
            int scrollToPosition = tabIndex * itemsPerPage;
            if (recyclerView != null) {
                recyclerView.scrollToPosition(scrollToPosition);
                recyclerView.postDelayed(()->{
                    recyclerView.getLayoutManager().findViewByPosition(scrollToPosition).requestFocus();
                },100);
            }
        });

        return button;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setInitialOffset(int initialOffset) {
        this.initialOffset = initialOffset;
    }

    public int getInitialOffset() {
        return initialOffset;
    }
}
