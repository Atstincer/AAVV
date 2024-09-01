package com.example.usuario.aavv.Util;

/**
 * Created by usuario on 30/07/2023.
 */

public class MisConstantes {

    public static int INICIAR_HOME_PAGE = 0;
    public static int INICIAR_LIQUIDACIONES = 1;
    public static int INICIAR_EXCURSIONES_SALIENDO = 2;


    public enum Filtrar{
        FECHA_CONFECCION,
        FECHA_EXCURSION
    }

    public enum Estado{
        EDITAR,
        NUEVO
    }

    public enum FormatoFecha{
        MOSTRAR,
        ALMACENAR
    }
}
