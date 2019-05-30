﻿//BD INTERNA

CREATE TABLE UNIQUE_UUID(
  ID VARCHAR(30)
);
 
CREATE TABLE USUARIOS (
  ID INTEGER PRIMARY KEY autoincrement,
  FOTO VARCHAR(400),
  NOMBRE VARCHAR(100),
  APELLIDOS VARCHAR(100),
  GALERIA_ID INTEGER(6),
  DOMICILIO_ID INTEGER(6),
  TELEFONO_ID INTEGER(6),
  EMAIL VARCHAR(100)
);

CREATE TABLE GALERIA (
  ID INTEGER(6),
  PATH VARCHAR(300),
  PRIMARY KEY (ID, PATH),
  FOREIGN KEY (ID) REFERENCES USUARIO(GALERIA_ID) ON DELETE CASCADE
);

CREATE TABLE DOMICILIO(
  ID INTEGER(6),
  DIRECCION VARCHAR(200),
  FOREIGN KEY (ID) REFERENCES USUARIO(DOMICILIO_ID) ON DELETE CASCADE
);

CREATE TABLE TELEFONO(
  ID INTEGER(6),
  NUMERO VARCHAR(15),
  FOREIGN KEY (ID) REFERENCES USUARIO(TELEFONO_ID) ON DELETE CASCADE
);