package reporte.sn.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import reporte.sn.config.Config;

public class OrquestadorCarga {

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
            Config.get("db.url"),
            Config.get("db.usuario"),
            Config.get("db.clave")
        );
    }

    public void ejecutar() {
        try (Connection conn = conectar()) {
            System.out.println("2 -> Iniciando Proceso de Base de Datos.");
            limpiarTablas(conn);
            cargarArchivos(conn);
            ejecutarETL(conn);
        } catch (Exception e) {
            throw new RuntimeException("Error en la orquestación: " + e.getMessage(), e);
        }
    }

    private void limpiarTablas(Connection conn) throws SQLException {
        try (CallableStatement stmt = conn.prepareCall("{call servnow.Depurar_Reporte()}")) {
            stmt.execute();
            System.out.println("    2.1 -> Limpieza de tablas ejecutada.");
        }
    }

    private void cargarArchivos(Connection conn) {
        try (InputStream input = Config.getFile("db.script.carga");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            StringBuilder sentencia = new StringBuilder();
            String linea;

            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("--")) continue;

                sentencia.append(linea).append(" ");
                if (linea.endsWith(";")) {
                    ejecutarCarga(conn, sentencia.toString().trim());
                    sentencia.setLength(0);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo SQL de carga", e);
        }
    }

    private void ejecutarCarga(Connection conn, String sql) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("    2.2 -> Sentencia LOAD DATA ejecutada.");
        } catch (SQLException e) {
            throw new RuntimeException("Error al ejecutar sentencia:\n" + sql, e);
        }
    }

    private void ejecutarETL(Connection conn) throws SQLException {
        try (CallableStatement stmt = conn.prepareCall("{call servnow.OrquestarETL()}")) {
            stmt.execute();
            System.out.println("    2.3 -> ETL ejecutada.");
        }
    }
}
