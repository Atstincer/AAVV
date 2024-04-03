package com.example.usuario.aavv;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.usuario.aavv.Ajustes.FragmentAjustes;
import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.Excursiones.FragmentExcursion;
import com.example.usuario.aavv.Excursiones.FragmentExcursiones;
import com.example.usuario.aavv.Hoteles.FragmentHoteles;
import com.example.usuario.aavv.Reservas.FragmentLiquidacion;
import com.example.usuario.aavv.Reservas.FragmentRepVenta;
import com.example.usuario.aavv.Reservas.FragmentReservar;
import com.example.usuario.aavv.Reservas.FragmentReservasSaliendoElDia;
import com.example.usuario.aavv.Reservas.FragmentVentaTTOO;
import com.example.usuario.aavv.Reservas.ReservaRVAdapter;
import com.example.usuario.aavv.TTOO.FragmentTouroperadores;
import com.example.usuario.aavv.Util.MisConstantes;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentTouroperadores.MyCallBack, FragmentReservar.MyCallBack,
        FragmentReservasSaliendoElDia.MyCallBack, FragmentAjustes.MyCallBack, FragmentLiquidacion.MyCallBack, FragmentVentaTTOO.MyCallBack,
        FragmentHoteles.MyCallBack, FragmentExcursion.MyCallBack, FragmentExcursiones.MyCallBack, ReservaRVAdapter.MyMainActivity,
        FragmentRepVenta.MyCallBack{

    public static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE = 0;

    private MisConstantes.Estado estadoFragmentReservar;
    private boolean hasStarted;
    private String lastFechaLiq, lastFechaEjec, lastDesde, lastHasta, lastFechaRepVenta;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            if(savedInstanceState.getString("estadoFragReservar")!=null && !savedInstanceState.getString("estadoFragReservar").equals("")){
                estadoFragmentReservar = MisConstantes.Estado.valueOf(savedInstanceState.getString("estadoFragReservar"));
            }
            hasStarted = savedInstanceState.getBoolean("hasStarted");
            lastFechaLiq = savedInstanceState.getString("lastFechaLiq");
            lastFechaEjec = savedInstanceState.getString("lastFechaEjec");
            lastDesde = savedInstanceState.getString("lastDesde");
            lastHasta = savedInstanceState.getString("lastHasta");
            lastFechaRepVenta = savedInstanceState.getString("lastFechaRepVenta");
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(!hasStarted) {
            if (MySharedPreferences.getFragmentInicio(getApplicationContext()) == MisConstantes.INICIAR_LIQUIDACIONES) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentLiquidacion(), FragmentLiquidacion.TAG)
                        .addToBackStack(null).commit();
            } else if (MySharedPreferences.getFragmentInicio(getApplicationContext()) == MisConstantes.INICIAR_EXCURSIONES_SALIENDO) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentReservasSaliendoElDia(), FragmentReservasSaliendoElDia.TAG)
                        .addToBackStack(null).commit();
            }
            hasStarted = true;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(estadoFragmentReservar!=null) {
            outState.putString("estadoFragReservar", estadoFragmentReservar.name());
        }
        outState.putBoolean("hasStarted",hasStarted);
        outState.putString("lastFechaLiq",lastFechaLiq);
        outState.putString("lastFechaEjec",lastFechaEjec);
        outState.putString("lastDesde",lastDesde);
        outState.putString("lastHasta",lastHasta);
        outState.putString("lastFechaRepVenta",lastFechaRepVenta);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setLastFechaRepVenta(String fecha) {
        lastFechaRepVenta = fecha;
    }

    @Override
    public String getLastFechaRepVenta() {
        return lastFechaRepVenta;
    }

    @Override
    public void setLastFechaLiq(String lastFechaLiq) {
        this.lastFechaLiq = lastFechaLiq;
    }

    @Override
    public String getLastFechaLiq() {
        return lastFechaLiq;
    }

    @Override
    public void setLastFechaEjec(String lastFechaEjec) {
        this.lastFechaEjec = lastFechaEjec;
    }

    @Override
    public String getLastFechaEjec() {
        return lastFechaEjec;
    }

    @Override
    public void setLastDesde(String lastDesde) {
        this.lastDesde = lastDesde;
    }

    @Override
    public void setLastHasta(String lastHasta) {
        this.lastHasta = lastHasta;
    }

    @Override
    public String getLastDesde() {
        return lastDesde;
    }

    @Override
    public String getLastHasta() {
        return lastHasta;
    }

    @Override
    public MisConstantes.Estado getEstadoFragmentReservar() {
        //if(estadoFragmentReservar==null){return MisConstantes.Estado.NUEVO;}
        return estadoFragmentReservar;
    }

    @Override
    public void setUpFragmentReservar(long id) {
        estadoFragmentReservar = MisConstantes.Estado.EDITAR;
        FragmentReservar fragment = new FragmentReservar();
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment,FragmentReservar.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpFragmentReservar(String lastTE, String fechaLiquidacion) {
        estadoFragmentReservar = MisConstantes.Estado.NUEVO;
        FragmentReservar fragment = new FragmentReservar();
        Bundle bundle = new Bundle();
        bundle.putString("lastTE",lastTE);
        bundle.putString("fechaLiq",fechaLiquidacion);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment,FragmentReservar.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpNewFragmentExcursion() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentExcursion(),FragmentExcursion.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpFragmentExcursion(long id) {
        FragmentExcursion fragment = new FragmentExcursion();
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment,FragmentExcursion.TAG).addToBackStack(null).commit();
    }

    @Override
    public void udUI(String tag) {
        String title = "";
        int navMenuItemId = 0;
        switch (tag){
            case FragmentTouroperadores.TAG:
                title = "Agencias";
                navMenuItemId = R.id.nav_agencias;
                break;
            case FragmentReservar.TAG:
                switch (getEstadoFragmentReservar()){
                    case NUEVO:
                        title = "Nueva Reserva";
                        break;
                    case EDITAR:
                        title = "Info reserva";
                        break;
                }
                navMenuItemId = R.id.nav_reservar;
                break;
            case FragmentReservasSaliendoElDia.TAG:
                title = "Reservas";
                navMenuItemId = R.id.nav_excursiones_dia;
                break;
            case FragmentLiquidacion.TAG:
                title = "Liquidaciones";
                navMenuItemId = R.id.nav_liquidacion;
                break;
            case FragmentRepVenta.TAG:
                title = "Reportes de venta";
                navMenuItemId = R.id.nav_repventa;
                break;
            case FragmentVentaTTOO.TAG:
                title = "Ventas por agencias";
                navMenuItemId = R.id.nav_venta_agencias;
                break;
            case FragmentHoteles.TAG:
                title = "Hoteles";
                navMenuItemId = R.id.nav_hoteles;
                break;
            case FragmentExcursiones.TAG:
                title = "Excursiones";
                navMenuItemId = R.id.nav_excursiones;
                break;
            case FragmentExcursion.TAG:
                title = "Excursion";
                navMenuItemId = R.id.nav_excursiones;
                break;
            case FragmentAjustes.TAG:
                title = "Ajustes";
                navMenuItemId = R.id.nav_ajustes;
                break;
        }
        setTitle(title);
        udNavigationView(navMenuItemId);
    }

    private void udNavigationView(int itemselected){
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setCheckedItem(itemselected);
    }

    @Override
    public void showSnackBar(String mensaje) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout,mensaje,Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        AppCompatTextView textView = (AppCompatTextView) snackBarView.getChildAt(0);
        textView.setMaxLines(5);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_reservar:
                estadoFragmentReservar = MisConstantes.Estado.NUEVO;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentReservar(),FragmentReservar.TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_liquidacion:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentLiquidacion(),FragmentLiquidacion.TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_repventa:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentRepVenta(),FragmentRepVenta.TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_excursiones_dia:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentReservasSaliendoElDia(), FragmentReservasSaliendoElDia.TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_venta_agencias:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentVentaTTOO(),FragmentVentaTTOO.TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_agencias:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentTouroperadores(), FragmentTouroperadores.TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_hoteles:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentHoteles(), FragmentHoteles.TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_excursiones:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentExcursiones(), FragmentExcursiones.TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.nav_ajustes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentAjustes(), FragmentAjustes.TAG)
                        .addToBackStack(null).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
