package com.example.usuario.aavv.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;


/**
 * Created by usuario on 3/12/2022.
 */

public class MyEmail {

    private String [] para;
    private String asunto,cuerpo;


    public MyEmail(String[] para, String asunto, String cuerpo) {
        this.para = para;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
    }

    public static void setUpEmail(Context ctx, MyEmail email){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL,email.getPara());
        intent.putExtra(Intent.EXTRA_SUBJECT, email.getAsunto());
        intent.putExtra(Intent.EXTRA_TEXT, email.getCuerpo());
        //intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:"));
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            ctx.startActivity(intent);
        } else {
            Toast.makeText(ctx, "No hay aplicación que soporte esta acción", Toast.LENGTH_SHORT).show();
        }
    }

    private String [] getPara() {
        return para;
    }

    private String getAsunto() {
        return asunto;
    }

    private String getCuerpo() {
        return cuerpo;
    }

}
