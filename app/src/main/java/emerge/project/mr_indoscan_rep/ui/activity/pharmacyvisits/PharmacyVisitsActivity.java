package emerge.project.mr_indoscan_rep.ui.activity.pharmacyvisits;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import emerge.project.mr_indoscan_rep.BuildConfig;
import emerge.project.mr_indoscan_rep.R;
import emerge.project.mr_indoscan_rep.services.api.ApiClient;
import emerge.project.mr_indoscan_rep.services.api.ApiInterface;
import emerge.project.mr_indoscan_rep.services.network.NetworkAvailability;
import emerge.project.mr_indoscan_rep.ui.activity.doctors.DoctorsActivity;
import emerge.project.mr_indoscan_rep.ui.activity.expences.ExpensesActivity;
import emerge.project.mr_indoscan_rep.ui.activity.locations.LocationActivity;
import emerge.project.mr_indoscan_rep.ui.activity.mileage.MileageActivity;
import emerge.project.mr_indoscan_rep.ui.activity.products.ProductsUnavailabilityActivity;
import emerge.project.mr_indoscan_rep.ui.activity.visits.VisitsActivity;
import emerge.project.mr_indoscan_rep.ui.adapters.navigation.NavigationAdapter;
import emerge.project.mr_indoscan_rep.ui.adapters.pharmacy.PharmacyDoctorAdapter;
import emerge.project.mr_indoscan_rep.ui.adapters.pharmacy.ProductAdapter;
import emerge.project.mr_indoscan_rep.utils.entittes.Doctor;
import emerge.project.mr_indoscan_rep.utils.entittes.Navigation;
import emerge.project.mr_indoscan_rep.utils.entittes.Products;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PharmacyVisitsActivity extends Activity {


    @BindView(R.id.drawer_layout)
    DrawerLayout dLayout;

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationView bottomNavigationBar;

    @BindView(R.id.listview_navigation)
    ListView listViewNavigation;


    @BindView(R.id.recyclerview_product)
    RecyclerView recyclerviewProduct;


    @BindView(R.id.recyclerview_doc)
    RecyclerView recyclerviewDoc;


    NavigationAdapter navigationAdapter;
    public static String tokenID;
    EncryptedPreferences encryptedPreferences;
    private static final String USER_ID = "userID";

    ApiInterface apiService;


    ArrayList<Products> productsArrayList = new ArrayList<Products>();
    ArrayList<Doctor> docArrayList = new ArrayList<Doctor>();

    ArrayList<Doctor> doctorList= new ArrayList<Doctor>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_visits);

        ButterKnife.bind(this);


        bottomNavigationBar.setSelectedItemId(R.id.navigation_visits);
        bottomNavigationBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        ArrayList<Navigation> navigationItems = new ArrayList<Navigation>();
        navigationItems.add(new Navigation("Expenses", R.drawable.ic_product_defult_small));
        navigationItems.add(new Navigation("Mileage", R.drawable.ic_product_defult_small));
        navigationItems.add(new Navigation("Pharmacy Visits", R.drawable.ic_product_defult_small));
        navigationItems.add(new Navigation("Products", R.drawable.ic_product_defult_small));


        tokenID = BuildConfig.API_TOKEN_ID;
        apiService = ApiClient.getClient().create(ApiInterface.class);

        encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword("122547895511").build();


        navigationAdapter = new NavigationAdapter(this, navigationItems);
        listViewNavigation.setAdapter(navigationAdapter);

        listViewNavigation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(PharmacyVisitsActivity.this, ExpensesActivity.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(PharmacyVisitsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                    startActivity(intent, bndlanimation);
                    finish();

                } else if (position == 1) {
                    Intent intent = new Intent(PharmacyVisitsActivity.this, MileageActivity.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(PharmacyVisitsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                    startActivity(intent, bndlanimation);
                    finish();
                } else if (position == 2) {

                }else if(position==3){
                    Intent intent = new Intent(PharmacyVisitsActivity.this, ProductsUnavailabilityActivity.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(PharmacyVisitsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                    startActivity(intent, bndlanimation);
                    finish();
                }
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerviewProduct.setLayoutManager(layoutManager);
        recyclerviewProduct.setItemAnimator(new DefaultItemAnimator());
        recyclerviewProduct.setNestedScrollingEnabled(false);


        LinearLayoutManager layoutManagerDoc = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerviewDoc.setLayoutManager(layoutManagerDoc);
        recyclerviewDoc.setItemAnimator(new DefaultItemAnimator());
        recyclerviewDoc.setNestedScrollingEnabled(false);

        getProducts();
        getDoc();
    }

    private void getProducts() {
        if (!NetworkAvailability.isNetworkAvailable(this)) {
            Toast.makeText(this, "No Internet Access, Please try again", Toast.LENGTH_SHORT).show();
        } else {
            try {
                int userId = encryptedPreferences.getInt(USER_ID, 0);
                apiService.getAllProductsForMR(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ArrayList<Products>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(ArrayList<Products> pro) {
                                productsArrayList = pro;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                setProducts(productsArrayList);

                            }
                        });
            } catch (Exception ex) {
                Toast.makeText(this, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void setProducts(ArrayList<Products> pro) {
        ProductAdapter productAdapter = new ProductAdapter(this, pro);
        recyclerviewProduct.setAdapter(productAdapter);
    }
    private void getDoc() {
        if (!NetworkAvailability.isNetworkAvailable(this)) {
            Toast.makeText(this, "No Internet Access, Please try again", Toast.LENGTH_SHORT).show();
        } else {
            try {
                int userId = encryptedPreferences.getInt(USER_ID, 0);
                apiService.getAllDoctors(tokenID, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ArrayList<Doctor>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(ArrayList<Doctor> doc) {
                                docArrayList = doc;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                setDoc(docArrayList);

                            }
                        });
            } catch (Exception ex) {
                Toast.makeText(this, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void setDoc(ArrayList<Doctor> doc) {
        if(doc.isEmpty()){
            doctorList.add(new Doctor(1, "Test","Test", "0796607483", "Test", "Test", "Test",
                    1, "Test", "Test", true, false
                    , "Test", "Test", null, null,"true",false));

            doctorList.add(new Doctor(1, "Test","Test", "0796607483", "Test", "Test", "Test",
                    1, "Test", "Test", true, false
                    , "Test", "Test", null, null,"true",false));

            doctorList.add(new Doctor(1, "Test","Test", "0796607483", "Test", "Test", "Test",
                    1, "Test", "Test", true, false
                    , "Test", "Test", null, null,"true",false));

            doc = doctorList;

        }else {

        }



        PharmacyDoctorAdapter pharmacyDoctorAdapter = new PharmacyDoctorAdapter(this,doc);
        recyclerviewDoc.setAdapter(pharmacyDoctorAdapter);
    }


    @OnClick(R.id.imageView2)
    public void onClickSliderMenue(View view) {
        dLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit!");
        alertDialogBuilder.setMessage("Do you really want to exit ?");
        alertDialogBuilder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        alertDialogBuilder.show();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_visits:
                    Intent intent = new Intent(PharmacyVisitsActivity.this, VisitsActivity.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(PharmacyVisitsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                    startActivity(intent, bndlanimation);
                    finish();

                    return true;
                case R.id.navigation_location:
                    Intent intent2 = new Intent(PharmacyVisitsActivity.this, LocationActivity.class);
                    Bundle bndlanimation2 = ActivityOptions.makeCustomAnimation(PharmacyVisitsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                    startActivity(intent2, bndlanimation2);
                    finish();

                    return true;
                case R.id.navigation_doctors:
                    Intent intentDoc = new Intent(PharmacyVisitsActivity.this, DoctorsActivity.class);
                    Bundle bndlanimationDoc = ActivityOptions.makeCustomAnimation(PharmacyVisitsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                    startActivity(intentDoc, bndlanimationDoc);
                    finish();
                    return true;

            }
            return false;
        }
    };

}