package aplicacion.contactos.com.miscontactos;

import java.util.Comparator;

class CompararNombre implements Comparator<Contacto>{

    @Override
    public int compare(Contacto customer1, Contacto customer2) {
        int Nombres = customer1.getNombre().compareTo(customer2.getNombre());
        int Apellidos = customer1.getApellidos().compareTo(customer2.getApellidos());
        if (Nombres == 0) {
            return ((Apellidos == 0) ? Nombres : Apellidos);
        } else {
            return Nombres;
        }
    }
}

class CompararApellido implements Comparator<Contacto>{
    @Override
    public int compare(Contacto customer1, Contacto customer2) {

        int Nombres = customer1.getNombre().compareTo(customer2.getNombre());
        int Apellidos = customer1.getApellidos().compareTo(customer2.getApellidos());

        //En el caso de no tener Apellidos (null) se vaya al fondo
        if (customer1.getApellidos().length()==0){ Apellidos = 1; }
        if (customer2.getApellidos().length()==0){ Apellidos = -1; }

        //En el caso de ser idéntido, que mire el nombre
        if (Apellidos == 0) {
            return ((Nombres == 0) ? Apellidos : Nombres);
        } else {
            return Apellidos;
        }
    }
}

class CompararDomicilio implements Comparator<Contacto> {
    @Override
    public int compare(Contacto customer1, Contacto customer2) {
        int Nombres = customer1.getNombre().compareTo(customer2.getNombre());
        int Domicilio;
        String a1, a2;
        try {
            a1 = customer1.getDomicilios().get(0).getDireccion();
            if (a1.length() == 0 || a1.isEmpty() || a1 == null || a1.equals(" ")) {
                a1 = "";
            }
        } catch (IndexOutOfBoundsException e) {
            a1 = "";
        }
        try {
            a2 = customer2.getDomicilios().get(0).getDireccion();
            if (a2.length() == 0 || a2.isEmpty() || a2 == null || a2.equals(" ")) {
                a2 = "";
            }
        } catch (IndexOutOfBoundsException e) {
            a2 = "";
        }

        Domicilio = a1.compareTo(a2);


        //En el caso de no tener Domicilios (null) se vaya al fondo
        if (a1.length()==0){ Domicilio=1; }
        if (a2.length()==0){ Domicilio = -1; }

        if (Nombres == 0) {
            return ((Nombres == 0) ? Domicilio : Nombres);

        } else {
            return Domicilio;
        }
    }
}

class CompararTelefono implements Comparator<Contacto> {
    @Override
    public int compare(Contacto customer1, Contacto customer2) {
        int Nombres = customer1.getNombre().compareTo(customer2.getNombre());
        int Telefono;
        String a1, a2;
        try {
            a1 = customer1.getTelefonos().get(0).getNumero();
            if (a1.length() == 0 || a1.isEmpty() || a1 == null || a1.equals(" ")) {
                a1 = "";
            }
        } catch (IndexOutOfBoundsException e) {
            a1 = "";
        }
        try {
            a2 = customer2.getTelefonos().get(0).getNumero();
            if (a2.length() == 0 || a2.isEmpty() || a2 == null || a2.equals(" ")) {
                a2 = "";
            }
        } catch (IndexOutOfBoundsException e) {
            a2 = "";
        }
        Telefono = a1.compareTo(a2);

        //En el caso de no tener Telefonos (null) se vaya al fondo
        if (a1.length()==0){ Telefono=1;   }
        if (a2.length()==0){ Telefono = -1;}

        if (Nombres == 0) {
            return ((Nombres == 0) ? Telefono : Nombres);

        } else {
            return Telefono;
        }
    }
}
