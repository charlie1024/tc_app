package com.timestamp.calculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CheckBoxListLayoutAdapter extends BaseAdapter {

    private ArrayList<ListLayoutItem> lvil = new ArrayList<ListLayoutItem> ();
    public CheckBoxListLayoutAdapter() {}

    @Override
    public int getCount() {
        return lvil.size();
    }

    @Override
    public Object getItem(int i) {
        return lvil.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ckblayout, viewGroup, false);
        }

        TextView textTextView = (TextView) view.findViewById(R.id.itemText);
        ListLayoutItem listViewItem = lvil.get(i);
        textTextView.setText(listViewItem.getText());

        return view;
    }

    public void insert(String text) {
        ListLayoutItem item = new ListLayoutItem();

        item.setText(text);
        lvil.add(item);
    }

    public void insert(String text, int n) {
        ListLayoutItem item = new ListLayoutItem();

        item.setText(text);
        lvil.add(n, item);
    }

    public void insertItems(ArrayList<String> array) {
        int k, size = array.size();

        for (k=0; k<size; k++) {
            ListLayoutItem item = new ListLayoutItem();
            item.setText(array.get(k));
            lvil.add(item);
        }
    }

    public void remove(int pos) {
        lvil.remove(pos);
    }

    public void remove(String text) {
        int k;
        for(k = 0; k < lvil.size(); k++)
            if (lvil.get(k).getText().equals(text)) lvil.remove(k);
    }

    public void clear() {
        lvil.clear();
    }

    public String getItemString(int i) {
        return lvil.get(i).getText();
    }

}
