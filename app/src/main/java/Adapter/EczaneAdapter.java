package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import Model.EczaneDetay;
import sercandevops.com.htmlparseeczaneuygulamasi.R;

public class EczaneAdapter extends BaseAdapter {

    List<EczaneDetay> list;
    Context context;
    Activity activity;

    public EczaneAdapter(List<EczaneDetay> list, Context context,Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        TextView eczaneIsim,eczaneAdres,eczaneTel,eczaneFax,eczaneAdresTarifi;
        Button haritadaGoster,aramarYap;

        convertView = LayoutInflater.from(context).inflate(R.layout.layout,parent,false);

        eczaneIsim = convertView.findViewById(R.id.txAd);
        eczaneAdres = convertView.findViewById(R.id.txAdres);
        eczaneTel = convertView.findViewById(R.id.txTelefon);
        eczaneFax = convertView.findViewById(R.id.txFax);
        eczaneAdresTarifi = convertView.findViewById(R.id.txAdresTarihi);
        haritadaGoster = convertView.findViewById(R.id.btnharitagoster);


        eczaneIsim.setText(list.get(position).getEczaneIsmi());
        eczaneAdres.setText(list.get(position).getAdres());
        eczaneTel.setText(list.get(position).getTelefon());
        eczaneFax.setText(list.get(position).getFax());
        eczaneAdresTarifi.setText(list.get(position).getTarif());

        eczaneTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse("tel:"+list.get(position).getTelefon()));

                activity.startActivity(intent);

            }
        });


        return convertView;
    }
}
