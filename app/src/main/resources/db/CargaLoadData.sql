-- 2.- CARGAR DATOS DE INCIDENTES A LA TABLA SERVNOW.REPORTE
LOAD DATA LOCAL INFILE 'C:/SERVNOW/INC.csv'
-- LOAD DATA LOCAL INFILE 'C:/Jhair/Inetum/Reporte_General_mysql/LOADFILE/INC_202401_202503.csv'
REPLACE
INTO TABLE SERVNOW.REPORTE
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(
    Numero,
    Estado,
    Prioridad,
    Grupo_de_asignacion,
    Asignado_a,
    Nombre_Resolutor,
    Resuelto_por,
    Squad,
    Abierto_por,
    Solicitante,
    Provisto_para,
    @Abierto,
    @Creado_el,
    @Atendido,
    @Cerrado,
    Duracion,
    Categoria,
    Subcategoria_1,
    Subcategoria_2,
    Subcategoria_3,
    Articulo_de_Conocimiento,
    Codigo_de_resolucion,
    Origen_del_incidente,
    Linea_de_negocio,
    Tipo_de_intermediario,
    Recursos_Afectados,
    Ramo,
    Tipo_de_canal,
	@Duracion_de_atencion,
    Problema,
    Aplicacion,
    Cierre_de_Mes,
    Activo,
    Cerrado_por,
    Priorizado,
    Tipo_de_escalamiento
)
SET
    Abierto = CASE 
               WHEN @Abierto = '' THEN NULL 
               ELSE DATE(STR_TO_DATE(@Abierto, '%d-%m-%Y %H:%i:%s'))
           END,
    Creado_el = CASE 
               WHEN @Creado_el = '' THEN NULL 
               ELSE DATE(STR_TO_DATE(@Creado_el, '%d-%m-%Y %H:%i:%s'))
           END,
    Atendido = CASE 
               WHEN @Atendido = '' THEN NULL 
               ELSE DATE(STR_TO_DATE(@Atendido, '%d-%m-%Y %H:%i:%s'))
           END,
    Cerrado = CASE 
               WHEN @Cerrado = '' THEN NULL 
               ELSE DATE(STR_TO_DATE(@Cerrado, '%d-%m-%Y %H:%i:%s'))
           END,
	Duracion_de_atencion = CASE 
                               WHEN @Duracion_de_atencion = '' THEN NULL 
                               ELSE @Duracion_de_atencion 
                           END;
    

-- 3.- CARGAR DATOS DE REQUERIMIENTOS A LA TABLA SERVNOW.REPORTE
LOAD DATA LOCAL INFILE 'C:/SERVNOW/RITM.csv'
-- LOAD DATA LOCAL INFILE 'C:/Jhair/Inetum/Reporte_General_mysql/LOADFILE/RITM_202401_202503.csv'
REPLACE
INTO TABLE SERVNOW.REPORTE
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(
    Numero,
    Estado,
    Prioridad,
    Grupo_de_asignacion,
    Asignado_a,
    Nombre_Resolutor,
    Squad,
    Abierto_por,
    Solicitante,
    @Abierto,
    @Creado_el,
    @Atendido,
    @Cerrado,
    Duracion,
    Categoria,
    Subcategoria_1,
    Subcategoria_2,
    Subcategoria_3,
    Articulo_de_Conocimiento,
    Linea_de_negocio,
    Tipo_de_intermediario,
    Ramo,
    Tipo_de_canal,
    @Duracion_de_atencion,
    Aplicacion,
    Activo,
    Cerrado_por,
    Priorizado,
    Tipo_de_escalamiento
)
SET
    Abierto = CASE 
               WHEN @Abierto = '' THEN NULL 
               ELSE DATE(STR_TO_DATE(@Abierto, '%d-%m-%Y %H:%i:%s'))
           END,
    Creado_el = CASE 
               WHEN @Creado_el = '' THEN NULL 
               ELSE DATE(STR_TO_DATE(@Creado_el, '%d-%m-%Y %H:%i:%s'))
           END,
    Atendido = CASE 
               WHEN @Atendido = '' THEN NULL 
               ELSE DATE(STR_TO_DATE(@Atendido, '%d-%m-%Y %H:%i:%s'))
           END,
    Cerrado = CASE 
               WHEN @Cerrado = '' THEN NULL 
               ELSE DATE(STR_TO_DATE(@Cerrado, '%d-%m-%Y %H:%i:%s'))
           END,
	Duracion_de_atencion = CASE 
                               WHEN @Duracion_de_atencion = '' THEN NULL 
                               ELSE @Duracion_de_atencion 
                           END;