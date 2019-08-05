package com.ideyahaiti.reidha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends ArrayAdapter<Census> implements Filterable {

    Context context;
    List<Census> arrayList;
    ArrayList<Census> arrayPam;

    public CustomAdapter(Context context,List<Census> list) {
        super(context, R.layout.custom_listview,list);

        this.context = context;
        this.arrayList = list;
        this.arrayPam = new ArrayList<>();
        this.arrayPam.addAll(list);
    }


    @Override
    public View getView(int position, View convertView,ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.custom_listview,parent,false);

        TextView name = convertView.findViewById(R.id.cl_name);
        name.setText(arrayList.get(position).getLastname()+" "+arrayList.get(position).getFirstname());

        TextView cinornif = convertView.findViewById(R.id.cl_cinornif);
        if(arrayList.get(position).getNif().contentEquals("0")){
            //if nif is empty
            cinornif.setText(arrayList.get(position).getCin());
        }else{
            cinornif.setText(arrayList.get(position).getNif());
        }
        return convertView;
    }

    public void filter(String chartext)
    {
        chartext = chartext.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if(chartext.length()==0)
        {
            arrayList.addAll(arrayPam);
        }
        else
        {
            for (Census customModelList : arrayPam)
            {
                if (customModelList.getLastname().toLowerCase(Locale.getDefault()).contains(chartext))
                {
                    arrayList.add(customModelList);
                }
                if (customModelList.getFirstname().toLowerCase(Locale.getDefault()).contains(chartext))
                {
                    arrayList.add(customModelList);
                }
                /*if (customModelList.getCin().toLowerCase(Locale.getDefault()).contains(chartext))
                {
                    arrayList.add(customModelList);
                }
                if (customModelList.getNif().toLowerCase(Locale.getDefault()).contains(chartext))
                {
                    arrayList.add(customModelList);
                }*/
            }
        }
        notifyDataSetChanged();
    }

}
