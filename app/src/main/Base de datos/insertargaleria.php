<?php

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$id = $_GET["IDUSUARIO"];
$path= $_GET["PATH"];
$uuid= $_GET["UUIDUNIQUE"];


  $sql = "INSERT INTO GALERIA(IDUSUARIO, PATH, UUIDUNIQUE) VALUES ('$id', '$path', '$uuid')";

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