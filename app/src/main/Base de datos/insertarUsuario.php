<?php

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$nombre = $_GET["NOMBRE"];
$email= $_GET["EMAIL"];
$foto= $_GET["FOTO"];
$uuid= $_GET["UUIDUNIQUE"];


  $sql = "INSERT INTO USUARIOS_GALERIA (NOMBRE, EMAIL, FOTO, UUIDUNIQUE) VALUES ('$nombre', '$email', '$foto', '$uuid')";

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