package com.example.hairsalon;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    public static Upload uploadCurrent;
    private Context mContext,pContext;
    public static Upload print;
    public static String printImage;
    public static String printPrice;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;
    public static String example;
    public static String printName;
    DatabaseReference QueryRef;

    int[] IMAGES = {R.drawable.stylish1, R.drawable.stylish2, R.drawable.stylish3, R.drawable.stylishh4};
    public static ArrayList<String> NAME = new ArrayList<>();
    public static ArrayList<String> EMAIL = new ArrayList<>();
    public ImageAdapter(Context context,Context pcontext, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
        pContext = pcontext;
    }

    public static  List<HashMap<String, String>> AList = new ArrayList<HashMap<String, String>>();


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.imgae_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        Upload uploadCurrent = mUploads.get(position);

        //Upload uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getmName());
        holder.textViewPrice.setText("₹ : " + uploadCurrent.getPrice());


     /*   Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
      */
        Picasso.get ()
                .load(uploadCurrent.getmImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        //super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_hair_style);
        public TextView textViewName,textViewPrice;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            imageView = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final HairStyle h =new HairStyle();
                    h.aList.clear();

                    print = mUploads.get(getAdapterPosition());
                    printName = print.getmName();
                    printImage = print.getmImageUrl();
                    printPrice = print.getPrice();
                  //  String Email = print.
                   // HairStyle.NAME.add(printName);
                    final int[] count1 = {0};
                    QueryRef = FirebaseDatabase.getInstance().getReference().child("HAIRSTYLIST");
                    QueryRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for( final DataSnapshot data : children) {
                                String data1= data.getKey().toString();
                                DatabaseReference Query = QueryRef.child(data1).child("Hairstyles");
                                final String City = data.child("Info").child("city").getValue(String.class);
                                if(HairStyle.count1!=0)
                                {
                                    // if(City.equals(HairStyle.Address1))
                                    {
                                        Query.orderByChild("mName").equalTo(printName).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                // Toast.makeText(pContext , "Selected Hair Style:- " + data.getKey().toString(), Toast.LENGTH_LONG).show();
                                                if(dataSnapshot!=null) {
                                                    if(count1[0]==0)
                                                    {
                                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                            if(City.equals(HairStyle.Address1))
                                                            {
                                                                String name = data.child("Info").child("name").getValue().toString();
                                                                HairStyle.NAME.add(name);
                                                                String email = data.child("Info").child("email").getValue(String.class);
                                                                HairStyle.EMAIL.add(email);
                                                                HashMap<String, String> hm = new HashMap<String, String>();
                                                                hm.put("txt", "Name : " + name);
                                                                hm.put("txt1", "Email : " + email);
                                                                hm.put("flag", Integer.toString(IMAGES[1]));
                                                                h.aList.add(hm);
                                                            }

                                                        }
                                                    }


                                                }
                                                count1[0]++;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                }
                                else {
                                    Query.orderByChild("mName").equalTo(printName).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //Toast.makeText(pContext , "Selected Hair Style:- " + data.getKey().toString(), Toast.LENGTH_LONG).show();
                                            if(dataSnapshot!=null) {
                                                if(count1[0]==0)
                                                {
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                        String name = data.child("Info").child("name").getValue().toString();
                                                        NAME.add(name);
                                                        String email = data.child("Info").child("email").getValue(String.class);
                                                        //EMAIL.add(email);
                                                        HashMap<String, String> hm = new HashMap<String, String>();
                                                        hm.put("txt", "Name : " + name);
                                                        hm.put("txt1", "Email : " + email);
                                                        hm.put("flag", Integer.toString(IMAGES[1]));
                                                        h.aList.add(hm);
                                                    }
                                                }


                                            }
                                            count1[0]++;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);

                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


}