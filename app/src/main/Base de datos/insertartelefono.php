<?php

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$id = $_GET["ID"];
$numero= $_GET["NUMERO"];
$uuid= $_GET["UUIDUNIQUE"];


  $sql = "INSERT INTO TELEFONO(ID, NUMERO, UUIDUNIQUE) VALUES ('$id', '$numero', '$uuid')";

mysqli_set_charset($conexion, "utf8"); //formato de datos utf8
if (mysqli_query($conexion, $sql)) {
      echo "OK";
} else {
      echo "ERROR";
}

//desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");

?>