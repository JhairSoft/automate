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

    private static final Charset ORIGEN_CHARSET = Charset.forName("Windows-1252");
    private static final Charset DESTINO_CHARSET = StandardCharsets.UTF_8;
    private static final String LINE_SEPARATOR = "\n"; // Forzar salto de línea LF

    public void convertirAUTF8(Path origen, Path destino) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(origen.toFile()), ORIGEN_CHARSET));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino.toFile()), DESTINO_CHARSET))
        ) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                writer.write(linea);
                writer.write(LINE_SEPARATOR); // Salto de línea explícito
                System.out.println("Se generó el archivo con éxito");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al convertir CSV a UTF-8: " + e.getMessage(), e);
        }
    }
}
