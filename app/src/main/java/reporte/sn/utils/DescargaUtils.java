package reporte.sn.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DescargaUtils {

    public static Set<String> snapshotArchivosExistentes(Path carpeta) {
        // Validar existencia de la carpeta y crear si no existe
        if (!Files.exists(carpeta)) {
            try {
                Files.createDirectories(carpeta);
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear la carpeta: " + carpeta.toString());
            }
        }
        // Valida los archivos existentes en esta carpeta y los convierte a Strings
        try (Stream<Path> archivos = Files.list(carpeta)) {
            return archivos
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Error al listar archivos existentes: " + carpeta.toString());
        }
    }

    public static Optional<Path> esperarNuevoArchivo(Path carpeta, String extension, Set<String> existentes,
            Duration timeout) {
        long inicio = System.currentTimeMillis();
        long limite = timeout.toMillis();

        while ((System.currentTimeMillis() - inicio) < limite) {
            try (Stream<Path> archivos = Files.list(carpeta)) {
                Optional<Path> nuevo = archivos
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(extension))
                        .filter(p -> !existentes.contains(p.getFileName().toString()))
                        .findFirst();

                if (nuevo.isPresent()) {
                    System.out.println("    1.5 -> Archivo Descargado.");
                    return nuevo;
                }

                Thread.sleep(1000);
            } catch (IOException | InterruptedException ignored) {
            }
        }

        return Optional.empty();
    }
}