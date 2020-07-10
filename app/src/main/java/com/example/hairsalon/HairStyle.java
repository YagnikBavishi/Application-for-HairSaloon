package com.example.hairsalon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.hairsalon.R.menu.mymenu;


public class HairStyle extends AppCompatActivity implements LocationListener, ImageAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    ImageView imageView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    Toolbar toolbar;
    public static String name,Address1;
    int[] IMAGES = {R.drawable.stylish1, R.drawable.stylish2, R.drawable.stylish3, R.drawable.stylishh4};

    private LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;

    public static ArrayList<String> NAME = new ArrayList<>();
    public static ArrayList<String> EMAIL = new ArrayList<>();
    public static ArrayList<String> ADDRESS=new ArrayList<>();
    Location currentLocation;
    DatabaseReference QueryRef = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST");
    public static SimpleAdapter adapter;

    private String provider;
    RadioButton selected = null;
    private int call = 0;
    public static int count1 = 0;


    public  static  List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
    List<HashMap<String, String>> aList2 = new ArrayList<HashMap<String, String>>();


    private FirebaseRecyclerOptions<ColorSpace.Model> optioms;
    private Object firebaseRecyclerAdapter;
    String[] from = {"flag", "txt", "txt1","txt2","cur"};
    int[] to = {R.id.imageView2, R.id.textView_name, R.id.textview_discription};
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_style);


        toolbar = findViewById(R.id.header);
        imageView = findViewById(R.id.imageView2);
        listView = findViewById(R.id.listview);

        toolbar.inflateMenu(mymenu);
        toolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.settings) {
                    Intent intent = new Intent(HairStyle.this, Setting.class);
                    startActivity(intent);
                }
                return false;

            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria;
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            Toast.makeText(this, "Provider " + provider + " has been selected.", Toast.LENGTH_SHORT).show();
            if(call==0)
            {
                onLocationChanged(location);
                call++;
            }

        }

    }

    public void onLocationChanged(Location location) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        if (call == 0) {
            try {

                List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
                Address1 = address.get(0).getLocality();
                Address1 = Address1.toLowerCase();
                //Success.setText(Address1);

                final String finalAddress = Address1;
                final int[] count = {0};
                QueryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for(DataSnapshot data : children){
                            String address = data.child("Info").child("city").getValue(String.class);
                            if(address!=null && address.equals(finalAddress)){
                                count[0] = 1;

                              //  String name = data.child("Info").child("name").getValue(String.class);
                              ///  NAME.add(name);
                                //String email=data.child("Info").child("email").getValue(String.class);
                                //EMAIL.add(email);
                                //ADDRESS.add(address);
                                /*String name5 = data.child("Info").getValue(String.class);

                                Picasso.get()
                                        .load(name5)
                                        .placeholder(R.mipmap.ic_launcher)
                                        .fit()
                                        .centerCrop()
                                        .into(imageView);*/
                                //HashMap<String, String> hm = new HashMap<String, String>();
                                //hm.put("txt", "Name : " + name);
                               // hm.put("txt1","Email :" +email);
                              //  hm.put("txt2","Address :" +address);
                              //  hm.put("flag", Integer.toString(IMAGES[1]));
                                // aList.add(hm);
                                mRecyclerView = findViewById(R.id.recycler_view);
                                mRecyclerView.setHasFixedSize(true);

                                mRecyclerView.setLayoutManager(new LinearLayoutManager(HairStyle.this));

                                mProgressCircle = findViewById(R.id.progress_circle);

                                mUploads = new ArrayList<>();

                                mDatabaseRef = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST").child(data.getKey()).child("Hairstyles");
                                mDatabaseRef.addValueEventListener(new ValueEventListener() {


                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            Upload upload = postSnapshot.getValue(Upload.class);
                                            mUploads.add(upload);
                                            count1++;
                                        }

                                        mAdapter = new ImageAdapter(HairStyle.this, HairStyle.this, mUploads);
                                        // mRadio = new RadioButtonsAdapter();

                                        mRecyclerView.setAdapter(mAdapter);
                                        // mRecyclerView.setAdapter(mRadio);
                                        mProgressCircle.setVisibility(View.INVISIBLE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(HairStyle.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        mProgressCircle.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }

                        }

                        if(count[0] != 1){

                            QueryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot shot : dataSnapshot.getChildren()) {
                                        //name = shot.child("Info").child("name").getValue(String.class);
                                        //String Address = shot.child("Info").child("address").getValue(String.class);
                                        //String email=shot.child("Info").child("email").getValue(String.class);
                                        //String address = shot.child("Info").child("city").getValue(String.class);
                                        //HashMap<String, String> hm = new HashMap<String, String>();
                                        //NAME.add(name);
                                        //EMAIL.add(email);
                                        //ADDRESS.add(address);

                                        //hm.put("txt", "Name : " + name);
                                        //hm.put("txt1", "Email : " + email);
                                      //  hm.put("txt2", "Address : " + address);
                                        // hm.put("txt", "Address : " + Address);
                                        //DISCRIPTION.add(Address);
                                      //  hm.put("flag", Integer.toString(IMAGES[1]));

                                        //  aList.add(hm);

                                        mRecyclerView = findViewById(R.id.recycler_view);
                                        mRecyclerView.setHasFixedSize(true);
                                        mRecyclerView.setLayoutManager(new LinearLayoutManager(HairStyle.this));

                                        mProgressCircle = findViewById(R.id.progress_circle);

                                        mUploads = new ArrayList<>();

                                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("HAIRSTYLIST").child(shot.getKey()).child("Hairstyles");
                                        mDatabaseRef.addValueEventListener(new ValueEventListener() {


                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    Upload upload = postSnapshot.getValue(Upload.class);
                                                    mUploads.add(upload);
                                                }

                                                mAdapter = new ImageAdapter(HairStyle.this, HairStyle.this, mUploads);
                                                // Toast.makeText(HairStyle.this , "hello" + ImageAdapter.position , Toast.LENGTH_LONG).show();

                                                // mRadio = new RadioButtonsAdapter();

                                                mRecyclerView.setAdapter(mAdapter);
                                                // mRecyclerView.setAdapter(mRadio);
                                                mProgressCircle.setVisibility(View.INVISIBLE);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(HairStyle.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                mProgressCircle.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            } catch (NullPointerException e) {
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                call++;
            }
        }




        adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.customlayout, from, to);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1 = new Intent(getApplicationContext(), StylishReview.class);
                // intent.putExtra("name", NAME[i]);
                //intent.putExtra("image", IMAGES[i]);
                intent1.putExtra("email", EMAIL.get(i));
                intent1.putExtra("name", NAME.get(i));
               // intent1.putExtra("address",ADDRESS.get(i));
                intent1.putExtra("image", IMAGES[1]);
                intent1.putExtra("printSname" , ImageAdapter.printName);
                intent1.putExtra("printSimage" , ImageAdapter.printImage);
                intent1.putExtra("printSprice" , ImageAdapter.printPrice);
                startActivity(intent1);
            }
        });
    }
    /* public void listViewFun()
     {

         Toast.makeText(this,"Har Har MAhadev",Toast.LENGTH_SHORT).show();
        // SimpleAdapter adapter = new SimpleAdapter(this, ImageAdapter.AList, R.layout.customlayout, from, to);
         //listView.setAdapter(adapter);
     }*/
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(locationManager.GPS_PROVIDER.toString()!=null)
        {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 400, 1, this);
        }
        // locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 400, 1, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);


    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                }
            }
        });
    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }


}












