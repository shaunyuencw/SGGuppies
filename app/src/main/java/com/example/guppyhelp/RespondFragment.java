package com.example.guppyhelp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RespondFragment extends Fragment {
    ListView zongweilist;
    String[] items = {"Placeholder for ADdress","desc"};
    private ArrayList<String> data = new ArrayList<String>();
    EditText comment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_respond, container, false);
        //comment = (EditText) rootView.findViewById(R.id.editTextTextPersonName2);
        Resources res = getResources();
        zongweilist = (ListView) rootView.findViewById(R.id.SOSList2);

        //TODO The data here is dummy data, to be updated with SQL
        for(int i =0; i<3;i++){
                data.add("Placeholder for address and desc, this will be quite " +"\n" +"a long line of text cuz address n desc is long");
        }
        mylistadapter my = new mylistadapter(getActivity(),R.layout.test,data);

        //zongweilist.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.zongwei_listview_detail,items));
        zongweilist.setAdapter(my);

        //tx1.setText("bob2");
        return rootView;
    }

    private class mylistadapter extends ArrayAdapter<String>{
        private int layout;
        public mylistadapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder main = null;
            if(convertView == null){
               LayoutInflater inflate = LayoutInflater.from(getContext());
               convertView = inflate.inflate(layout,parent,false);
               ViewHolder vh =new ViewHolder();
               vh.button = (Button) convertView.findViewById(R.id.button2);
               vh.desc = (TextView) convertView.findViewById(R.id.testview);
               vh.desc.setText(getItem(position).toString());
               vh.button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Toast.makeText(getContext(),"working" + position,Toast.LENGTH_SHORT).show();
                   }
               });
               convertView.setTag(vh);
            }else{
                main = (ViewHolder) convertView.getTag();
                main.desc.setText(getItem(position));
            }

            return convertView;
        }

    }
    public class ViewHolder{
        TextView desc;
        Button button;

    }


}