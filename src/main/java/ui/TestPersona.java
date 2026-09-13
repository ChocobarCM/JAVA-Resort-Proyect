package ui;

import data.DataPersona;
import Entities.Persona;
import java.util.LinkedList;

public class TestPersona {

    public static void main(String[] args) {

        DataPersona dp = new DataPersona();

        // ==========================================
        // 1. AGREGAR PERSONA
        // ==========================================
        Persona p = new Persona();
        p.setDni(12345678);
        p.setNombre("Juan");
        p.setApellido("Perez");
        p.setEmail("juanperez@mail.com");

        dp.add(p);

        System.out.println("Persona agregada:");
        System.out.println("ID: " + p.getIdPersona());
        System.out.println("DNI: " + p.getDni());
        System.out.println("Nombre: " + p.getNombre() + " " + p.getApellido());
        System.out.println("Email: " + p.getEmail());

        // ==========================================
        // 2. OBTENER TODAS LAS PERSONAS
        // ==========================================
        System.out.println("\n--- Todas las personas ---");
        LinkedList<Persona> personas = dp.getAll();

        for (Persona persona : personas) {
            System.out.println(
                persona.getIdPersona() + " | " +
                persona.getDni() + " | " +
                persona.getNombre() + " " + persona.getApellido() + " | " +
                persona.getEmail()
            );
        }

        // ==========================================
        // 3. BUSCAR POR DNI
        // ==========================================
        Persona personaBuscar = new Persona();
        personaBuscar.setDni(12345678);

        Persona encontrada = dp.getByDni(personaBuscar);

        System.out.println("\n--- Buscar por DNI ---");
        if (encontrada != null) {
            System.out.println("Persona encontrada:");
            System.out.println("ID: " + encontrada.getIdPersona());
            System.out.println("DNI: " + encontrada.getDni());
            System.out.println("Nombre: " + encontrada.getNombre() + " " + encontrada.getApellido());
            System.out.println("Email: " + encontrada.getEmail());
        } else {
            System.out.println("No se encontró la persona.");
        }
    }
}