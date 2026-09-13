package com.libreriasmart;

import com.libreriasmart.repository.Database;
import com.libreriasmart.ui.AppMenu;
import com.libreriasmart.ui.ConsoleUI;

/**
 * Punto de entrada. Inicializa la BD en listas y lanza el menú.
 *
 * Compilar:  javac -d out $(find src -name "*.java")  (Linux/Mac)
 *            Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } | javac -d out -encoding UTF8 @-
 * Ejecutar:  java -cp out com.libreriasmart.Main
 */
public class Main {
    public static void main(String[] args) {
        Database.getInstancia(); // carga seed hardcodeado
        ConsoleUI.limpiar();
        new AppMenu().iniciar();
    }
}
