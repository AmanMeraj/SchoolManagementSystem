package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.school.schoolmanagement.Admin.ActivityAdminDashboard;
import com.school.schoolmanagement.Admin.Model.NavItem;
import com.school.schoolmanagement.R;

import java.util.List;
import java.util.Map;

public class CustomNavigationAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<NavItem> navItems;
    private Map<NavItem, List<String>> navSubItems;
    private int selectedGroup = -1;
    private int selectedChild = -1;

    public CustomNavigationAdapter(Context context, List<NavItem> navItems,
                                   Map<NavItem, List<String>> navSubItems) {
        this.context = context;
        this.navItems = navItems;
        this.navSubItems = navSubItems;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.nav_item, parent, false);
        }

        NavItem item = navItems.get(groupPosition);

        ImageView icon = convertView.findViewById(R.id.nav_item_icon);
        TextView title = convertView.findViewById(R.id.nav_item_title);
        TextView indicator = convertView.findViewById(R.id.nav_item_indicator);
        View selectionIndicator = convertView.findViewById(R.id.selection_indicator);

        icon.setImageResource(item.getIconRes());
        title.setText(item.getTitle());

        if (navSubItems.containsKey(item)) {
            indicator.setVisibility(View.VISIBLE);
            indicator.setText(isExpanded ? "-" : "+");
        } else {
            indicator.setVisibility(View.GONE);
        }

        selectionIndicator.setVisibility(
                selectedGroup == groupPosition && selectedChild == -1 ?
                        View.VISIBLE : View.INVISIBLE
        );

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.nav_subitem, parent, false);
        }

        TextView title = convertView.findViewById(R.id.nav_subitem_title);
        title.setText(navSubItems.get(navItems.get(groupPosition)).get(childPosition));

        convertView.setBackground(
                selectedGroup == groupPosition && selectedChild == childPosition ?
                        ContextCompat.getDrawable(context, R.drawable.selected_nav_item) :
                        null
        );

        return convertView;
    }

    // Other required overrides...
    @Override
    public int getGroupCount() {
        return navItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        NavItem item = navItems.get(groupPosition);
        return navSubItems.containsKey(item) ? navSubItems.get(item).size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return navItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return navSubItems.get(navItems.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setSelectedItem(int groupPosition, int childPosition) {
        this.selectedGroup = groupPosition;
        this.selectedChild = childPosition;
        notifyDataSetChanged();
    }
}
