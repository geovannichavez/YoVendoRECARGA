package com.globalpaysolutions.yovendosaldo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendosaldo.customs.DrawerMenuItem;

import java.util.ArrayList;

/**
 * Created by Josu� Ch�vez on 15/04/2016.
 */
public class ExpandableDrawerAdapter extends BaseExpandableListAdapter
{

    public ArrayList<DrawerMenuItem> groupItem;
    public ArrayList<String> tempChild;
    public ArrayList<Object> Childtem = new ArrayList<Object>();
    public LayoutInflater minflater;
    public Activity activity;
    private final Context context;

    public ExpandableDrawerAdapter(Context context, ArrayList<DrawerMenuItem> grList, ArrayList<Object> childItem)
    {
        this.context = context;
        groupItem = grList;
        this.Childtem = childItem;
    }

    public void setInflater(LayoutInflater mInflater, Activity act)
    {
        this.minflater = mInflater;
        activity = act;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        tempChild = (ArrayList<String>) Childtem.get(groupPosition);
        TextView text = null;
        if (convertView == null)
        {
            convertView = new TextView(context);
        }
        text = (TextView) convertView;
        text.setText(">" + tempChild.get(childPosition));
        //		convertView.setOnClickListener(new OnClickListener() {
        //			@Override
        //			public void onClick(View v) {
        //				Toast.makeText(activity, tempChild.get(childPosition),
        //						Toast.LENGTH_SHORT).show();
        //			}
        //		});
        convertView.setTag(tempChild.get(childPosition));
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition)
    {
        return ((ArrayList<String>) Childtem.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return null;
    }

    @Override
    public int getGroupCount()
    {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition)
    {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        /*if (convertView == null)
        {
            convertView = new TextView(context);
        }

        DrawerMenuItem currentItem = groupItem.get(groupPosition);

        ((TextView) convertView).setText(currentItem.getName());
        convertView.setTag(groupItem.get(groupPosition));
        return convertView;
*/


        DrawerMenuItem parentItem = groupItem.get(groupPosition);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.custom_drawer_menu_item, null);
        }

        TextView tvGrupoTitulo = (TextView) convertView.findViewById(R.id.tvDrawerTitulo);
        ImageView ivGrupoIcono = (ImageView) convertView.findViewById(R.id.ivDrawerMenuIcono);

        tvGrupoTitulo.setText(parentItem.getName());
        ivGrupoIcono.setImageResource(parentItem.getIconID());

        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }


    /*private Context _context;
    private List<DrawerMenuItem> ListaParentItemDrawer;
    private HashMap<String, List<String>> ListaChildItemDrawer;

    public ExpandableDrawerAdapter(Context context, List<DrawerMenuItem> listDataHeader, HashMap<String, List<String>> listChildData)
    {
        this._context = context;
        this.ListaParentItemDrawer = listDataHeader;
        this.ListaChildItemDrawer = listChildData;
    }

    /*//*
    /*//*
    /*//*   SETTING CHILD
    /*//*
    /*//*

    @Override
    public Object getChild(int groupPosition, int childPosititon)
    {
        return this.ListaChildItemDrawer.get(this.ListaParentItemDrawer.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.custom_drawer_menu_subitem, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.tvDrawerSubtitulo);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        int pos = this.ListaChildItemDrawer.get(this.ListaParentItemDrawer.get(groupPosition)).size();
        return pos;
    }



    /*//*
    /*//*
    /*//*   SETTING GROUP
    /*//*
    /*//*

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.ListaParentItemDrawer.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this.ListaParentItemDrawer.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        //String headerTitle = (String) getGroup(groupPosition);
        DrawerMenuItem parentItem = (DrawerMenuItem) getGroup(groupPosition);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.custom_drawer_menu_item, null);
        }

        TextView tvGrupoTitulo = (TextView) convertView.findViewById(R.id.tvDrawerTitulo);
        ImageView ivGrupoIcono = (ImageView) convertView.findViewById(R.id.ivDrawerMenuIcono);

        tvGrupoTitulo.setText(parentItem.getName());
        ivGrupoIcono.setImageResource(parentItem.getIconID());

        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }*/
}
