package reporte.sn.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ConversorCSV {
    public void convertirAUTF8(Path origen, Path destino) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(origen.toFile()), Charset.forName("Windows-1252")));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino.toFile()), StandardCharsets.UTF_8))
        ) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al convertir CSV a UTF-8: " + e.getMessage());
        }
    }
}