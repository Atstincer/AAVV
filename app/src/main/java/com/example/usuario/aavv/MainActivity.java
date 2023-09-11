package com.example.usuario.aavv;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.usuario.aavv.Ajustes.FragmentAjustes;
import com.example.usuario.aavv.Reservas.FragmentLiquidacion;
import com.example.usuario.aavv.Reservas.FragmentReservar;
import com.example.usuario.aavv.Reservas.FragmentReservasSaliendoElDia;
import com.example.usuario.aavv.Reservas.FragmentVentaTTOO;
import com.example.usuario.aavv.TTOO.FragmentTouroperadores;
import com.example.usuario.aavv.Util.MisConstantes;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentTouroperadores.MyCallBack, FragmentReservar.MyCallBack,
        FragmentReservasSaliendoElDia.MyCallBack, FragmentAjustes.MyCallBack, FragmentLiquidacion.MyCallBack, FragmentVentaTTOO.MyCallBack {

    public static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE = 0;

    private MisConstantes.Estado estadoFragmentReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    }

    @Override
    public MisConstantes.Estado getEstado() {
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
    public void udUI(String tag) {
        String title = "";
        switch (tag){
            case FragmentTouroperadores.TAG:
                title = "Agencias";
                break;
            case FragmentReservar.TAG:
                switch (estadoFragmentReservar){
                    case NUEVO:
                        title = "Nueva Reserva";
                        break;
                    case EDITAR:
                        title = "Info reserva";
                        break;
                }
                break;
            case FragmentReservasSaliendoElDia.TAG:
                title = "Reservas";
                break;
            case FragmentLiquidacion.TAG:
                title = "Liquidaciones";
                break;
            case FragmentVentaTTOO.TAG:
                title = "Ventas por agencias";
                break;
            case FragmentAjustes.TAG:
                title = "Ajustes";
                break;
        }
        setTitle(title);
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
