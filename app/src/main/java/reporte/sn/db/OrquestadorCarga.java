package reporte.sn.db;

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
            limpiarTablas(conn);
            //cargarArchivos(conn);
            //ejecutarETL(conn);
            System.out.println("Proceso completo ejecutado correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error en la orquestación: " + e.getMessage(), e);
        }
    }

    private void limpiarTablas(Connection conn) throws SQLException {
        try (CallableStatement stmt = conn.prepareCall("{call servnow.Depurar_Reporte()}")) {
            stmt.execute();
            System.out.println("Limpieza de tablas ejecutada.");
        }
    }

    private void cargarArchivos(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("LOAD DATA LOCAL INFILE 'C:/SERVNOW/INC.csv' INTO TABLE tabla_inc FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\\n' IGNORE 1 LINES;");
            stmt.execute("LOAD DATA LOCAL INFILE 'C:/SERVNOW/RITM.csv' INTO TABLE tabla_ritm FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\\n' IGNORE 1 LINES;");
            System.out.println("Archivos INC y RITM cargados.");
        }
    }

    private void ejecutarETL(Connection conn) throws SQLException {
        try (CallableStatement stmt = conn.prepareCall("{call servnow.OrquestarETL()}")) {
            stmt.execute();
            System.out.println("ETL ejecutada.");
        }
    }
}