package com.globalpaysolutions.yovendosaldo.customs;

/**
 * Created by Geovanni on 22/03/2016.
 */
public class ItemDrawerViewModel
{
    private String Titulo;
    private int Icono;

    public int getIcono()
    {
        return Icono;
    }

    public String getTitulo()
    {
        return Titulo;
    }

    public void setTitulo(String titulo)
    {
        Titulo = titulo;
    }

    public void setIcono(int icono)
    {
        Icono = icono;
    }

    public ItemDrawerViewModel(String pTitulo, int pIcono)
    {
        this.Titulo = pTitulo;
        this.Icono = pIcono;
    }

}
